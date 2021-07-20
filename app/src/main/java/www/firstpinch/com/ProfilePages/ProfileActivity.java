package www.firstpinch.com.firstpinch2.ProfilePages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.EditProfile;
import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.ImageViewActivity;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedRecyclerView;
import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;
import www.firstpinch.com.firstpinch2.PinchAPost;
import www.firstpinch.com.firstpinch2.PinchAQues;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 06-09-2016.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    String GET_PROFILE_DETAILS_URL = "http://54.169.84.123/api/users/profile/1";
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    String name, username, image_url="", profile_id, profile_desc="";
    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    int user_id,POST_FEED_DELETE=1,QUESTION_FEED_DELETE=2;
    TextView tv_profileName, tv_username, edit, tv_profileDesc;
    ImageView im_profile_image;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    private Boolean isFabOpen = false;
    ProgressDialog progressDialog;


    public Boolean getFabOpen() {
        return isFabOpen;
    }

    public void setFabOpen(Boolean fabOpen) {
        isFabOpen = fabOpen;
    }

    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private FloatingActionButton h_fab, h_fab_pinch_question, h_fab_pinch_post;
    LinearLayout h_ll_post, h_ll_ques;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");

        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);


        edit = (TextView) findViewById(R.id.tv_edit);
        tv_profileName = (TextView) findViewById(R.id.p_profilename);
        tv_username = (TextView) findViewById(R.id.p_username);
        tv_profileDesc = (TextView) findViewById(R.id.profile_desc);
        im_profile_image = (ImageView) findViewById(R.id.circleimageview);
        h_fab = (FloatingActionButton) findViewById(R.id.h_fab);
        h_fab_pinch_question = (FloatingActionButton) findViewById(R.id.h_fab_pinch_a_question);
        h_fab_pinch_post = (FloatingActionButton) findViewById(R.id.h_fab_pinch_a_post);
        h_ll_post = (LinearLayout) findViewById(R.id.h_ll_post);
        h_ll_ques = (LinearLayout) findViewById(R.id.h_ll_ques);
        final ImageView im_header = (ImageView) findViewById(R.id.header);
        tv_profileDesc.setTypeface(custom_font1);
        tv_profileName.setTypeface(custom_font);
        tv_username.setTypeface(custom_font);


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        username = getIntent().getStringExtra("username");
        profile_id = getIntent().getStringExtra("profile_id");
        profile_desc = getIntent().getStringExtra("bio");
        get_profile_detail();

        //animations for fab button initialization
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        h_fab.setOnClickListener(this);
        h_fab_pinch_question.setOnClickListener(this);
        h_fab_pinch_post.setOnClickListener(this);
        h_ll_ques.setOnClickListener(this);
        h_ll_post.setOnClickListener(this);

        Picasso.with(this)
                //.load("http://s15.postimg.org/3omz97m7v/sample_5.jpg")
                .load("https://www.firstpinch.com//assets/user_cover.jpg")
                .placeholder(R.drawable.placeholder_image)
                .into(im_header);
        /*if (profile_desc != null && profile_desc != "null") {
            tv_profileDesc.setText(profile_desc);
        } else {
            tv_profileDesc.setText("");
        }*/
        /*if (username != null && username != "null") {
            tv_username.setText(username);
        } else {
            tv_profileDesc.setText("");
        }*/
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //edit profile onCLickListener
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), EditProfile.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("profilename", name);
                intent.putExtra("username", "@" + username);
                intent.putExtra("bio", profile_desc);
                intent.putExtra("imageurl", image_url);
                intent.putExtra("coverimg", "https://www.firstpinch.com//assets/user_cover.jpg");
                startActivity(intent);
            }
        });

        //profile image onClick Listener
        im_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!image_url.isEmpty()) {
                    Intent intent;
                    intent = new Intent(ProfileActivity.this, ImageViewActivity.class);
                    intent.putExtra("profileimage", "");
                    intent.putExtra("title", "");
                    intent.putExtra("profilename", name);
                    intent.putExtra("imageurl", image_url);
                    ArrayList<String> arr_images = new ArrayList<String>();
                    arr_images.add(image_url);
                    intent.putStringArrayListExtra("arr_images", arr_images);
                    //intent.putExtra("responses", current.getResponses() + "");
                    //intent.putExtra("score", current.getScore() + "");
                    //intent.putExtra("appreciations", current.getAppreciations_count() + "");
                    intent.putExtra("isMainFeedAdapter", "profileActivity");
                    intent.putExtra("position", 0);
                    startActivity(intent);
                }
            }
        });


    }

    //get all profile details API request
    private void get_profile_detail() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_PROFILE_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONObject profile = jsonObj.getJSONObject("profile");
                            name = profile.getString("name");
                            username = profile.getString("uname");
                            image_url = profile.getString("image");
                            profile_desc = profile.getString("bio");
                            tv_profileName.setText(name);
                            tv_username.setText("@" + username);
                            Log.e("Profile desc", "-" + profile_desc);
                            Picasso.with(getApplicationContext())
                                    .load(image_url)
                                    .placeholder(R.drawable.placeholder_image)
                                    .transform(new RoundedCornersTransform())
                                    .into(im_profile_image);
                            if (profile.getString("id").contentEquals(String.valueOf(user_id))) {
                                h_fab.setVisibility(View.VISIBLE);
                                edit.setVisibility(View.VISIBLE);
                                if(profile_desc.contentEquals("null")||profile_desc.contentEquals("")){
                                    tv_profileDesc.setText("\"Add your bio in edit profile...\"");
                                    tv_profileDesc.setTextColor(0xFF9E9E9E);
                                } else {
                                    tv_profileDesc.setText(profile_desc);
                                    tv_profileDesc.setTextColor(0xFF212121);
                                }
                            } else {
                                h_fab.setVisibility(View.GONE);
                                edit.setVisibility(View.INVISIBLE);
                                if(profile_desc.contentEquals("null")||profile_desc.contentEquals("")){
                                    tv_profileDesc.setText("\"No Bio Yet...\"");
                                    tv_profileDesc.setTextColor(0xFF9E9E9E);
                                } else {
                                    tv_profileDesc.setText(profile_desc);
                                    tv_profileDesc.setTextColor(0xFF212121);
                                }
                            }
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("profile_id", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("profile_id", profile.getString("id"));
                            editor.commit();
                            //progressDialog.dismiss();
                            setupViewPager(viewPager);

                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(ProfileActivity.this, e.toString());
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Check your Internet Connection and Try Again",Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-Id", profile_id);
                headers.put("User-Uname",username);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        /*progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading User Content...");
        progressDialog.show();*/

    }

    //get profile details when activity is resumed
    private void get_profile_detail_on_resume() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_PROFILE_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONObject profile = jsonObj.getJSONObject("profile");
                            name = profile.getString("name");
                            username = profile.getString("uname");
                            image_url = profile.getString("image");
                            profile_desc = profile.getString("bio");
                            tv_profileName.setText(name);
                            tv_username.setText("@" + username);
                            Picasso.with(getApplicationContext())
                                    .load(image_url)
                                    .placeholder(R.drawable.placeholder_image)
                                    .transform(new RoundedCornersTransform())
                                    .into(im_profile_image);
                            if (profile.getString("id").contentEquals(String.valueOf(user_id))) {
                                h_fab.setVisibility(View.VISIBLE);
                                edit.setVisibility(View.VISIBLE);
                                if(profile_desc.contentEquals("null")||profile_desc.contentEquals("")){
                                    tv_profileDesc.setText("\"Add your bio in edit profile...\"");
                                    tv_profileDesc.setTextColor(0xFF9E9E9E);
                                } else {
                                    tv_profileDesc.setText(profile_desc);
                                    tv_profileDesc.setTextColor(0xFF212121);
                                }
                            } else {
                                h_fab.setVisibility(View.GONE);
                                edit.setVisibility(View.INVISIBLE);
                                if(profile_desc.contentEquals("null")||profile_desc.contentEquals("")){
                                    tv_profileDesc.setText("\"No Bio Yet...\"");
                                    tv_profileDesc.setTextColor(0xFF9E9E9E);
                                } else {
                                    tv_profileDesc.setText(profile_desc);
                                    tv_profileDesc.setTextColor(0xFF212121);
                                }
                            }
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("profile_id", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("profile_id", profile.getString("id"));
                            editor.commit();

                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(ProfileActivity.this, e.toString());
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
                headers.put("User-Id", profile_id);
                headers.put("User-Uname",username);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getY();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    Toast.makeText(this, "up or down swipe", Toast.LENGTH_SHORT).show();
                } else {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }*/

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
        /*adapter.addFragment(new PostProfileFragment(), "Posts");
        adapter.addFragment(new QuestionsProfileFragment(), "Questions");
        adapter.addFragment(new YourHousesProfileFragment(), "Your Houses");*/
        adapter.addFragment(new PostProfileFragment(), "Posts");
        adapter.addFragment(new QuestionsProfileFragment(), "Questions");
        adapter.addFragment(new YourHousesProfileFragment(), "Clubs");
        viewPager.setAdapter(adapter);
    }

    //onCLicks on fab buttons and their textViews
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.h_ll_ques:
                final Intent intent;
                intent = new Intent(ProfileActivity.this, PinchAQues.class);
                intent.putExtra("isHouseProfileActivity", "false");
                startActivityForResult(intent,10);
                break;
            case R.id.h_ll_post:
                intent = new Intent(ProfileActivity.this, PinchAPost.class);
                intent.putExtra("isHouseProfileActivity", "false");
                startActivityForResult(intent,10);
                break;
            case R.id.h_fab:
                animateFAB();
                break;
            case R.id.h_fab_pinch_a_question:
                intent = new Intent(ProfileActivity.this, PinchAQues.class);
                intent.putExtra("isHouseProfileActivity", "false");
                startActivityForResult(intent,10);
                break;
            case R.id.h_fab_pinch_a_post:
                intent = new Intent(ProfileActivity.this, PinchAPost.class);
                intent.putExtra("isHouseProfileActivity", "false");
                startActivityForResult(intent,10);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //when pinched a post/question success
            if (requestCode == 10)
                recreate();
            //when feed item in posts is deleted
            else if (requestCode == POST_FEED_DELETE)
                ((PostProfileFragment) adapter.mFragmentList.get(0)).get_feed_deleted(Integer.parseInt(data.getStringExtra("id")));
                //when feed item in questions is deleted
            else if (requestCode == QUESTION_FEED_DELETE)
                ((QuestionsProfileFragment) adapter.mFragmentList.get(1)).get_feed_deleted(Integer.parseInt(data.getStringExtra("id")));
        }
    }

    //animation on fab button
    public void animateFAB() {

        if (isFabOpen) {

            h_fab.startAnimation(rotate_backward);
            h_fab_pinch_question.startAnimation(fab_close);
            h_fab_pinch_post.startAnimation(fab_close);
            h_ll_ques.setVisibility(View.GONE);
            h_ll_post.setVisibility(View.GONE);
            h_fab_pinch_question.setClickable(false);
            h_fab_pinch_post.setClickable(false);
            isFabOpen = false;

        } else {

            h_fab.startAnimation(rotate_forward);
            h_fab_pinch_question.startAnimation(fab_open);
            h_fab_pinch_post.startAnimation(fab_open);
            h_ll_ques.setVisibility(View.VISIBLE);
            h_ll_post.setVisibility(View.VISIBLE);
            h_fab_pinch_question.setClickable(true);
            h_fab_pinch_post.setClickable(true);
            isFabOpen = true;

        }
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

    //close fab button when activity is in paused state
    @Override
    protected void onPause() {
        super.onPause();
        if (h_ll_ques.getVisibility() == View.VISIBLE) {
            animateFAB();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_profile_detail_on_resume();
    }
}