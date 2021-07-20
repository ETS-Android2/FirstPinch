package www.firstpinch.com.firstpinch2.NotificationPages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.MyLinearLayoutManager;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 13-09-2016.
 */

//house notification fragment
public class HouseNotificationsFragment extends Fragment {

    String GET_HOUSE_NOTIFICATIOINS_URL = "http://54.169.84.123/api/getClubNotification";
    private RecyclerView recyclerView;
    private HouseNotificationsRecyclerViewAdapter recyclerAdapter;
    List<HouseNotificationsObject> data = new ArrayList<HouseNotificationsObject>();
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout,swipeRefreshLayout2;
    LinearLayout ll_empty_recyclerview;
    TextView tv_empty_recyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.house_notifications_recyclerview, container, false);
        Log.i("Unit Testing","HouseNotificationsFragment oncreate");
        SharedPreferences sp = getActivity().getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout2 = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout2);
        tv_empty_recyclerview = (TextView) view.findViewById(R.id.tv_empty_recyclerview);
        //called when No data present
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                data.clear();
                page_index = 1;
                get_feed(1);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //called when there is an error from server (pull down to refresh)
        swipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                data.clear();
                page_index = 1;
                get_feed(1);
                swipeRefreshLayout2.setRefreshing(false);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.housee_notificationsh_recyclerview);
        ll_empty_recyclerview = (LinearLayout) view.findViewById(R.id.ll_empty_recyclerview);
        recyclerAdapter = new HouseNotificationsRecyclerViewAdapter(getActivity(), data);
        recyclerView.setAdapter(recyclerAdapter);
        final MyLinearLayoutManager llm = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        //scroll listeners to request API again
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if ((llm.findLastVisibleItemPosition() == recyclerAdapter.getItemCount() - 3||llm.findLastVisibleItemPosition() == recyclerAdapter.getItemCount() - 4) && !loading) {
                    //bottom of list!
                    //Log.e("position", "" + llm.findLastCompletelyVisibleItemPosition());
                    Log.e("house notif position", "" + llm.findLastVisibleItemPosition());
                    page_index++;
                    get_feed(page_index);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        get_feed(1);
        return view;
    }

    //to refresh housenotification data
    public void refresh() {
        data.clear();
        page_index = 1;
        get_feed(1);
    }

    //get data of house notification API request
    private void get_feed(final int page_index) {

        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_HOUSE_NOTIFICATIOINS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray feed = jsonObj.getJSONArray("club_notification_feed");
                            for (int i = 0; i < feed.length(); i++) {
                                HouseNotificationsObject current = new HouseNotificationsObject();
                                JSONObject block = feed.getJSONObject(i);
                                String type = block.getString("type");
                                current.setNotifType(type);
                                if (type.contentEquals("StoryPost")) {
                                    current.setNotifhpost_question_response("Post");
                                    current.setNotifhPost_Question(0);
                                    current.setNotifhUserName(block.getString("story_user_name"));
                                    current.setNotifhProfileImageUrl(block.getString("story_user_image"));
                                    current.setNotifhText(block.getString("story_user_name") + " has pinched a Post "
                                            + " in "
                                            +block.getString("story_club_name"));
                                    current.setNotifhTitle("\"" + block.getString("story_title") + "\"");
                                    current.setNotifhId(block.getString("story_id"));
                                }else if (type.contentEquals("QuestionPost")) {
                                    current.setNotifhpost_question_response("Question");
                                    current.setNotifhPost_Question(1);
                                    current.setNotifhUserName(block.getString("question_user_name"));
                                    current.setNotifhProfileImageUrl(block.getString("question_user_image"));
                                    current.setNotifhText(block.getString("question_user_name")+" has pinched a Question "
                                    + " in "
                                            +block.getString("question_club_name"));
                                    current.setNotifhTitle("\"" + block.getString("question_title") + "\"");
                                    current.setNotifhId(block.getString("question_id"));
                                }else if (type.contentEquals("comment")) {
                                    JSONObject rating_user, rating_house, rating_details;
                                    current.setNotifhpost_question_response("Post");
                                    current.setNotifhPost_Question(0);
                                    rating_house = block.getJSONObject("comment_club");
                                    rating_user = block.getJSONObject("comment_user");
                                    current.setNotifhUserName(rating_user.getString("comment_user_name"));
                                    current.setNotifhProfileImageUrl(rating_user.getString("comment_user_image"));
                                    rating_details = block.getJSONObject("comment");
                                    current.setNotifhText("<b>"+rating_user.getString("comment_user_name")+"</b>"+" has responded to the Post"
                                            + " in "
                                            +rating_house.getString("club_name"));
                                    current.setNotifhTitle("\"" + rating_details.getString("comment")+"\"");
                                    current.setNotifhId(rating_details.getString("story_id"));
                                } else if (type.contentEquals("answer")) {
                                    JSONObject rating_user,rating_details;
                                    current.setNotifhpost_question_response("Question");
                                    current.setNotifhPost_Question(1);
                                    rating_user = block.getJSONObject("answer_user");
                                    current.setNotifhUserName(rating_user.getString("answer_user_name"));
                                    current.setNotifhProfileImageUrl(rating_user.getString("answer_user_image"));
                                    rating_details = block.getJSONObject("answer");
                                    current.setNotifhText(rating_user.getString("answer_user_name")+" has answered to the Question ");
                                    current.setNotifhTitle("\""+rating_details.getString("question_title")+"\"");
                                    current.setNotifhId(rating_details.getString("question_id"));
                                }
                                data.add(current);
                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(getActivity(), e.toString());
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        if(data.isEmpty()){
                            swipeRefreshLayout.setVisibility(View.GONE);
                            swipeRefreshLayout2.setVisibility(View.VISIBLE);
                            tv_empty_recyclerview.setText("No Notifications");
                        } else {
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            swipeRefreshLayout2.setVisibility(View.GONE);
                        }
                        /*if (page_index == 1)
                            progressDialog.dismiss();*/
                        loading = false;
                        Log.i("Unit Testing","HouseNotificationsFragment data parsed");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(),"Check your Internet Connection and Try Again",Toast.LENGTH_SHORT).show();
                        Log.e("check", "check" + error);
                        if(data.isEmpty()){
                            swipeRefreshLayout.setVisibility(View.GONE);
                            swipeRefreshLayout2.setVisibility(View.VISIBLE);
                            tv_empty_recyclerview.setText("Pull down to refresh");
                        } else {
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            swipeRefreshLayout2.setVisibility(View.GONE);
                        }
                        /*if (page_index == 1)
                            progressDialog.dismiss();*/
                        loading = false;
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Page-Index", String.valueOf(page_index));
                headers.put("User-Id", String.valueOf(user_id));
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        /*if (page_index == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading Notifications...");
            progressDialog.show();
        }*/

    }

    //for static app (currently not used)
    public static List<HouseNotificationsObject> getData() {
        List<HouseNotificationsObject> data = new ArrayList<HouseNotificationsObject>();
        String notifhIndicatorImageUrlList[] = {"http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg"};
        String notifhProfileImageUrlList[] = {"http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/normal/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg"};
        String notifhTextList[] = {"Akash Mondal has appreciated Post My dream Destination", "Vishal Puri has appreciated Post My dream Destination",
                "Idhar b kuch b dala h\nadffxcvx\nsdfscsdc\nfsdcsdcvs\nsfsdcsdvfv",
                "Akash Mondal has appreciated Post My dream Destination", "Vishal Puri has appreciated Post My dream Destination",
                "Idhar b kuch b dala h\nadffxcvx\nsdfscsdc\nfsdcsdcvs\nsfsdcsdvfv"};

        for (int i = 0; i < notifhTextList.length; i++) {
            HouseNotificationsObject current = new HouseNotificationsObject();
            current.setNotifhIndicatiorImageUrl(notifhIndicatorImageUrlList[i]);
            current.setNotifhProfileImageUrl(notifhProfileImageUrlList[i]);
            current.setNotifhText(notifhTextList[i]);

            data.add(current);
        }
        return data;
    }
}