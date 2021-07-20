package www.firstpinch.com.firstpinch2.Search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 27-09-2016.
 */

//searchable activity with four fragments to view search results
public class SearchableActivity extends AppCompatActivity {

    String GET_SEARCH_DETAILS_URL = "http://54.169.84.123/api/search";
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    String query;
    int user_id, page_index = 1;
    Boolean loading = false;
    ProgressDialog progressDialog;
    List<YourHousesProfileObject> house_data = new ArrayList<YourHousesProfileObject>();
    List<HouseMemberRecyclerObject> people_data = new ArrayList<HouseMemberRecyclerObject>();
    List<SearchQuestionsFragmentObject> question_data = new ArrayList<SearchQuestionsFragmentObject>();
    List<SearchQuestionsFragmentObject> post_data = new ArrayList<SearchQuestionsFragmentObject>();

    public String getQuery() {
        return query;
    }

    public List<SearchQuestionsFragmentObject> getPost_data() {
        return post_data;
    }

    public void setPost_data(List<SearchQuestionsFragmentObject> post_data) {
        this.post_data = post_data;
    }

    public List<SearchQuestionsFragmentObject> getQuestion_data() {
        return question_data;
    }

    public void setQuestion_data(List<SearchQuestionsFragmentObject> question_data) {
        this.question_data = question_data;
    }

    public List<HouseMemberRecyclerObject> getPeople_data() {
        return people_data;
    }

    public void setPeople_data(List<HouseMemberRecyclerObject> people_data) {
        this.people_data = people_data;
    }

    public List<YourHousesProfileObject> getHouse_data() {
        return house_data;
    }

    public void setHouse_data(List<YourHousesProfileObject> house_data) {
        this.house_data = house_data;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        this.overridePendingTransition(R.anim.right_to_left,
                R.anim.do_nothing);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get the searched text from searchActivity
        query = getIntent().getStringExtra("query");
        viewPager = (ViewPager) findViewById(R.id.search_viewpager);
        get_search_details(query);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    //get the first 10 search results to load in fragments of searchable activity
    private void get_search_details(final String query) {
        loading = true;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_SEARCH_DETAILS_URL,
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
                            setHouse_data(house_data);

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
                            setPeople_data(people_data);

                            JSONArray feed3 = obj.getJSONArray("questions");
                            for (int i = 0; i < feed3.length(); i++) {
                                SearchQuestionsFragmentObject current = new SearchQuestionsFragmentObject();
                                JSONObject feedBlock3 = feed3.getJSONObject(i);
                                current.setId(feedBlock3.getString("id"));
                                current.setQuestionTitle(feedBlock3.getString("title"));
                                current.setProfileImageUrl(feedBlock3.getString("user_image"));
                                question_data.add(current);
                            }
                            setQuestion_data(question_data);

                            JSONArray feed4 = obj.getJSONArray("stories");
                            for (int i = 0; i < feed4.length(); i++) {
                                SearchQuestionsFragmentObject current = new SearchQuestionsFragmentObject();
                                JSONObject feedBlock4 = feed4.getJSONObject(i);
                                current.setId(feedBlock4.getString("id"));
                                current.setQuestionTitle(feedBlock4.getString("title"));
                                current.setProfileImageUrl(feedBlock4.getString("user_image"));
                                post_data.add(current);
                            }
                            setPost_data(post_data);

                            //load the fragments with data
                            setupViewPager(viewPager);

                            if (page_index == 1)
                                progressDialog.dismiss();
                            loading = false;

                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(SearchableActivity.this, e.toString());
                        }

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
                headers.put("Profile-Id", String.valueOf(user_id));
                headers.put("Search-Query", query);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        if (page_index == 1) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Searching...");
            progressDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.overridePendingTransition(R.anim.do_nothing,
                        R.anim.left_to_right);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //load the fragments with data
    private void setupViewPager(ViewPager viewPager) {
        //viewPager.setOffscreenPageLimit(3);
        SearchableActivity.ViewPagerAdapter adapter = new SearchableActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchedHousesFragment(), "Clubs");
        adapter.addFragment(new SearchedHouseMemberFragment(), "People");
        adapter.addFragment(new SearchQuestionsFragment(), "Questions");
        adapter.addFragment(new SearchPostFragment(), "Posts");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //close activity with animation
        this.overridePendingTransition(R.anim.do_nothing,
                R.anim.left_to_right);
    }


}