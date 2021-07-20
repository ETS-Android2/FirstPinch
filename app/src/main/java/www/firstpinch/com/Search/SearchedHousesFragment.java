package www.firstpinch.com.firstpinch2.Search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileAdapter;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 04-11-2016.
 */

public class SearchedHousesFragment extends Fragment {

    public static String house = "", GET_SEARCHED_HOUSES_URL = "http://54.169.84.123/api/clubSearch",
            profile_id;
    int count = 0;
    private RecyclerView recyclerView;
    private YourHousesProfileAdapter recyclerAdapter;
    int user_id, page_index = 1;
    Boolean loading = false;
    String query;
    ProgressDialog progressDialog;
    List<YourHousesProfileObject> house_data;
    LinearLayout ll_empty_recyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        house_data = ((SearchableActivity) getActivity()).getHouse_data();
        query = ((SearchableActivity) getActivity()).getQuery();
        View view = inflater.inflate(R.layout.search_house_feed_recyclerview, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                house_data.clear();
                page_index = 1;
                get_feed(1);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        SharedPreferences sp = getActivity().getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0); //session id always same
        SharedPreferences sp1 = getActivity().getSharedPreferences("profile_id",
                Activity.MODE_PRIVATE);
        profile_id = sp1.getString("profile_id", "1"); //id of the profile selected

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) view.findViewById(R.id.house_recyclerview);
        ll_empty_recyclerview = (LinearLayout) view.findViewById(R.id.ll_empty_recyclerview);
        //Button btn_go = (Button) findViewById(R.id.btn_go);
        //selectedHouses = new SelectedInterestsObject();
        recyclerAdapter = new YourHousesProfileAdapter(getActivity(), house_data);
        if (house_data.isEmpty()) {
            swipeRefreshLayout.setVisibility(View.GONE);
            ll_empty_recyclerview.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            ll_empty_recyclerview.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(recyclerAdapter);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        final StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //recyclerAdapter.getItemViewType(0);
                int[] asc = llm.findLastVisibleItemPositions(null);
                RecyclerView.ViewHolder firstViewHolder = recyclerView.findViewHolderForLayoutPosition(asc[0]);
                View itemView = firstViewHolder.itemView;
                int num = (int) (firstViewHolder.getLayoutPosition() - recyclerView.getChildItemId(itemView));
                if ((firstViewHolder.getLayoutPosition() == recyclerAdapter.getItemCount() - 4 || firstViewHolder.getLayoutPosition() == recyclerAdapter.getItemCount() - 3) && !loading) {
                    //bottom of list!
                    Log.e("position", "" + firstViewHolder.getLayoutPosition());
                    page_index++;
                    get_feed(page_index);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //get_feed(1);

        return view;
    }

    private void get_feed(final int page_index) {

        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_SEARCHED_HOUSES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray feed = obj.getJSONArray("clubs");
                            for (int i = 0; i < feed.length(); i++) {
                                YourHousesProfileObject current = new YourHousesProfileObject();
                                JSONObject feedBlock = feed.getJSONObject(i);
                                current.setYourHouseName(feedBlock.getString("name"));
                                current.setYourHouseImageUrl(feedBlock.getString("image"));
                                current.setYourHouseDesc(feedBlock.getString("description"));
                                current.setId(feedBlock.getString("id"));
                                current.setJoin_exit(feedBlock.getInt("status"));
                                house_data.add(current);
                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(getActivity(), e.toString());
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        if (page_index == 1)
                            progressDialog.dismiss();
                        loading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        if (page_index == 1)
                            progressDialog.dismiss();
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
                headers.put("Search-Query", query);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        if (page_index == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

    }

    public static List<YourHousesProfileObject> getData() {
        List<YourHousesProfileObject> data = new ArrayList<YourHousesProfileObject>();
        String yourHouseImageUrlList[] = {"http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg"};
        String yourHouseNameList[] = {"Decor Ideas", "My Travel Diaries", "Fitness & Health", "Decor Ideas", "My Travel Diaries",
                "Fitness & Health", "My Travel Diaries", "Fitness & Health", "Decor Ideas", "My Travel Diaries", "Fitness & Health", "Decor Ideas",
                "My Travel Diaries", "Fitness & Health"};
        String yourHouseDescList[] = {"http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg" +
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg" +
                        "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg" +
                        "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg" +
                        "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg" +
                        "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg"};
        Integer join_exitList[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        for (int i = 0; i < yourHouseImageUrlList.length && i < yourHouseNameList.length; i++) {
            YourHousesProfileObject current = new YourHousesProfileObject();
            current.setYourHouseName(yourHouseNameList[i]);
            current.setYourHouseImageUrl(yourHouseImageUrlList[i]);
            current.setJoin_exit(join_exitList[i]);
            current.setYourHouseDesc(yourHouseDescList[i]);

            data.add(current);
        }
        return data;
    }

}