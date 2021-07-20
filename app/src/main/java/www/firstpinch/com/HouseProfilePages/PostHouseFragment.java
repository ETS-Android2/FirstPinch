package www.firstpinch.com.firstpinch2.HouseProfilePages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedObject;
import www.firstpinch.com.firstpinch2.MyLinearLayoutManager;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 20-10-2016.
 */

//posts fragment in house page
public class PostHouseFragment extends Fragment {

    public static String house = "", GET_POST_HOUSE_FEED_URL = "http://54.169.84.123/api/getClubStories",
            house_id="",house_name="";
    private RecyclerView recyclerView;
    private PostHouseFragmentAdapter recyclerAdapter;
    List<MainFeedObject> data = new ArrayList<MainFeedObject>();
    List<MainFeedObject> newdata = new ArrayList<MainFeedObject>();
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout ll_empty_recyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.post_house_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                data.clear();
                recyclerAdapter.notifyDataSetChanged();
                page_index = 1;
                get_feed(1);
                //swipeRefreshLayout.setRefreshing(false);
            }
        });
        SharedPreferences sp = getActivity().getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        SharedPreferences sp1 = getActivity().getSharedPreferences("house_id",
                Activity.MODE_PRIVATE);
        house_id = sp1.getString("house_id", "1");
        house_name = sp1.getString("house_name", "1");
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) view.findViewById(R.id.house_recyclerview);
        ll_empty_recyclerview = (LinearLayout) view.findViewById(R.id.ll_empty_recyclerview);
        //Button btn_go = (Button) findViewById(R.id.btn_go);
        //selectedHouses = new SelectedInterestsObject();
        recyclerAdapter = new PostHouseFragmentAdapter(getActivity(), data);
        recyclerView.setAdapter(recyclerAdapter);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(null);
        //scroll listener to send API request again
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (llm.findLastVisibleItemPosition() == recyclerAdapter.getItemCount() - 3 && !loading) {
                    //bottom of list!
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

    //called when feed item deleted from house page activity
    public void get_feed_deleted(int id) {
        try {
            for(int i =0;i<data.size();i++){
                MainFeedObject current = data.get(i);
                if(current.getMainfeed_question_id().contentEquals(id+"")){
                    data.remove(i);
                    recyclerAdapter.notifyItemRemoved(i);
                    return;
                }
            }
        } catch(Exception e){
            Log.e("MAinfeedDeleted","PostProfileRecyclerView");
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(getActivity(), e.toString());
        }
    }

    //get house post data API request
    private void get_feed(final int page_index) {

        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_POST_HOUSE_FEED_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray feed = jsonObj.getJSONArray("feed");
                            for (int i = 0; i < feed.length(); i++) {
                                MainFeedObject current = new MainFeedObject();
                                current.setHouseName(house_name);
                                JSONObject feedBlock = feed.getJSONObject(i);
                                JSONObject block = feedBlock.getJSONObject("feedblock");
                                String type = block.getString("type");
                                JSONObject obj_user = block.getJSONObject("user");
                                current.setMainfeed_user_id(obj_user.getInt("user_id"));
                                current.setProfileName(obj_user.getString("user_name"));
                                current.setProfileImageUrl(obj_user.getString("user_image"));
                                if (type.contentEquals("story")) {
                                    JSONObject obj_story = block.getJSONObject("story");
                                    JSONArray arr_contents = obj_story.getJSONArray("contents");
                                    for (int j = 0; j < arr_contents.length(); j++) {
                                        JSONObject obj_images = arr_contents.getJSONObject(j);
                                        current.arr_images.add(obj_images.getString("content"));
                                    }
                                    JSONArray arr_comments = obj_story.getJSONArray("comment");
                                    for (int j = 0; j < arr_comments.length(); j++) {
                                        JSONObject obj_comments = arr_comments.getJSONObject(j);
                                        if(j==0) {
                                            current.setHint_comm_profile_id(obj_comments.getString("user_id"));
                                            current.setHint_comm_profile_username(obj_comments.getString("user_uname"));
                                            current.setHint_comm_profile_name(obj_comments.getString("user_name"));
                                            current.setHint_comm_profile_image(obj_comments.getString("user_image"));
                                            current.setHint_comm_title(obj_comments.getString("comment"));
                                        } else {
                                            current.setHint_comm_profile_id2(obj_comments.getString("user_id"));
                                            current.setHint_comm_profile_username2(obj_comments.getString("user_uname"));
                                            current.setHint_comm_profile_name2(obj_comments.getString("user_name"));
                                            current.setHint_comm_profile_image2(obj_comments.getString("user_image"));
                                            current.setHint_comm_title2(obj_comments.getString("comment"));
                                        }
                                    }
                                    current.setPreview_title(obj_story.getString("preview_title"));
                                    current.setPreview_description(obj_story.getString("preview_description"));
                                    current.setPreview_image(obj_story.getString("preview_image"));
                                    current.setPreview_link(obj_story.getString("preview_link"));
                                    current.setNum_of_images(current.arr_images.size());
                                    //Log.e("arr_contents=", "" + arr_contents);
                                    current.setMainfeed_question_id(obj_story.getString("id"));
                                    current.setTitle(obj_story.getString("title"));
                                    current.setDesc(obj_story.getString("description"));
                                    current.setAppreciations_count(obj_story.getInt("appreciation_count"));
                                    current.setUser_rate(obj_story.getInt("apprection_star"));
                                    current.setScore(obj_story.getDouble("score"));
                                    current.setResponses(obj_story.getInt("comment_count"));
                                    current.setBookmark_id(obj_story.getString("bookmark_id"));
                                    current.setBookmark_status(obj_story.getString("bookmark_status"));
                                    current.setPost_ques(0);

                                } else {
                                    JSONObject obj_story = block.getJSONObject("question");
                                    current.setTitle(obj_story.getString("title"));
                                    current.setPreview_title(obj_story.getString("preview_title"));
                                    current.setPreview_description(obj_story.getString("preview_description"));
                                    current.setPreview_image(obj_story.getString("preview_image"));
                                    current.setPreview_link(obj_story.getString("preview_link"));
                                    //current.setDesc(obj_story.getString("description"));
                                    current.setAppreciations_count(obj_story.getInt("appreciation_count"));
                                    //current.setScore(obj_story.getDouble("score"));
                                    //current.setResponses(obj_story.getInt("comment_count"));
                                    current.setPost_ques(1);
                                }
                                //JSONObject obj_comment = block.getJSONObject("comment");


                                newdata.add(current);

                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(getActivity(), e.toString());
                        }
                        if (page_index == 1) {
                            try {
                                //progressDialog.dismiss();
                                swipeRefreshLayout.setRefreshing(false);
                            } catch (Exception e) {
                                Log.e("MainFeedRecyclerView", "progressDialog" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(getActivity(), e.toString());
                            }
                            data.addAll(0,newdata);
                            recyclerAdapter.notifyItemRangeInserted(0, newdata.size());
                            newdata.clear();
                        } else {
                            data.addAll(data.size(),newdata);
                            recyclerAdapter.notifyItemRangeInserted(data.size(), newdata.size());
                            newdata.clear();
                        }
                        if(data.isEmpty()){
                            swipeRefreshLayout.setVisibility(View.GONE);
                            ll_empty_recyclerview.setVisibility(View.VISIBLE);
                        } else {
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            ll_empty_recyclerview.setVisibility(View.GONE);
                        }
                        loading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        if (page_index == 1)
                            swipeRefreshLayout.setRefreshing(false);
                        //progressDialog.dismiss();
                        loading = false;
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders()  {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Page-Index", String.valueOf(page_index));
                headers.put("User-Id", String.valueOf(user_id));
                headers.put("Club-Id", house_id);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        if (page_index == 1) {
            /*progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading Club Content...");
            progressDialog.show();*/
            swipeRefreshLayout.setRefreshing(true);
        }

    }
}