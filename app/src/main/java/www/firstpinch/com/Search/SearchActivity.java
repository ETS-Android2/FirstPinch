package www.firstpinch.com.firstpinch2.Search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.MyLinearLayoutManager;
import www.firstpinch.com.firstpinch2.ProfilePages.ProfileActivity;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 28-09-2016.
 */

//search activity opened when clicked on search icon from Home.java
public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    String GET_SEARCH_URL = "http://54.169.84.123/api/autosearch";
    public static String house = "";
    String global_query="";
    private RecyclerView recyclerViewProfile, recyclerViewHouse, recyclerViewOptions;
    private SearcActivityProfileAdapter recyclerProfileAdapter;
    private SearchActivityOptionsAdapter recyclerAdapterOptions;
    private SearchActivityHouseAdapter recyclerHouseAdapter;
    TextView tv_search_profiles, tv_search_houses;
    List<SearchActivityObject> profile_data = new ArrayList<SearchActivityObject>();
    List<SearchActivityObject> house_data = new ArrayList<SearchActivityObject>();
    List<SearchActivityObject> options_data = new ArrayList<SearchActivityObject>();
    Toolbar toolbar;

    TextView search_for;
    LinearLayout ll_search_for;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        Log.e("log-","onCreate");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        search_for = (TextView) findViewById(R.id.tv_search_for);
        ll_search_for = (LinearLayout) findViewById(R.id.ll_search_for);
        tv_search_profiles = (TextView) findViewById(R.id.tv_search_profiles);
        tv_search_houses = (TextView) findViewById(R.id.tv_search_houses);
        tv_search_profiles.setVisibility(View.GONE);
        tv_search_houses.setVisibility(View.GONE);

        recyclerViewProfile = (RecyclerView) findViewById(R.id.search_profile_recyclerview);
        recyclerProfileAdapter = new SearcActivityProfileAdapter(this, profile_data);
        recyclerViewProfile.setAdapter(recyclerProfileAdapter);
        recyclerViewProfile.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerViewHouse = (RecyclerView) findViewById(R.id.search_house_recyclerview);
        recyclerHouseAdapter = new SearchActivityHouseAdapter(this, house_data);
        recyclerViewHouse.setAdapter(recyclerHouseAdapter);
        recyclerViewHouse.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerViewOptions = (RecyclerView) findViewById(R.id.search_options_recyclerview);
        recyclerAdapterOptions = new SearchActivityOptionsAdapter(this, options_data);
        recyclerViewOptions.setAdapter(recyclerAdapterOptions);
        recyclerViewOptions.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        this.overridePendingTransition(R.anim.right_to_left,
                R.anim.do_nothing);

        ll_search_for.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQueryTextSubmit(global_query);
            }
        });

    }

    //get search results from API request
    private void get_search(String query) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://54.169.84.123/api/autosearch?searchquery="+query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            house_data.clear();
                            profile_data.clear();
                            JSONObject obj = new JSONObject(response);
                            JSONArray feed = obj.getJSONArray("clubs");
                            for (int i = 0; i < feed.length(); i++) {
                                SearchActivityObject current = new SearchActivityObject();
                                JSONObject feedBlock = feed.getJSONObject(i);
                                current.setSearch_name(feedBlock.getString("name"));
                                current.setSearch_image(feedBlock.getString("image"));
                                current.setSearch_desc(feedBlock.getString("description"));
                                current.setId(feedBlock.getString("id"));
                                house_data.add(current);
                            }
                            if(feed.length()>0){
                                tv_search_houses.setVisibility(View.VISIBLE);
                            }else{
                                tv_search_houses.setVisibility(View.GONE);
                            }
                            recyclerHouseAdapter.notifyDataSetChanged();

                            JSONArray feed2 = obj.getJSONArray("users");
                            for (int i = 0; i < feed2.length(); i++) {
                                SearchActivityObject current = new SearchActivityObject();
                                JSONObject feedBlock2 = feed2.getJSONObject(i);
                                current.setSearch_name(feedBlock2.getString("name"));
                                current.setSearch_image(feedBlock2.getString("image"));
                                current.setId(feedBlock2.getString("id"));
                                profile_data.add(current);
                            }
                            if(feed2.length()>0){
                                tv_search_profiles.setVisibility(View.VISIBLE);
                            }else{
                                tv_search_profiles.setVisibility(View.GONE);
                            }
                            recyclerProfileAdapter.notifyDataSetChanged();
                            options_data = getDataOptions();
                            recyclerAdapterOptions = new SearchActivityOptionsAdapter(SearchActivity.this, options_data);
                            recyclerViewOptions.setAdapter(recyclerAdapterOptions);
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(SearchActivity.this, e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),"Check your Internet Connection and Try Again",Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        requestQueue.add(stringRequest);

    }

    public void onClickCalled() {
        // Call another acitivty here and pass some arguments to it.
        onQueryTextSubmit(global_query);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search_home).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.setQueryHint("Search...");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("log-","onBackPressed");
        //close activity with animation
        this.overridePendingTransition(R.anim.do_nothing,
                R.anim.left_to_right);
    }

    public Context contextShare(){
        return this;
    }

    //called when search is clicked
    @Override
    public boolean onQueryTextSubmit(String query) {

        if(!query.contentEquals("")) {
            Intent intent = new Intent(SearchActivity.this, SearchableActivity.class);
            intent.putExtra("query", query);
            startActivity(intent);
        }
        return false;
    }

    //called when text in search is changed
    @Override
    public boolean onQueryTextChange(String newText) {

        //Toast.makeText(this, "" + newText, Toast.LENGTH_SHORT).show();
        this.global_query = newText;
        search_for.setText("\""+newText+"\"");
        if (newText.length()!=0) {
            /*options_data = getDataOptions();
            recyclerAdapterOptions = new SearchActivityOptionsAdapter(this, options_data);
            recyclerViewOptions.setAdapter(recyclerAdapterOptions);*/
            get_search(newText);
        } else {
            /*options_data.clear();
            recyclerAdapterOptions = new SearchActivityOptionsAdapter(this, options_data);
            recyclerViewOptions.setAdapter(recyclerAdapterOptions);*/
        }

        return false;
    }

    //data for search options
    public static List<SearchActivityObject> getDataOptions() {
        List<SearchActivityObject> data = new ArrayList<SearchActivityObject>();

        String searchNameList[] = {"Search Clubs", "Search Profiles", "Search Posts", "Search Questions"};
        for (int i = 0; i < searchNameList.length; i++) {
            SearchActivityObject current = new SearchActivityObject();
            current.setSearch_name(searchNameList[i]);
            //current.setSearch_image(searchImageUrlList[i]);
            data.add(current);
        }
        return data;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("log-","onRestart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("log-","onDestroy");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("log-","onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("log-","onPause");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("log-","onStart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("log-","onStop");

    }
}
