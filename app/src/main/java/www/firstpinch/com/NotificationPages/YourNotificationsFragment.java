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

//your notification fragment of notifications.java
public class YourNotificationsFragment extends Fragment {

    public static String house = "", GET_YOUR_NOTIFICATIOINS_URL = "http://54.169.84.123/api/getUserNotification";
    private RecyclerView recyclerView;
    private YourNotificationsRecyclerViewAdapter recyclerAdapter;
    List<YourNotificatioinsObject> data = new ArrayList<YourNotificatioinsObject>();
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    LinearLayout ll_empty_recyclerview;
    SwipeRefreshLayout swipeRefreshLayout, swipeRefreshLayout2;
    TextView tv_empty_recyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.your_notifications_recyclerview, container, false);
        Log.i("Unit Testing", "YourNotificationsFragment oncreate");
        SharedPreferences sp = getActivity().getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout2 = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout2);
        tv_empty_recyclerview = (TextView) view.findViewById(R.id.tv_empty_recyclerview);
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

        recyclerView = (RecyclerView) view.findViewById(R.id.your_notifications_recyclerview);
        ll_empty_recyclerview = (LinearLayout) view.findViewById(R.id.ll_empty_recyclerview);
        recyclerAdapter = new YourNotificationsRecyclerViewAdapter(getActivity(), data);
        recyclerView.setAdapter(recyclerAdapter);
        final MyLinearLayoutManager llm = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        //scroll listener to request data from API again
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (llm.findLastVisibleItemPosition() == recyclerAdapter.getItemCount() - 3 && !loading) {
                    //bottom of list!
                    //Log.e("position", "" + llm.findLastCompletelyVisibleItemPosition());
                    Log.e("position", "" + llm.findLastVisibleItemPosition());
                    page_index++;
                    get_feed(page_index);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        get_feed(1);
        return view;
    }

    //called to refresh your notifications data
    public void refresh() {
        data.clear();
        page_index = 1;
        get_feed(1);
    }

    //get yourNotifications data API request
    private void get_feed(final int page_index) {

        loading = true;
        try {
            requestQueue = Volley.newRequestQueue(getActivity());
        } catch (Exception e) {
            Log.e("YourNotificationFragmnt", "getFeed :" + e.toString());
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(getActivity(), e.toString());
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_YOUR_NOTIFICATIOINS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray feed = jsonObj.getJSONArray("user_notification_feed");
                            for (int i = 0; i < feed.length(); i++) {
                                YourNotificatioinsObject current = new YourNotificatioinsObject();
                                JSONObject block = feed.getJSONObject(i);
                                String type = block.getString("type");
                                current.setNotifType(type);
                                current.setUser_react("");
                                JSONObject rating_user = null;
                                if (type.contentEquals("storyRate")) {
                                    String user_react = block.getString("user_react");
                                    current.setUser_react(user_react);
                                    current.setNotifpost_question_response("Post");
                                    current.setNotifPost_Question(0);
                                    rating_user = block.getJSONObject("rating_user");
                                    current.setNotifUserName(rating_user.getString("rating_user_name"));
                                    current.setNotifProfileImageUrl(rating_user.getString("rating_user_image"));
                                    JSONObject rating_details = block.getJSONObject("rating_details");
                                    current.setNotifId(rating_details.getString("rating_story_id"));
                                    current.setNotifText(" appreciated your ");
                                    current.setNotifTitle("\"" + rating_details.getString("rating_story_title") + "\"");
                                } else if (type.contentEquals("questionRate")) {
                                    String user_react = block.getString("user_react");
                                    current.setUser_react(user_react);
                                    current.setNotifpost_question_response("Question");
                                    current.setNotifPost_Question(1);
                                    rating_user = block.getJSONObject("rating_user");
                                    current.setNotifUserName(rating_user.getString("rating_user_name"));
                                    current.setNotifProfileImageUrl(rating_user.getString("rating_user_image"));
                                    JSONObject rating_details = block.getJSONObject("rating_details");
                                    current.setNotifId(rating_details.getString("rating_question_id"));
                                    current.setNotifText(" appreciated your ");
                                    current.setNotifTitle("\"" + rating_details.getString("rating_question_title") + "\"");
                                } else if (type.contentEquals("comment")) {
                                    current.setNotifpost_question_response("Post");
                                    current.setNotifPost_Question(0);
                                    rating_user = block.getJSONObject("comment_user");
                                    current.setNotifUserName(rating_user.getString("comment_user_name"));
                                    current.setNotifProfileImageUrl(rating_user.getString("comment_user_image"));
                                    JSONObject rating_details = block.getJSONObject("comment");
                                    current.setNotifId(rating_details.getString("story_id"));
                                    current.setNotifText(" responded to your ");
                                    current.setNotifTitle("\"" + rating_details.getString("story_title") + "\"");
                                } else if (type.contentEquals("answer")) {
                                    current.setNotifpost_question_response("Question");
                                    current.setNotifPost_Question(1);
                                    rating_user = block.getJSONObject("answer_user");
                                    current.setNotifUserName(rating_user.getString("answer_user_name"));
                                    current.setNotifProfileImageUrl(rating_user.getString("answer_user_image"));
                                    JSONObject rating_details = block.getJSONObject("answer");
                                    current.setNotifId(rating_details.getString("question_id"));
                                    current.setNotifText(" answered your ");
                                    current.setNotifTitle("\"" + rating_details.getString("question_title") + "\"");
                                } else if (type.contentEquals("commenttag")) {
                                    current.setNotifpost_question_response("Response");
                                    current.setNotifPost_Question(0);
                                    rating_user = block.getJSONObject("tag_user");
                                    current.setNotifUserName(rating_user.getString("tag_user_name"));
                                    current.setNotifProfileImageUrl(rating_user.getString("tag_user_image"));
                                    JSONObject rating_details = block.getJSONObject("comment");
                                    current.setNotifId(rating_details.getString("story_id"));
                                    current.setNotifText(" tagged you in ");
                                    current.setNotifTitle("\"" + rating_details.getString("comment") + "\"");
                                } else if (type.contentEquals("answertag")) {
                                    current.setNotifpost_question_response("Answer");
                                    current.setNotifPost_Question(1);
                                    rating_user = block.getJSONObject("vote_user");
                                    current.setNotifUserName(rating_user.getString("vote_user_name"));
                                    current.setNotifProfileImageUrl(rating_user.getString("vote_user_image"));
                                    JSONObject rating_details = block.getJSONObject("answer");
                                    current.setNotifId(rating_details.getString("question_id"));
                                    current.setNotifText(" tagged you in an ");
                                    current.setNotifTitle("\"" + rating_details.getString("answer") + "\"");
                                } else if (type.contentEquals("commentvote")) {
                                    current.setNotifpost_question_response("Response");
                                    current.setNotifPost_Question(0);
                                    rating_user = block.getJSONObject("comment_user");
                                    current.setNotifUserName(rating_user.getString("vote_user_name"));
                                    current.setNotifProfileImageUrl(rating_user.getString("vote_user_image"));
                                    JSONObject rating_details = block.getJSONObject("comment");
                                    current.setNotifId(rating_details.getString("story_id"));
                                    current.setNotifText(" appreciated your ");
                                    current.setNotifTitle("\"" + rating_details.getString("comment") + "\"");
                                } else if (type.contentEquals("answervote")) {
                                    current.setNotifpost_question_response("Answer");
                                    current.setNotifPost_Question(1);
                                    rating_user = block.getJSONObject("vote_user");
                                    current.setNotifUserName(rating_user.getString("vote_user_name"));
                                    current.setNotifProfileImageUrl(rating_user.getString("vote_user_image"));
                                    JSONObject rating_details = block.getJSONObject("answer");
                                    current.setNotifId(rating_details.getString("question_id"));
                                    current.setNotifText(" appreciated your ");
                                    current.setNotifTitle("\"" + rating_details.getString("answer") + "\"");
                                }
                                data.add(current);

                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(getActivity(), e.toString());
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        if (data.isEmpty()) {
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
                        Log.i("Unit Testing", "YourNotificationsFragment data parsed");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        Toast.makeText(getActivity(), "Check your Internet Connection and Try Again", Toast.LENGTH_SHORT).show();
                        if (data.isEmpty()) {
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

}
