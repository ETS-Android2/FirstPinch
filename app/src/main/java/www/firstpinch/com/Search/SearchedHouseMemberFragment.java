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
import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseMemberRecyclerObject;
import www.firstpinch.com.firstpinch2.HouseProfilePages.HouseMembreRecyclerAdapter;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 05-11-2016.
 */

public class SearchedHouseMemberFragment extends Fragment {

    private RecyclerView recyclerView;
    private HouseMembreRecyclerAdapter recyclerAdapter;
    public String GET_SEARCHED_PEOPLE_URL = "http://54.169.84.123/api/userSearch",
            house_id;
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;
    String query;
    List<HouseMemberRecyclerObject> people_data;
    LinearLayout ll_empty_recyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        people_data =  ((SearchableActivity) getActivity()).getPeople_data();
        query = ((SearchableActivity) getActivity()).getQuery();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.member_fragment_recyclerview, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                people_data.clear();
                page_index = 1;
                get_house_members(1);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        SharedPreferences sp = getActivity().getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        SharedPreferences sp1 = getActivity().getSharedPreferences("house_id",
                Activity.MODE_PRIVATE);
        house_id = sp1.getString("house_id", "1");

        recyclerView = (RecyclerView) view.findViewById(R.id.memberfragment_recyclerview);
        ll_empty_recyclerview = (LinearLayout) view.findViewById(R.id.ll_empty_recyclerview);
        recyclerAdapter = new HouseMembreRecyclerAdapter(getActivity(), people_data);
        if(people_data.isEmpty()){
            swipeRefreshLayout.setVisibility(View.GONE);
            ll_empty_recyclerview.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            ll_empty_recyclerview.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(recyclerAdapter);
        //recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        final StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int[] asc =  llm.findLastVisibleItemPositions(null);
                RecyclerView.ViewHolder firstViewHolder = recyclerView.findViewHolderForLayoutPosition(asc[0]);
                //Log.e("MEMBER SCROLL position", "" + firstViewHolder.getLayoutPosition());
                try {
                    if ((firstViewHolder.getLayoutPosition() == recyclerAdapter.getItemCount() - 2 || firstViewHolder.getLayoutPosition() == recyclerAdapter.getItemCount() - 1) && !loading) {
                        //bottom of list!
                        Log.e("position", "" + firstViewHolder.getLayoutPosition());
                        page_index++;
                        get_house_members(page_index);
                    }
                }catch(Exception e){
                    Log.e("SearchHousemmberFrgmnt","scroll listener"+e.toString());
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(getActivity(), e.toString());
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //get_house_members(1);
        return view;
    }

    private void get_house_members(final int page_index) {

        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_SEARCHED_PEOPLE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray feed2 = obj.getJSONArray("users");
                            for (int i = 0; i < feed2.length(); i++) {
                                HouseMemberRecyclerObject current = new HouseMemberRecyclerObject();
                                JSONObject feedBlock2 = feed2.getJSONObject(i);
                                current.setMemberId(feedBlock2.getString("id"));
                                current.setMemberName(feedBlock2.getString("name"));
                                current.setMemberUserName(feedBlock2.getString("uname"));
                                current.setMemberBio(feedBlock2.getString("bio"));
                                current.setMemberProfileImageUrl(feedBlock2.getString("image"));
                                people_data.add(current);
                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        /*if (page_index == 1)
                            progressDialog.dismiss();*/
                        loading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
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
                headers.put("Search-Query", query);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        /*if (page_index == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading Feed...");
            progressDialog.show();
        }*/

    }

    public static List<HouseMemberRecyclerObject> getData() {
        List<HouseMemberRecyclerObject> data = new ArrayList<HouseMemberRecyclerObject>();
        String memberNameList[] = {"Akash Mondal", "Vishal Puri", "Dinesh Sir", "Rob", "Vishal", "Abhishek Sharma", "Ritu Sharma", "Ritu Sharma",
                "Akash Mondal", "Vishal Puri", "Dinesh Sir", "Rob", "Vishal", "Abhishek Sharma", "Ritu Sharma", "Ritu Sharma"};
        String memberUsernameList[] = {"@dexter", "@vip", "@dinesh", " @ghonu", "@vip", "@abhionly", "@Simpatico", "@Simpatico",
                "@dexter", "@vip", "@dinesh", " @ghonu", "@vip", "@abhionly", "@Simpatico", "@Simpatico"};
        String memberProfileImageUrlList[] = {"https://s9.postimg.org/7hbv6xicf/dp_1.png",
                "https://s15.postimg.org/fdf7jpld7/dp_2.png",
                "https://s21.postimg.org/9qx9kmyvb/dp_3.jpg",
                "https://s15.postimg.org/ujn1s8zor/dp_4.jpg",
                "https://s14.postimg.org/k384wb3ep/dp_5.jpg",
                "https://s16.postimg.org/tlc5d7dfp/dp_6.jpg",
                "https://s12.postimg.org/yvcvyvkql/dp_7.jpg",
                "https://s12.postimg.org/4f6z0qz7h/dp_8.jpg",
                "https://s9.postimg.org/7hbv6xicf/dp_1.png",
                "https://s15.postimg.org/fdf7jpld7/dp_2.png",
                "https://s21.postimg.org/9qx9kmyvb/dp_3.jpg",
                "https://s15.postimg.org/ujn1s8zor/dp_4.jpg",
                "https://s14.postimg.org/k384wb3ep/dp_5.jpg",
                "https://s16.postimg.org/tlc5d7dfp/dp_6.jpg",
                "https://s12.postimg.org/yvcvyvkql/dp_7.jpg",
                "https://s12.postimg.org/4f6z0qz7h/dp_8.jpg"};

        for (int i = 0; i < memberNameList.length; i++) {
            HouseMemberRecyclerObject current = new HouseMemberRecyclerObject();
            current.setMemberName(memberNameList[i]);
            current.setMemberUserName(memberUsernameList[i]);
            current.setMemberProfileImageUrl(memberProfileImageUrlList[i]);

            data.add(current);
        }
        return data;
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        count = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        count = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        count = 0;
    }*/
}