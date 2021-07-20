package www.firstpinch.com.firstpinch2.HouseProfilePages;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.Explore.ExploreActivity;
import www.firstpinch.com.firstpinch2.PinchAPostInHouse;
import www.firstpinch.com.firstpinch2.PinchAQuesInHouse;
import www.firstpinch.com.firstpinch2.ProfilePages.PostProfileFragment;
import www.firstpinch.com.firstpinch2.ProfilePages.QuestionsProfileFragment;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 16-09-2016.
 */

//house profile Activity
public class HouseProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static String POST_JOIN_HOUSE_URL = "http://54.169.84.123/api/user_clubs",
            POST_EXIT_HOUSE_URL = "http://54.169.84.123/api/user_clubs/1",
            GET_CHECK_STATUS_URL = "http://54.169.84.123/api/checkClubUserStatus",
            GET_HOUSE_INFO = "http://54.169.84.123/api/clubs/1";
    Toolbar toolbar;
    ViewPager viewPager;
    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    TabLayout tabLayout;
    String name, image_url, house_id, houseDesc;
    Boolean status;
    TextView tv_profileName, tv_profileDesc;
    ImageView im_profile_image, im_cover_image;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    private Boolean isFabOpen = false;
    public Boolean getFabOpen() {
        return isFabOpen;
    }
    public void setFabOpen(Boolean fabOpen) {
        isFabOpen = fabOpen;
    }
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private FloatingActionButton h_fab, h_fab_pinch_question, h_fab_pinch_post;
    LinearLayout h_ll_post, h_ll_ques,ll_fab;
    Button enter_exit, invite_friends;
    int user_id,POST_FEED_DELETE=1,QUESTION_FEED_DELETE=2;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_profile_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
        get_house_info();
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
        TextView activityTextPost = (TextView) findViewById(R.id.activityTexttHousepage);
        TextView activityTextQuestion = (TextView) findViewById(R.id.activityText2);

        activityTextPost.setTypeface(custom_font1);
        activityTextQuestion.setTypeface(custom_font1);

        tv_profileName = (TextView) findViewById(R.id.p_profilename);
        tv_profileName.setTypeface(custom_font);

        tv_profileDesc = (TextView) findViewById(R.id.p_desc);
        enter_exit = (Button) findViewById(R.id.house_enter_button);
        invite_friends = (Button) findViewById(R.id.house_invite_friends_button);
        h_fab = (FloatingActionButton) findViewById(R.id.h_fab);
        h_fab_pinch_question = (FloatingActionButton) findViewById(R.id.h_fab_pinch_a_question);
        h_fab_pinch_post = (FloatingActionButton) findViewById(R.id.h_fab_pinch_a_post);
        ll_fab = (LinearLayout) findViewById(R.id.ll_fab);
        h_ll_post = (LinearLayout) findViewById(R.id.h_ll_post);
        h_ll_ques = (LinearLayout) findViewById(R.id.h_ll_ques);
        im_profile_image = (ImageView) findViewById(R.id.house_profile_circleimageview);
        im_cover_image = (ImageView) findViewById(R.id.house_header);

        tv_profileDesc.setTypeface(custom_font1);

        name = getIntent().getStringExtra("housename");
        image_url = getIntent().getStringExtra("imageurl");
        house_id = getIntent().getStringExtra("house_id");
        houseDesc = getIntent().getStringExtra("housedesc");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("house_id", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("house_id", house_id);
        editor.putString("house_name", name);
        editor.commit();

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
                .load(image_url)
                .into(im_cover_image);
        Picasso.with(this)
                .load(image_url)
                .into(im_profile_image);
        tv_profileName.setText(name);
        tv_profileDesc.setText(houseDesc);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //invite button onCLick
        invite_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                //sharingIntent.setType("image/*");
                //String shareBody = houseDesc;
                String title = "Download FirstPinch App to join - " + name + "Club";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                //sharingIntent.putExtra(Intent.EXTRA_STREAM, current.getTitle());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey! I think this Firstpinch Club is just right for you. Visit here www.firstpinch.com/clubs/"+house_id+"/"+name.replaceAll(" ","-"));
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

            }
        });
        enter_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enter_exit.getText().toString().contains("Leave")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.drawable.ic_dialog_alert);
                            builder.setTitle("Leave?");
                            builder.setMessage("Are you sure you want to leave this club?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    exitRequest(house_id);
                                }

                            });
                            builder.setNegativeButton("No", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(Color.GREEN);
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.RED);
                } else {
                    joinRequest(house_id);

                }
            }
        });

    }

    //get house details API request
    private void get_house_info() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_HOUSE_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            int statuss = jsonObj.getInt("status");
                            if (statuss == 1) {
                                enter_exit.setText("Leave");
                                enter_exit.setBackgroundResource(R.drawable.house_page_button_exit);
                                ll_fab.setVisibility(View.VISIBLE);
                            } else {
                                enter_exit.setText("Join");
                                enter_exit.setBackgroundResource(R.drawable.house_page_button_enter);
                                /*if (h_fab_pinch_post.getVisibility() == View.VISIBLE) {
                                    animateFAB();
                                    h_fab.setVisibility(View.GONE);
                                }*/
                                Toast.makeText(getApplicationContext(),
                                        "Join the club to Post/Ask",
                                        Toast.LENGTH_LONG)
                                        .show();
                                ll_fab.setVisibility(View.GONE);
                            }
                            Log.e("house status", "" + statuss);
                            name = jsonObj.getString("name");
                            houseDesc = jsonObj.getString("description");
                            image_url = jsonObj.getString("image");

                            Log.e("images club", image_url + "");
                            Picasso.with(getApplicationContext())
                                    .load(image_url)
                                    .into(im_cover_image);
                            Picasso.with(getApplicationContext())
                                    .load(image_url)
                                    .into(im_profile_image);
                            tv_profileName.setText(name);
                            tv_profileDesc.setText(houseDesc);
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(HouseProfileActivity.this, e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                        Toast.makeText(getApplicationContext(),"Check your Internet Connection and Try Again",Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-Id", String.valueOf(user_id));
                headers.put("Club-Id", house_id);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
    }

    //exit house API request
    private void exitRequest(final String house_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                POST_EXIT_HOUSE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("YourHousesProfileAdpter", "data from server - " + response);
                        if (response != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                status = jsonObj.getBoolean("status");
                                if (status) {
                                    ll_fab.setVisibility(View.GONE);
                                    enter_exit.setText("Join");
                                    enter_exit.setBackgroundResource(R.drawable.house_page_button_enter);
                                    /*Toast.makeText(getApplicationContext(),
                                            "" + response,
                                            Toast.LENGTH_LONG)
                                            .show();*/
                        /*startActivity(new Intent(HouseRecyclerView.this, HouseRecyclerView.class));
                        finish();*/
                                } else {
                                    /*Toast.makeText(getApplicationContext(),
                                            ""+response,
                                            Toast.LENGTH_LONG)
                                            .show();*/
                                }

                            } catch (final JSONException e) {
                                Log.e("YourHousesProfileAdpter", "Json parsing error: " + e.getMessage());
                                Error_GlobalVariables error_global = new Error_GlobalVariables();
                                error_global.initializeVar(HouseProfileActivity.this, e.toString());
                                /*Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();*/
                            }
                        } else {
                            Log.e("YourHousesProfileAdpter", "Couldn't get json from server.");
                            /*Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();*/
                        }
                        //progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders()  {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-Id", String.valueOf(user_id));
                headers.put("Club-Id", house_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //join house API request
    public void joinRequest(final String house_id) {

        StringRequest myReq = new StringRequest(Request.Method.POST,
                POST_JOIN_HOUSE_URL,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {

            @Override
            public byte[] getBody()  {
                String str = "{\"user\":{\"user_id\":\"" + user_id + "\"" +
                        ",\"club_id\":\"" + house_id + "\"}}";
                Log.e("data", "" + str);
                return str.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
        /*progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Joining...");
        progressDialog.show();*/

    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SelectInterests", "data from server - " + response);
                if (response != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        status = jsonObj.getBoolean("status");
                        if (status) {
                            enter_exit.setText("Leave");
                            enter_exit.setBackgroundResource(R.drawable.house_page_button_exit);
                            ll_fab.setVisibility(View.VISIBLE);
                            /*Toast.makeText(getApplicationContext(),
                                    "" + response,
                                    Toast.LENGTH_LONG)
                                    .show();*/
                        /*startActivity(new Intent(HouseRecyclerView.this, HouseRecyclerView.class));
                        finish();*/
                        } else {
                            /*Toast.makeText(getApplicationContext(),
                                    ""+response,
                                    Toast.LENGTH_LONG)
                                    .show();*/
                        }

                    } catch (final JSONException e) {
                        Log.e("SelectInterests", "Json parsing error: " + e.getMessage());
                        /*Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();*/
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(HouseProfileActivity.this, e.toString());
                    }
                } else {
                    Log.e("SelectInterests", "Couldn't get json from server.");
                    /*Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG)
                            .show();*/
                }
                //progressDialog.dismiss();
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SignUp", "error connect - " + error);
                //progressDialog.dismiss();
            }
        };
    }


    //fab button and text layout on clicks
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.h_ll_ques:
                final Intent intent;
                intent = new Intent(HouseProfileActivity.this, PinchAQuesInHouse.class);
                intent.putExtra("isHouseProfileActivity", "true");
                intent.putExtra("house_id", house_id);
                intent.putExtra("housename",name);
                startActivityForResult(intent,10);
                break;
            case R.id.h_ll_post:
                intent = new Intent(HouseProfileActivity.this, PinchAPostInHouse.class);
                intent.putExtra("isHouseProfileActivity", "true");
                intent.putExtra("house_id", house_id);
                intent.putExtra("housename",name);
                startActivityForResult(intent,10);
                break;
            case R.id.h_fab:
                animateFAB();

                break;
            case R.id.h_fab_pinch_a_question:
                intent = new Intent(HouseProfileActivity.this, PinchAQuesInHouse.class);
                intent.putExtra("isHouseProfileActivity", "true");
                intent.putExtra("house_id", house_id);
                intent.putExtra("housename",name);
                startActivityForResult(intent,10);
                break;
            case R.id.h_fab_pinch_a_post:
                intent = new Intent(HouseProfileActivity.this, PinchAPostInHouse.class);
                intent.putExtra("isHouseProfileActivity", "true");
                intent.putExtra("house_id", house_id);
                intent.putExtra("housename",name);
                startActivityForResult(intent,10);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //on pinch a post/question success
            if (requestCode == 10)
                recreate();
                //pinch a post delete success refresh feed
            else if (requestCode == POST_FEED_DELETE)
                ((PostHouseFragment) adapter.mFragmentList.get(0)).get_feed_deleted(Integer.parseInt(data.getStringExtra("id")));
                //pinch a question delete success refresh feed
            else if (requestCode == QUESTION_FEED_DELETE)
                ((QuestionHouseFragment) adapter.mFragmentList.get(1)).get_feed_deleted(Integer.parseInt(data.getStringExtra("id")));
        }
    }

    //animation for fab buttons
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
        adapter.addFragment(new PostHouseFragment(), "Posts");
        adapter.addFragment(new QuestionHouseFragment(), "Questions");
        adapter.addFragment(new HouseMemberRecyclerFragment(), "Members");
        viewPager.setAdapter(adapter);
    }

    //adapter for house fragments
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

    //close fab buttons when activity paused
    @Override
    protected void onPause() {
        super.onPause();
        if (h_ll_ques.getVisibility() == View.VISIBLE) {
            animateFAB();
        }
    }

    //data updated when activity resumed
    @Override
    protected void onResume() {
        super.onResume();
        get_house_info();
    }
}