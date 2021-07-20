package www.firstpinch.com.firstpinch2.Explore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import www.firstpinch.com.firstpinch2.GridSpacingItemDecoration;
import www.firstpinch.com.firstpinch2.Home;
import www.firstpinch.com.firstpinch2.HouseObject;
import www.firstpinch.com.firstpinch2.HouseRecyclerViewAdapter;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileAdapter;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 16-09-2016.
 */
//list of houses for a choosen interest
public class ExploreHousesRecyclerView extends AppCompatActivity {

    public static String house = "",interest_id,GET_EXPLORE_HOUSES_URL = "http://54.169.84.123/api/getExploreClubs";
    int count = 0;
    private RecyclerView recyclerView;
    private YourHousesProfileAdapter recyclerAdapter;
    List<YourHousesProfileObject> data = new ArrayList<YourHousesProfileObject>();
    Toolbar toolbar;
    //TextView tv_interest_chosen;
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_houses_recyclerview);
        Log.i("Unit Testing","Explore House oncreate");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //tv_interest_chosen = (TextView) findViewById(R.id.tv_interest_chosen);
        setSupportActionBar(toolbar);
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        interest_id = getIntent().getStringExtra("interest_id");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("interestname"));
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
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
        //tv_interest_chosen.setText(getIntent().getStringExtra("interestname"));

        recyclerView = (RecyclerView) findViewById(R.id.house_recyclerview);
        Button btn_go = (Button) findViewById(R.id.btn_go);
        recyclerAdapter = new YourHousesProfileAdapter(this, data);
        recyclerView.setAdapter(recyclerAdapter);
        final StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        int spanCount = 2; // 3 columns
        int spacing = 4; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //recyclerAdapter.getItemViewType(0);
                int[] asc =  llm.findLastVisibleItemPositions(null);
                RecyclerView.ViewHolder firstViewHolder = recyclerView.findViewHolderForLayoutPosition(asc[0]);
                if ((firstViewHolder.getLayoutPosition() == recyclerAdapter.getItemCount() - 4||firstViewHolder.getLayoutPosition() == recyclerAdapter.getItemCount() - 3) && !loading) {
                    Log.e("position", "" + firstViewHolder.getLayoutPosition());
                    page_index++;
                    get_feed(page_index);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        get_feed(1);


    }

    //API request to get houses data
    private void get_feed(final int page_index) {

        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_EXPLORE_HOUSES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONArray feed = jsonObj.getJSONArray("clubs");
                            for (int i = 0; i < feed.length(); i++) {
                                YourHousesProfileObject current = new YourHousesProfileObject();
                                JSONObject block = feed.getJSONObject(i);
                                current.setYourHouseName(block.getString("name"));
                                current.setYourHouseImageUrl(block.getString("image"));
                                current.setId(block.getString("id"));
                                if(block.getString("userstatus").contentEquals("unjoin")){
                                    current.setJoin_exit(0);
                                } else {
                                    current.setJoin_exit(1);
                                }
                                current.setYourHouseDesc(block.getString("description"));
                                data.add(current);
                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(ExploreHousesRecyclerView.this, e.toString());
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        if (page_index == 1)
                            progressDialog.dismiss();
                        Log.i("Unit Testing","Explore Houses data parsed");
                        loading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        Toast.makeText(getApplicationContext(),"Check your Internet Connection and Try Again",Toast.LENGTH_LONG).show();
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
                headers.put("Interest-Id", interest_id);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        if (page_index == 1) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading Feed...");
            progressDialog.show();
        }

    }

    //for static app(currently not used)
    public static List<YourHousesProfileObject> getData(){
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
                "Fitness & Health","My Travel Diaries", "Fitness & Health", "Decor Ideas","My Travel Diaries", "Fitness & Health", "Decor Ideas",
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
        Integer join_exitList[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<yourHouseImageUrlList.length && i < yourHouseNameList.length;i++){
            YourHousesProfileObject current = new YourHousesProfileObject();
            current.setYourHouseName(yourHouseNameList[i]);
            current.setYourHouseImageUrl(yourHouseImageUrlList[i]);
            current.setJoin_exit(join_exitList[i]);
            current.setYourHouseDesc(yourHouseDescList[i]);
            data.add(current);
        }
        return data;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        count=0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        count=0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        count=0;
    }
}

