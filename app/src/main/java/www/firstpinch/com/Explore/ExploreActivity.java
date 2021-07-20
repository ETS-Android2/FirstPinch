package www.firstpinch.com.firstpinch2.Explore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 16-09-2016.
 */
public class ExploreActivity extends AppCompatActivity {

    public static String house = "", GET_EXPLORE_INTERESTS_URL = "http://54.169.84.123/api/getExploreInterests",
            device_id = "", device_width = "", device_height = "";
    private RecyclerView recyclerView;
    private ExploreAdapter recyclerAdapter;
    List<ExploreObject> data = new ArrayList<ExploreObject>();
    Toolbar toolbar;
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_recyclerview);
        Log.i("Unit Testing", "Explore oncreate");
        try {
            SharedPreferences sp = getSharedPreferences("UserDetails", Activity.MODE_PRIVATE);
            user_id = sp.getInt("id", 0);
        } catch (Exception e) {
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(ExploreActivity.this, e.toString());
        }
        toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Interests");
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

        recyclerView = (RecyclerView) findViewById(R.id.house_recyclerview);
        recyclerAdapter = new ExploreAdapter(this, data);
        recyclerView.setAdapter(recyclerAdapter);
        final StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        //scroll listener to call the API again
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
                /*int num = (int) (firstViewHolder.getLayoutPosition() - recyclerView.getChildItemId(itemView));
                Log.e("getlayutposiyion", "" + firstViewHolder.getLayoutPosition());
                Log.e("getitemCount", "" + recyclerAdapter.getItemCount());
                Log.e("num=", "" + num);*/
                if ((firstViewHolder.getLayoutPosition() == recyclerAdapter.getItemCount() - 4 || firstViewHolder.getLayoutPosition() == recyclerAdapter.getItemCount() - 3) && !loading) {
                    //bottom of list!
                    Log.e("position", "" + firstViewHolder.getLayoutPosition());
                    page_index++;
                    get_feed(page_index);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        get_feed(1);
        this.overridePendingTransition(R.anim.top_right_to_centre,
                R.anim.do_nothing);
    }

    //get explore interests API request
    private void get_feed(final int page_index) {

        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_EXPLORE_INTERESTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONObject feed = jsonObj.getJSONObject("interests");
                            JSONArray arr_unjoined_interests = feed.getJSONArray(("unjoined_interests"));
                            for (int i = 0; i < arr_unjoined_interests.length(); i++) {
                                ExploreObject current = new ExploreObject();
                                JSONObject block = arr_unjoined_interests.getJSONObject(i);
                                current.setInterest_id(block.getString("id"));
                                current.setExp_interest_name(block.getString("title"));
                                current.setExp_interest_image(block.getString("image"));
                                data.add(current);
                            }
                            JSONArray arr_joined_interests = feed.getJSONArray(("joined_interests"));
                            for (int i = 0; i < arr_joined_interests.length(); i++) {
                                ExploreObject current = new ExploreObject();
                                JSONObject block = arr_joined_interests.getJSONObject(i);
                                current.setInterest_id(block.getString("id"));
                                current.setExp_interest_name(block.getString("title"));
                                current.setExp_interest_image(block.getString("image"));
                                data.add(current);
                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(ExploreActivity.this, e.toString());
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        if (page_index == 1)
                            progressDialog.dismiss();
                        loading = false;
                        Log.i("Unit Testing", "Explore interests data parsed");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
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
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        if (page_index == 1) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading Interests...");
            progressDialog.show();
        }

    }

    //for static app(currently not used)
    public static List<ExploreObject> getData() {
        List<ExploreObject> data = new ArrayList<ExploreObject>();
        String[] exp_interestList = {"Decor Ideas", "My Travel Diaries", "Crosswords Mania", "Fitness Goals & Tips", "Cute Baby Names",
                "Skin Care", "Great People said..", "Classic Revisited", "Reiki", "My Travel Diaries", "Crosswords Mania",
                "Skin Care", "Great People said.."};

        String[] exp_interest_image_List = {"http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/79/normal/1c174cd5a9570dfa9fe62f1115f93336.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/normal/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/79/normal/1c174cd5a9570dfa9fe62f1115f93336.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/normal/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/normal/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/79/normal/1c174cd5a9570dfa9fe62f1115f93336.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/normal/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/79/normal/1c174cd5a9570dfa9fe62f1115f93336.jpg"};
        for (int i = 0; i < exp_interest_image_List.length && i < exp_interestList.length; i++) {
            ExploreObject current = new ExploreObject();
            current.setExp_interest_name(exp_interestList[i]);
            current.setExp_interest_image(exp_interest_image_List[i]);
            data.add(current);
        }
        return data;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //for animating the view when back button is pressed
        this.overridePendingTransition(R.anim.do_nothing,
                R.anim.bottom_left_to_center);
    }


}
