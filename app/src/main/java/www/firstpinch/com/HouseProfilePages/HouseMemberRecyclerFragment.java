package www.firstpinch.com.firstpinch2.HouseProfilePages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import www.firstpinch.com.firstpinch2.Explore.ExploreActivity;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedObject;
import www.firstpinch.com.firstpinch2.MyLinearLayoutManager;
import www.firstpinch.com.firstpinch2.NotificationPages.HouseNotificationsFragment;
import www.firstpinch.com.firstpinch2.NotificationPages.NotificationsFragment;
import www.firstpinch.com.firstpinch2.NotificationPages.YourNotificatioinsObject;
import www.firstpinch.com.firstpinch2.NotificationPages.YourNotificationsFragment;
import www.firstpinch.com.firstpinch2.NotificationPages.YourNotificationsRecyclerViewAdapter;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 27-09-2016.
 */

//House Member Fragment in house profile page
public class HouseMemberRecyclerFragment extends Fragment {

    private RecyclerView recyclerView;
    private HouseMembreRecyclerAdapter recyclerAdapter;
    List<HouseMemberRecyclerObject> data = new ArrayList<HouseMemberRecyclerObject>();
    public String GET_POST_HOUSE_FEED_URL = "http://54.169.84.123/api/getUserMemer",
    house_id;
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.member_fragment_recyclerview, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                data.clear();
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
        recyclerAdapter = new HouseMembreRecyclerAdapter(getActivity(), data);
        recyclerView.setAdapter(recyclerAdapter);
        //recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //final StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        //scroll listener to call get house members API again
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if ((llm.findLastVisibleItemPosition() == recyclerAdapter.getItemCount() - 1||llm.findLastVisibleItemPosition() == recyclerAdapter.getItemCount() - 2) && !loading) {
                    //bottom of list!
                    Log.e("position", "" + llm.findLastVisibleItemPosition());
                    page_index++;
                    get_house_members(page_index);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        get_house_members(1);
        return view;
    }

    //get house members API request
    private void get_house_members(final int page_index) {

        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_POST_HOUSE_FEED_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray feed = jsonObj.getJSONArray("userMember");
                            for (int i = 0; i < feed.length(); i++) {
                                HouseMemberRecyclerObject current = new HouseMemberRecyclerObject();
                                JSONObject userMember = feed.getJSONObject(i);
                                current.setMemberId(userMember.getString("id"));
                                current.setMemberName(userMember.getString("name"));
                                current.setMemberUserName(userMember.getString("uname"));
                                current.setMemberBio(userMember.getString("bio"));
                                current.setMemberProfileImageUrl(userMember.getString("image"));
                                data.add(current);
                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(getActivity(), e.toString());
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
                headers.put("Club-Id", house_id);
                return headers;
            }

        };
        requestQueue.add(stringRequest);

    }

    //for static app(currently not used)
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

}

