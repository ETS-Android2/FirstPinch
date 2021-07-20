package www.firstpinch.com.firstpinch2.MainFeed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
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
import www.firstpinch.com.firstpinch2.R;
import www.firstpinch.com.firstpinch2.SQLite.DatabaseHandler;

/**
 * Created by Rianaa Admin on 08-09-2016.
 */

//MainFeed fragment from Home.java
public class MainFeedRecyclerView extends Fragment {

    public static String house = "", GET_MAIN_FEED_URL = "http://54.169.84.123/api/getHomeFeed";
    int count = 0;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    public MainFeedRecyclerViewAdapter recyclerAdapter;
    public List<MainFeedObject> data = new ArrayList<MainFeedObject>();
    public List<MainFeedObject> newdata = new ArrayList<MainFeedObject>();
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;
    DatabaseHandler db;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_feed_recyclerview, container, false);
        Log.i("Unit Testing", "MainFeedRecyclerView oncreate");
        Log.i("Unit Testing", "MainFeedRecyclerView oncreate");

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        context = getActivity();
        db = new DatabaseHandler(getActivity());
        db.onOpen(db.getWritableDatabase());

        SharedPreferences sp = getActivity().getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        recyclerView = (RecyclerView) view.findViewById(R.id.house_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        //called when swipeDown to refresh feed
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                if (!isNetworkAvailable()) {
                    swipeRefreshLayout.setRefreshing(false);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), 0);
                    alertDialogBuilder
                            .setMessage("No internet connection on your device. Would you like to enable it?")
                            .setTitle("No Internet Connection")
                            .setCancelable(false)
                            .setPositiveButton(" Enable Internet ",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            getActivity().startActivity(dialogIntent);
                                        }
                                    });

                    alertDialogBuilder.setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                } else {
                    db.removeAll();
                    data.clear();
                    recyclerAdapter.notifyDataSetChanged();
                    page_index = 1;
                    get_feed(1);
                    //swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        if (!isNetworkAvailable()) {
            data = db.getAllFeedData();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), 0);
            alertDialogBuilder
                    .setMessage("No internet connection on your device. Would you like to enable it?")
                    .setTitle("No Internet Connection")
                    .setCancelable(false)
                    .setPositiveButton(" Enable Internet ",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getActivity().startActivity(dialogIntent);
                                }
                            });

            alertDialogBuilder.setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        } /*else {
            //db.removeAll();
        }*/
        recyclerAdapter = new MainFeedRecyclerViewAdapter(getActivity(), data);
        recyclerView.setAdapter(recyclerAdapter);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(null);
        registerForContextMenu(recyclerView);
        //scroll listener to request API again
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (llm.findLastVisibleItemPosition() == recyclerAdapter.getItemCount() - 5 && !loading) {
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

    //called when feed item is deleted from Home.java to remove it
    public void get_feed_deleted(int id) {
        try {
            for (int i = 0; i < data.size(); i++) {
                MainFeedObject current = data.get(i);
                if (current.getMainfeed_question_id().contentEquals(id + "")) {
                    data.remove(i);
                    recyclerAdapter.notifyItemRemoved(i);
                    return;
                }
            }
        } catch (Exception e) {
            Log.e("MAinfeedDeleted", "MAinFeedRecyclerView");
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(getActivity(), e.toString());
        }
    }

    //called to refresh feed
    public void get_feed_refreshed() {
        RequestQueue requestQueue = null;
        try {
            /*DatabaseHandler db;
            context = getActivity();
            db = new DatabaseHandler(getActivity());
            db.onOpen(db.getWritableDatabase());
            db.removeAll();*/
            data.clear();
            page_index = 1;
            loading = true;
            requestQueue = Volley.newRequestQueue(context);
            final DatabaseHandler finalDb = db;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_MAIN_FEED_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                JSONArray feed = jsonObj.getJSONArray("feed");
                                for (int i = 0; i < feed.length(); i++) {
                                    MainFeedObject current = new MainFeedObject();
                                    JSONObject feedBlock = feed.getJSONObject(i);
                                    JSONObject block = feedBlock.getJSONObject("feedblock");
                                    String type = block.getString("type");
                                    JSONObject obj_club = block.getJSONObject("club");
                                    current.setMainfeed_house_id(obj_club.getInt("club_id"));
                                    current.setHouseName(obj_club.getString("club_name"));
                                    current.setHouseDesc(obj_club.getString("club_descrition"));
                                    current.setHouseImageUrl(obj_club.getString("club_image"));
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
                                            if (j == 0) {
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
                                        current.setNum_of_images(current.arr_images.size());
                                        //Log.e("arr_contents=", "" + arr_contents);
                                        current.setMainfeed_question_id(obj_story.getString("id"));
                                        current.setTitle(obj_story.getString("title"));
                                        current.setDesc(obj_story.getString("description"));
                                        current.setAppreciations_count(obj_story.getInt("appreciation_count"));
                                        current.setScore(obj_story.getDouble("score"));
                                        current.setUser_rate(obj_story.getInt("apprection_star"));
                                        current.setResponses(obj_story.getInt("comment_count"));
                                        current.setBookmark_id(obj_story.getString("bookmark_id"));
                                        current.setBookmark_status(obj_story.getString("bookmark_status"));
                                        current.setPreview_title(obj_story.getString("preview_title"));
                                        current.setPreview_description(obj_story.getString("preview_description"));
                                        current.setPreview_image(obj_story.getString("preview_image"));
                                        current.setPreview_link(obj_story.getString("preview_link"));
                                        current.setPost_ques(0);

                                    } else {
                                        JSONObject obj_story = block.getJSONObject("question");
                                        current.setMainfeed_question_id(obj_story.getString("id"));
                                        current.setTitle(obj_story.getString("title"));
                                        current.setDesc(obj_story.getString("content"));
                                        current.setResponses(obj_story.getInt("answer_count"));
                                        current.setUser_rate(obj_story.getInt("apprection_star"));
                                        current.setAppreciations_count(obj_story.getInt("appreciation_count"));
                                        current.setBookmark_id(obj_story.getString("bookmark_id"));
                                        current.setBookmark_status(obj_story.getString("bookmark_status"));
                                        current.setPreview_title(obj_story.getString("preview_title"));
                                        current.setPreview_description(obj_story.getString("preview_description"));
                                        current.setPreview_image(obj_story.getString("preview_image"));
                                        current.setPreview_link(obj_story.getString("preview_link"));
                                        JSONArray arr_comments = obj_story.getJSONArray("answers");
                                        for (int j = 0; j < arr_comments.length(); j++) {
                                            JSONObject obj_comments = arr_comments.getJSONObject(j);
                                            if (j == 0) {
                                                current.setHint_comm_profile_id(obj_comments.getString("user_id"));
                                                current.setHint_comm_profile_username(obj_comments.getString("user_uname"));
                                                current.setHint_comm_profile_name(obj_comments.getString("user_name"));
                                                current.setHint_comm_profile_image(obj_comments.getString("user_image"));
                                                current.setHint_comm_title(obj_comments.getString("answer"));
                                            } else {
                                                current.setHint_comm_profile_id2(obj_comments.getString("user_id"));
                                                current.setHint_comm_profile_username2(obj_comments.getString("user_uname"));
                                                current.setHint_comm_profile_name2(obj_comments.getString("user_name"));
                                                current.setHint_comm_profile_image2(obj_comments.getString("user_image"));
                                                current.setHint_comm_title2(obj_comments.getString("answer"));
                                            }
                                        }
                                        if (obj_story.getString("image").contentEquals("/default_interset.jpg")) {
                                            current.setNum_of_images(0);
                                        } else {
                                            current.setNum_of_images(1);
                                            current.arr_images.add(obj_story.getString("image"));
                                        }
                                        //current.setScore(obj_story.getDouble("score"));
                                        //current.setResponses(obj_story.getInt("comment_count"));
                                        current.setPost_ques(1);
                                    }
                                    //JSONObject obj_comment = block.getJSONObject("comment");
                                    /*if (page_index < 2) {
                                        Log.e("Insert: ", "Inserting ..");
                                        finalDb.addFeedData(current);
                                    }*/

                                    data.add(current);

                                }
                            } catch (JSONException e) {
                                Log.e("LOG", "" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(getActivity(), e.toString());
                            }
                            recyclerAdapter.notifyDataSetChanged();
                            if (page_index == 1) {
                                //progressDialog.dismiss();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            loading = false;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(getActivity(), "Check your Internet Connection and Try Again", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(getActivity(), "AuthFailureError - Try Again", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                Toast.makeText(getActivity(), "Sorry! We couldn't connect with the server - Try Again", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(getActivity(), "NetworkError - Try Again", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                Toast.makeText(getActivity(), "ParseError - Try Again", Toast.LENGTH_SHORT).show();
                            } /*else {
                                Toast.makeText(getActivity(),"Check your Internet Connection and Try Again",Toast.LENGTH_SHORT).show();
                            }*/
                            Log.e("check", "check" + error);
                            if (page_index == 1)
                                swipeRefreshLayout.setRefreshing(false);
                            //progressDialog.dismiss();
                            loading = false;
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {

                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Page-Index", "1");
                    headers.put("User-Id", String.valueOf(user_id));
                    return headers;
                }

            };
            requestQueue.add(stringRequest);
            if (page_index == 1) {
                /*progressDialog = new ProgressDialog(getActivity());
                //progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading Feed...");
                progressDialog.show();*/
                swipeRefreshLayout.setRefreshing(true);
            }
        } catch (Exception e) {
            Log.e("MainFeedRecyclrView", "getFeedRefereshed" + e.toString());
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(getActivity(), e.toString());
        }

    }

    //get feed data API request
    private void get_feed(final int page_index) {
        try {
            loading = true;
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(getActivity());
                //Log.e("get feed", "Setting a new request queue");
            }

            StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_MAIN_FEED_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //db.removeAll();
                                JSONObject jsonObj = new JSONObject(response);
                                JSONArray feed = jsonObj.getJSONArray("feed");
                                for (int i = 0; i < feed.length(); i++) {
                                    MainFeedObject current = new MainFeedObject();
                                    JSONObject feedBlock = feed.getJSONObject(i);
                                    JSONObject block = feedBlock.getJSONObject("feedblock");
                                    String type = block.getString("type");
                                    JSONObject obj_club = block.getJSONObject("club");
                                    current.setMainfeed_house_id(obj_club.getInt("club_id"));
                                    current.setHouseName(obj_club.getString("club_name"));
                                    current.setHouseDesc(obj_club.getString("club_descrition"));
                                    current.setHouseImageUrl(obj_club.getString("club_image"));
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
                                            if (j == 0) {
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
                                        current.setScore(obj_story.getDouble("score"));
                                        current.setUser_rate(obj_story.getInt("apprection_star"));
                                        current.setResponses(obj_story.getInt("comment_count"));
                                        current.setBookmark_id(obj_story.getString("bookmark_id"));
                                        current.setBookmark_status(obj_story.getString("bookmark_status"));
                                        current.setPost_ques(0);

                                    } else {
                                        JSONObject obj_story = block.getJSONObject("question");
                                        current.setMainfeed_question_id(obj_story.getString("id"));
                                        current.setTitle(obj_story.getString("title"));
                                        current.setDesc(obj_story.getString("content"));
                                        current.setResponses(obj_story.getInt("answer_count"));
                                        current.setUser_rate(obj_story.getInt("apprection_star"));
                                        current.setAppreciations_count(obj_story.getInt("appreciation_count"));
                                        current.setScore(obj_story.getDouble("score"));
                                        current.setBookmark_id(obj_story.getString("bookmark_id"));
                                        current.setBookmark_status(obj_story.getString("bookmark_status"));
                                        current.setPreview_title(obj_story.getString("preview_title"));
                                        current.setPreview_description(obj_story.getString("preview_description"));
                                        current.setPreview_image(obj_story.getString("preview_image"));
                                        current.setPreview_link(obj_story.getString("preview_link"));
                                        JSONArray arr_comments = obj_story.getJSONArray("answers");
                                        for (int j = 0; j < arr_comments.length(); j++) {
                                            JSONObject obj_comments = arr_comments.getJSONObject(j);
                                            if (j == 0) {
                                                current.setHint_comm_profile_id(obj_comments.getString("user_id"));
                                                current.setHint_comm_profile_username(obj_comments.getString("user_uname"));
                                                current.setHint_comm_profile_name(obj_comments.getString("user_name"));
                                                current.setHint_comm_profile_image(obj_comments.getString("user_image"));
                                                current.setHint_comm_title(obj_comments.getString("answer"));
                                            } else {
                                                current.setHint_comm_profile_id2(obj_comments.getString("user_id"));
                                                current.setHint_comm_profile_username2(obj_comments.getString("user_uname"));
                                                current.setHint_comm_profile_name2(obj_comments.getString("user_name"));
                                                current.setHint_comm_profile_image2(obj_comments.getString("user_image"));
                                                current.setHint_comm_title2(obj_comments.getString("answer"));
                                            }
                                        }
                                        if (obj_story.getString("image").contentEquals("/default_interset.jpg")) {
                                            current.setNum_of_images(0);
                                        } else {
                                            current.setNum_of_images(1);
                                            current.arr_images.add(obj_story.getString("image"));
                                        }
                                        //current.setScore(obj_story.getDouble("score"));
                                        //current.setResponses(obj_story.getInt("comment_count"));
                                        current.setPost_ques(1);
                                    }
                                    //JSONObject obj_comment = block.getJSONObject("comment");
                                    if (page_index < 5) {
                                        //Log.e("Insert: ", "Inserting ..");
                                        db.addFeedData(current);
                                    }
                                    newdata.add(current);
                                }
                            } catch (JSONException e) {
                                Log.e("LOG", "" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(getActivity(), e.toString());
                            }
                            try {
                                if (page_index == 1) {
                                    try {
                                        //progressDialog.dismiss();
                                        swipeRefreshLayout.setRefreshing(false);
                                    } catch (Exception e) {
                                        Log.e("MainFeedRecyclerView", "progressDialog" + e.toString());
                                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                                        error_global.initializeVar(getActivity(), e.toString());
                                    }
                                    data.addAll(0, newdata);
                                    recyclerAdapter.notifyItemRangeInserted(0, newdata.size());
                                    newdata.clear();
                                } else {
                                    data.addAll(data.size(), newdata);
                                    recyclerAdapter.notifyItemRangeInserted(data.size(), newdata.size());
                                    newdata.clear();
                                }
                            } catch (Exception e) {
                                Log.e("MainFeedRecyclerView", "notifydatasetchanges" + e.toString());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(getActivity(), e.toString());
                            }
                            //recyclerAdapter.notifyDataSetChanged();
                            loading = false;
                            Log.i("Unit Testing", "MainFeed data parsed");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("check", "check" + error);
                            if (page_index == 1) {
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(getActivity(), "Check your Internet Connection and Try Again", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof AuthFailureError) {
                                    Toast.makeText(getActivity(), "AuthFailureError - Try Again", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ServerError) {
                                    Toast.makeText(getActivity(), "Sorry! We couldn't connect with the server - Try Again", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(getActivity(), "NetworkError - Try Again", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ParseError) {
                                    Toast.makeText(getActivity(), "ParseError - Try Again", Toast.LENGTH_SHORT).show();
                                } /*else {
                                Toast.makeText(getActivity(),"Check your Internet Connection and Try Again",Toast.LENGTH_SHORT).show();
                            }*/
                                swipeRefreshLayout.setRefreshing(false);
                                //data.clear();
                                data = db.getAllFeedData();
                                recyclerAdapter.notifyDataSetChanged();
                            }
                            //progressDialog.dismiss();
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
            if (page_index == 1) {
                /*progressDialog = new ProgressDialog(getActivity());
                //progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading Feed...");
                progressDialog.show();*/
                swipeRefreshLayout.setRefreshing(true);
            }
        } catch (Exception e) {
            Log.e("MainFeedrecyclrView", "get Feed" + e.toString());
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(getActivity(), e.toString());
        }

    }

    //for static app(not used currently
    public static List<MainFeedObject> getData() {
        List<MainFeedObject> data = new ArrayList<MainFeedObject>();
        String houseImageUrlList[] = {"http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg"};

        String houseNameList[] = {"Decor Ideas", "My Travel Diaries", "Fitness & Health", "My Favourite Literary Character",
                "Healthy Diet", "Gardening Tips", "Cooking Queries", "Quotes from Books"};
        String profileImageUrlList[] = {"https://s9.postimg.org/7hbv6xicf/dp_1.png",
                "https://s3.amazonaws.com/cdn.firstpinch.com/spree/user_profile_pic/26/original/fbdp.jpg?1473159927",
                "https://s21.postimg.org/9qx9kmyvb/dp_3.jpg",
                "https://s15.postimg.org/ujn1s8zor/dp_4.jpg",
                "https://s14.postimg.org/k384wb3ep/dp_5.jpg",
                "https://s16.postimg.org/tlc5d7dfp/dp_6.jpg",
                "https://s12.postimg.org/yvcvyvkql/dp_7.jpg",
                "https://s12.postimg.org/4f6z0qz7h/dp_8.jpg"};
        /*http://s13.postimg.org/prj11gcpj/dp_9.jpg*/
        String profileNameList[] = {"Akash Mondal", "Vishal Puri", "Dinesh Sir", "Rob", "Vishal", "Abhishek Sharma", "Ritu Sharma", "Ritu Sharma"};
        String profileUsernameList[] = {"@dexter", "@vip", "@dinesh", " @ghonu", "@vip", "@abhionly", "@Simpatico", "@Simpatico"};
        String titleList[] = {"Cool kitchen gadgets to make like simpler!", "Morning Hike - Saguaro National Park",
                "I love the world at large", "", "Some myths busted!", "Best plants for a rock garden",
                "Q  I'm forever burning my rice....Any tips on avoiding this daily disaster?", ""};
        String descList[] = {"Juicer/Mixer Grinders \n" +
                "● opt for a juicer-mixer-grinder instead of a mixer-grinder \n" +
                "● Most models nowadays come with 3 speeds. Ensure that you buy one with a pulse mode too , for chopping delicate foods,whipping milkshake \n" +
                "● An overload protection function is a must for times when we tip in more food than recommended \n" +
                "● Pick a model with at least 3 jars,these ensure that the machine is versatile \n" +
                "● A locking system that ensures that the machine doesn't start till the jar is correctly locked is an essential safety feature -especially with little kids at home",
                "Saguaro cacti backlit by eastern sunrise - Tucson Mountain District. Monsoon rains make for a lush desert!",
                "I love the golden sand \n" +
                        "To go through my hand \n" +
                        "I love the flying birds in the sky \n" +
                        "Gliding or soaring very high \n" +
                        "I love, I love the world at large!! \n" +
                        "I love the flowers pretty and gay \n" +
                        "They get up early every day \n" +
                        "I love to dance, skip & hop \n" +
                        "Or climb right up a hilltop \n" +
                        "I love, I love the world at large!! \n" +
                        "I love to dance in a meadow green \n" +
                        "Strolling like a happy king \n" +
                        "Oh, I wouldn't lie \n" +
                        "I would do anything but die \n" +
                        "Coz \n" +
                        "I love, I love the world at large! \uD83D\uDC9F\uD83D\uDC9F\uD83D\uDC9F", "Jane Eyre; for her " +
                "strengthof character and morals. She loved with all her heart,but didn't become a slave of her desires.She fought " +
                "against the wrongs done by her evil aunt and cousins bravely. She is an inspiration for all the young ladies of today.",
                "Myth : Sipping hot water will help you lose weight \n" +
                        "Fact : There's no evidence of this. However, sipping water throughout the day may eventually help you in losing weight ,as you will feel full and will tend to eat less. \n" +
                        "Myth : All carbohydrates make you fat \n" +
                        "Fact : Avoid refined -carbohydrate -rich foods and sugars like pasta, white bread and doughnuts, but if you banish the \" good - carb \" foods like whole grains, fruits , vegetables and beans- you'll miss out on the main source of fuel as well as vital nutrients and fibre \n" +
                        "Myth : You crave certain foods because your body is deficient in one of the nutrients that the food may provide \n" +
                        "Fact : Untrue. Food cravings are more emotional rather than a result of nutritional deficiency",
                "Alpine plants , found in hilly areas are most suitable for your rock garden, as they are used to UV light, rapid drainage, extreme temperatures and a strong sun. \n" +
                        "Pick jade,kalanchoe,portulaca & chlorophytum.", "", "We all grew up with the adorable bunch from the Hundred Acre Woods. Who can stay away from the charms of Pooh, Piglet, Tigger and the rest of the cutesy gang? \n" +
                "However, whatever quotes they mouthed , make so much more sense now. After growing up I realised just how simple but touching and true they are! \n" +
                "Here's a few of them which make me smile no matter how glum I may be. . \n" +
                "Love is taking a few steps backward maybe even more.... To give way to the happiness of the person you love \n" +
                "You are braver than you believe, stronger than you seem & smarter, than you think.. \n" +
                "Some people care too much. I think it's called Love.."};

        String imageUrlList[] = {"https://s17.postimg.org/kzvpqs3gv/sample_1.jpg",
                "https://s9.postimg.org/kvqk58lxb/sample_2.jpg",
                "https://s3.amazonaws.com/cdn.firstpinch.com/spree/contents/845/normal/images.jpg?1475499285",
                "none", "https://s15.postimg.org/3omz97m7v/sample_5.jpg",
                "https://s10.postimg.org/cdv5ksrbd/sample_4.jpg",
                "none", "https://s15.postimg.org/ujn1s8zor/dp_4.jpg"};
        /*http://s11.postimg.org/50s2zrtxv/sample_6.png
        http://s13.postimg.org/jf7ewzi5z/sample_7.jpg
        http://s12.postimg.org/7tpfe9pwt/sample_8.jpg
        http://s18.postimg.org/4cdtbjf1l/sample_9.jpg
        http://s15.postimg.org/43lnqtq5n/sample_10.jpg
        http://s13.postimg.org/6myypau13/sample_11.jpg*/
        int post_quesList[] = {0, 1, 0, 0, 0, 0, 1, 0};
        Float ratingList[] = {4.45f, 3.2f, 1.134f, 0f, 1f, 2.3f, 5f, 1f};
        int totalCommentsList[] = {101, 266, 10, 0, 0, 0, 3, 5};
        int totalSharesList[] = {29, 32, 298, 1, 0, 2, 2, 1};
        int user_rating[] = {3, 4, 5, 1, 1, 2, 4, 4};
        int number_of_images[] = {1, 2, 3, 0, 4, 1, 0, 1};
        String hint_comm_profile_name[] = {"Akash Mondal", "Vishal Puri", "Dinesh Sir", "Rob", "Vishal", "Abhishek Sharma", "Ritu Sharma", "Ritu Sharma"};
        String hint_comm_profile_image[] = {"https://s9.postimg.org/7hbv6xicf/dp_1.png",
                "https://s15.postimg.org/fdf7jpld7/dp_2.png",
                "https://s21.postimg.org/9qx9kmyvb/dp_3.jpg",
                "https://s15.postimg.org/ujn1s8zor/dp_4.jpg",
                "https://s14.postimg.org/k384wb3ep/dp_5.jpg",
                "https://s16.postimg.org/tlc5d7dfp/dp_6.jpg",
                "https://s12.postimg.org/yvcvyvkql/dp_7.jpg",
                "https://s12.postimg.org/4f6z0qz7h/dp_8.jpg"};
        String hint_comm_title[] = {"Juicer/Mixer Grinders \n" +
                "● opt for a juicer-mixer-grinder instead of a mixer-grinder \n" +
                "● Most models nowadays come with 3 speeds. Ensure that you buy one with a pulse mode too , for chopping delicate foods,whipping milkshake \n" +
                "● An overload protection function is a must for times when we tip in more food than recommended \n" +
                "● Pick a model with at least 3 jars,these ensure that the machine is versatile \n" +
                "● A locking system that ensures that the machine doesn't start till the jar is correctly locked is an essential safety feature -especially with little kids at home",
                "Saguaro cacti backlit by eastern sunrise - Tucson Mountain District. Monsoon rains make for a lush desert!",
                "I love the golden sand \n" +
                        "To go through my hand \n" +
                        "I love the flying birds in the sky \n" +
                        "Gliding or soaring very high \n" +
                        "I love, I love the world at large!! \n" +
                        "I love the flowers pretty and gay \n" +
                        "They get up early every day \n" +
                        "I love to dance, skip & hop \n" +
                        "Or climb right up a hilltop \n" +
                        "I love, I love the world at large!! \n" +
                        "I love to dance in a meadow green \n" +
                        "Strolling like a happy king \n" +
                        "Oh, I wouldn't lie \n" +
                        "I would do anything but die \n" +
                        "Coz \n" +
                        "I love, I love the world at large! \uD83D\uDC9F\uD83D\uDC9F\uD83D\uDC9F", "Jane Eyre; for her " +
                "strengthof character and morals. She loved with all her heart,but didn't become a slave of her desires.She fought " +
                "against the wrongs done by her evil aunt and cousins bravely. She is an inspiration for all the young ladies of today.",
                "Myth : Sipping hot water will help you lose weight \n" +
                        "Fact : There's no evidence of this. However, sipping water throughout the day may eventually help you in losing weight ,as you will feel full and will tend to eat less. \n" +
                        "Myth : All carbohydrates make you fat \n" +
                        "Fact : Avoid refined -carbohydrate -rich foods and sugars like pasta, white bread and doughnuts, but if you banish the \" good - carb \" foods like whole grains, fruits , vegetables and beans- you'll miss out on the main source of fuel as well as vital nutrients and fibre \n" +
                        "Myth : You crave certain foods because your body is deficient in one of the nutrients that the food may provide \n" +
                        "Fact : Untrue. Food cravings are more emotional rather than a result of nutritional deficiency",
                "Alpine plants , found in hilly areas are most suitable for your rock garden, as they are used to UV light, rapid drainage, extreme temperatures and a strong sun. \n" +
                        "Pick jade,kalanchoe,portulaca & chlorophytum.", "", "We all grew up with the adorable bunch from the Hundred Acre Woods. Who can stay away from the charms of Pooh, Piglet, Tigger and the rest of the cutesy gang? \n" +
                "However, whatever quotes they mouthed , make so much more sense now. After growing up I realised just how simple but touching and true they are! \n" +
                "Here's a few of them which make me smile no matter how glum I may be. . \n" +
                "Love is taking a few steps backward maybe even more.... To give way to the happiness of the person you love \n" +
                "You are braver than you believe, stronger than you seem & smarter, than you think.. \n" +
                "Some people care too much. I think it's called Love.."};


        for (int i = 0; i < houseNameList.length; i++) {
            MainFeedObject current = new MainFeedObject();
            current.setHouseImageUrl(houseImageUrlList[i]);
            current.setHouseName(houseNameList[i]);
            current.setProfileImageUrl(profileImageUrlList[i]);
            current.setProfileName(profileNameList[i]);
            current.setProfileUsername(profileUsernameList[i]);
            current.setTitle(titleList[i]);
            current.setDesc(descList[i]);
            current.setImageUrl(imageUrlList[i]);
            current.setPost_ques(post_quesList[i]);
            current.setRating(ratingList[i]);
            current.setTotalComments(totalCommentsList[i]);
            current.setTotalShares(totalSharesList[i]);
            current.setUser_rate(user_rating[i]);
            current.setNum_of_images(number_of_images[i]);
            current.setHint_comm_profile_name(hint_comm_profile_name[i]);
            current.setHint_comm_profile_image(hint_comm_profile_image[i]);
            current.setHint_comm_title(hint_comm_title[i]);
            data.add(current);
        }
        return data;
    }

    //check the internet state (on/off)
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    //async task noy used currently
    private class LongOperation extends AsyncTask<String, Void, String> {
        private ProgressDialog pdia;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity());
            pdia.setMessage("Loading...");
            pdia.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            //TextView txt = (TextView) findViewById(R.id.output);
            //txt.setText(result);
            Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
            pdia.dismiss();
        }
    }

    //sqlite db open on resume
    @Override
    public void onResume() {
        super.onResume();
        //recyclerAdapter.notifyDataSetChanged();
        db = new DatabaseHandler(getActivity());
        db.onOpen(db.getWritableDatabase());
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
