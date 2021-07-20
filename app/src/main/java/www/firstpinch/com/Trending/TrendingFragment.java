package www.firstpinch.com.firstpinch2.Trending;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.MyLinearLayoutManager;
import www.firstpinch.com.firstpinch2.R;
import www.firstpinch.com.firstpinch2.Search.SearchActivityObject;

/**
 * Created by Rianaa Admin on 01-11-2016.
 */

//trending fragment of Home.java
public class TrendingFragment extends Fragment {

    String GET_TRENDING_FEED_URL = "http://54.169.84.123/api/home_feed/treandingHouse";
    public static String house = "";
    String global_query;
    private RecyclerView recyclerViewTrendingHouses, recyclerViewPopularHouses;
    TrendingHouseAdapter recyclerTrendingHousesAdapter;
    PopularHousesAdapter popularHousesAdapter;
    List<SearchActivityObject> data2 = Collections.emptyList();
    TextView tv_search_profiles, tv_search_houses;
    List<SearchActivityObject> trending_data = new ArrayList<SearchActivityObject>();
    List<SearchActivityObject> popular_data = new ArrayList<SearchActivityObject>();
    Toolbar toolbar;
    int user_id;
    TextView search_for;
    LinearLayout ll_search_for;
    TextView tv_recommended_houses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.trending, container, false);
        Log.i("Unit Testing","Trending oncreate");


        tv_recommended_houses = (TextView) view.findViewById(R.id.tv_trending_houses);
        TextView tv_popular_houses = (TextView) view.findViewById(R.id.tv_popular_houses);
        SharedPreferences sp = getActivity().getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        recyclerViewTrendingHouses = (RecyclerView) view.findViewById(R.id.trending_houses_recyclerview);
        recyclerTrendingHousesAdapter = new TrendingHouseAdapter(getActivity(), trending_data);
        recyclerViewTrendingHouses.setAdapter(recyclerTrendingHousesAdapter);
        recyclerViewTrendingHouses.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        recyclerViewPopularHouses = (RecyclerView) view.findViewById(R.id.popular_houses_recyclerview);
        popularHousesAdapter = new PopularHousesAdapter(getActivity(), popular_data);
        recyclerViewPopularHouses.setAdapter(popularHousesAdapter);
        recyclerViewPopularHouses.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        get_feed(user_id);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                popular_data.clear();
                trending_data.clear();
                //page_index = 1;
                get_feed(user_id);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    //get trending data API request
    private void get_feed(final int user_id) {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_TRENDING_FEED_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray feed = obj.getJSONArray("trendingHouses");
                            for (int i = 0; i < feed.length(); i++) {
                                SearchActivityObject current = new SearchActivityObject();
                                JSONObject feedBlock = feed.getJSONObject(i);
                                current.setSearch_name(feedBlock.getString("name"));
                                current.setSearch_image(feedBlock.getString("image"));
                                current.setSearch_desc(feedBlock.getString("description"));
                                current.setId(feedBlock.getString("id"));
                                trending_data.add(current);
                            }
                            recyclerTrendingHousesAdapter.notifyDataSetChanged();

                            JSONArray feed2 = obj.getJSONArray("PoplarHouses");
                            for (int i = 0; i < feed2.length(); i++) {
                                SearchActivityObject current = new SearchActivityObject();
                                JSONObject feedBlock1 = feed2.getJSONObject(i);
                                current.setSearch_name(feedBlock1.getString("name"));
                                current.setSearch_image(feedBlock1.getString("image"));
                                current.setSearch_desc(feedBlock1.getString("description"));
                                current.setId(feedBlock1.getString("id"));
                                popular_data.add(current);
                            }
                            popularHousesAdapter.notifyDataSetChanged();
                            if(trending_data.isEmpty()){
                                tv_recommended_houses.setVisibility(View.GONE);
                            } else {
                                tv_recommended_houses.setVisibility(View.VISIBLE);
                            }

                            Log.i("Unit Testing","Trending data parsed");

                            //JSONObject obj_comment = block.getJSONObject("comment");


                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(getActivity(), e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                    }

                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-Id", String.valueOf(user_id));
                return headers;
            }

        };
        requestQueue.add(stringRequest);

    }

    //for static app(currently not used)
    public static List<SearchActivityObject> getData1() {
        List<SearchActivityObject> data = new ArrayList<SearchActivityObject>();
        String searchImageUrlList[] = {"http://s10.postimg.org/cdv5ksrbd/sample_4.jpg",
                "http://s15.postimg.org/3omz97m7v/sample_5.jpg",
                "http://s11.postimg.org/50s2zrtxv/sample_6.png"};

        String searchNameList[] = {"Possession & Exorcism", "Short Stories By Me", "Strange and Horrifying Pictures"};
        for (int i = 0; i < searchNameList.length; i++) {
            SearchActivityObject current = new SearchActivityObject();
            current.setSearch_name(searchNameList[i]);
            current.setSearch_image(searchImageUrlList[i]);
            data.add(current);
        }
        return data;
    }

    //for static app(currently not used)
    public static List<SearchActivityObject> getData2() {
        List<SearchActivityObject> data = new ArrayList<SearchActivityObject>();
        String searchImageUrlList[] = {"https://s17.postimg.org/kzvpqs3gv/sample_1.jpg",
                "https://s9.postimg.org/kvqk58lxb/sample_2.jpg",
                "https://s12.postimg.org/c8uzawhct/sample_3.jpg"};
        String searchNameList[] = {"Decor Ideas", "My Travel Diaries", "Fitness & Health"};
        for (int i = 0; i < searchNameList.length; i++) {
            SearchActivityObject current = new SearchActivityObject();
            current.setSearch_name(searchNameList[i]);
            current.setSearch_image(searchImageUrlList[i]);
            data.add(current);
        }
        return data;
    }

}
