package www.firstpinch.com.firstpinch2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.BookMarks.BookMarksRecyclerView;
import www.firstpinch.com.firstpinch2.ErrorLogs.AlarmReceiver;
import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.Explore.ExploreActivity;
import www.firstpinch.com.firstpinch2.GCM.GCMRegistrationIntentService;
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedRecyclerView;
import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;
import www.firstpinch.com.firstpinch2.NotificationPages.NotificationsFragment;
import www.firstpinch.com.firstpinch2.ProfilePages.ProfileActivity;
import www.firstpinch.com.firstpinch2.Search.SearchActivity;
import www.firstpinch.com.firstpinch2.Settings.SettingsActivity;
import www.firstpinch.com.firstpinch2.Trending.TrendingFragment;

/**
 * Created by Rianaa Admin on 09-09-2016.
 */
public class Home extends AppCompatActivity implements View.OnClickListener {

    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_home_black_48dp,
            R.drawable.ic_notifications_black_48dp,
            R.drawable.ic_trending_up_black_48dp
    };
    private DrawerLayout drawerLayout;

    public Boolean getFabOpen() {
        return isFabOpen;
    }

    public void setFabOpen(Boolean fabOpen) {
        isFabOpen = fabOpen;
    }

    public static Boolean isFabOpen = false;
    private FloatingActionButton fab, fab_pinch_question, fab_pinch_post;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    LinearLayout ll_post, ll_ques;
    GoogleApiClient mGoogleApiClient;
    String gname, gemail, gurl, username, email, created_at, updated_at, name, gender, user_image, password;
    int g = 0, user_id, FEED_DELETE = 1;
    boolean doubleBackToExitPressedOnce = false;
    public ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    String message, body, title, content_id, content_type;
    int notification_count, tab_current_position = 0;
    BroadcastReceiver mBroadcastReceiver;
    TabLayout.Tab tab;
    TextView tab_tv;
    static boolean active = false;
    ArrayList<String> data_notif_id = new ArrayList<String>();
    //FrameLayout fl;
    //Button bt_yn,bt_hn;
    String YOUR_PROJECT_TOKEN = "d8d6ccf648c6edbd1ea825bd63a3bcd4";
    String SIGN_IN_URL = "http://54.169.84.123/api/login",
            PUT_LOGOUT = "http://54.169.84.123/api/logout";
    String token, android_id, release;
    int sdkVersion;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Log.i("Unit Testing", "Home oncreate");
        release = Build.VERSION.RELEASE;
        sdkVersion = Build.VERSION.SDK_INT;
        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int swidth = size.x;
        int sheight = size.y;
        Error_GlobalVariables.setDevice_id(release);
        Error_GlobalVariables.setDevice_width(swidth + "");
        Error_GlobalVariables.setDevice_height(sheight + "");

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(Home.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Home.this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 60 * 24;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 45);

        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                interval, pendingIntent);
        //Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();

        String projectToken = YOUR_PROJECT_TOKEN; // e.g.: "1ef7e30d2a58d27f4b90c42e31d6d7ad"
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
        isFabOpen = false;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        //toolbar.setBackgroundColor(0x0f5f5f5);
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    token = intent.getStringExtra("token");
                    Log.e("Home Token", token);
                    //Displaying the token as toast
                    //Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } /*else if (intent.getAction().equals(GCMRegistrationIntentService.NOTIFICATION_SERVICE)) {

                    // new push notification is received
                    Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();


                } */ else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_pinch_question = (FloatingActionButton) findViewById(R.id.fab_pinch_a_question);
        fab_pinch_post = (FloatingActionButton) findViewById(R.id.fab_pinch_a_post);
        ll_post = (LinearLayout) findViewById(R.id.ll_post);
        ll_ques = (LinearLayout) findViewById(R.id.ll_ques);
        //fl = (FrameLayout) findViewById(R.id.fl_fab);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        ll_ques.setOnClickListener(this);
        ll_post.setOnClickListener(this);
        fab_pinch_post.setOnClickListener(this);
        fab_pinch_question.setOnClickListener(this);

        initNavigationDrawer();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        /*Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                im_toolbar.setImageBitmap(bitmap);
                image = im_toolbar.getDrawable();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };

        Picasso.with(getApplicationContext())
                .load("https://s3.amazonaws.com/cdn.firstpinch.com/spree/user_profile_pic/26/original/fbdp.jpg?1473159927")
                .into(target);*/
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        gname = pref.getString("kname", "");
        gemail = pref.getString("kemail", "");
        gurl = pref.getString("kurl", "");
        g = pref.getInt("integer", 0);


        // Iterate over all tabs and set the custom view

        notification_count = 0;
        tab_current_position = tabLayout.getSelectedTabPosition();
        tab = tabLayout.getTabAt(1);
        tab.setCustomView(adapter.getTabView(1, notification_count));

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String notif_id = intent.getStringExtra("id");
                if (data_notif_id.contains(notif_id)) {

                } else {
                    data_notif_id.add(notif_id);
                    beginTask(intent);
                    //if (active)
                    ((NotificationsFragment) adapter.mFragmentList.get(1)).refresh();
                }

            }
        };

        SharedPreferences pref2 = getApplicationContext().getSharedPreferences("Notification_Count", MODE_PRIVATE);
        notification_count = pref2.getInt("notification_count", 0);
        set_notification_count(notification_count);
        //Log.e("Home onCreate", "Last Line");

        try {
            JSONObject props = new JSONObject();
            props.put("Gender", "Female");
            props.put("Logged in", false);
            mixpanel.track("Home - onCreate called", props);
        } catch (JSONException e) {
            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(Home.this, e.toString());
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (password.contentEquals("scl")) {

                } else {
                    credentials_check();
                }
            }
        }, 2000);
    }

    public void credentials_check() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, SIGN_IN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Sing In response", response);
                            JSONObject jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("0")) {
                                //et_password.setError("*Password is invalid");
                                if (g == 10) {
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                            new ResultCallback<Status>() {
                                                @Override
                                                public void onResult(Status status) {
                                                    SharedPreferences settings1 = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                                                    settings1.edit().clear().commit();
                                                    Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putBoolean("state", false);
                                                    editor.commit();
                                                    SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor2 = pref2.edit();
                                                    editor2.putBoolean("state", false);
                                                    editor2.commit();
                                                    SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor4 = pref4.edit();
                                                    editor4.putBoolean("state", false);
                                                    editor4.commit();
                                                    startActivity(new Intent(Home.this, Main.class));
                                                    finish();
                                                }
                                            });
                                } else {
                                    FacebookSdk.sdkInitialize(getApplicationContext());
                                    LoginManager.getInstance().logOut();
                                    Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putBoolean("state", false);
                                    editor.commit();
                                    SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = pref2.edit();
                                    editor2.putBoolean("state", false);
                                    editor2.commit();
                                    SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor4 = pref4.edit();
                                    editor4.putBoolean("state", false);
                                    editor4.commit();
                                    startActivity(new Intent(Home.this, Main.class));
                                    finish();
                                }
                            }
                            if (status.contentEquals("1")) {

                            } else if (status.contentEquals("2")) {
                                String message = jsonObj.getString("message");
                                //Toast.makeText(getApplicationContext(),"Login Failed : "+message,Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            //Toast.makeText(getApplicationContext(),""+e.toString(),Toast.LENGTH_SHORT).show();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(Home.this, e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Check your Internet Connection and Try Again", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Email-Id", email);
                headers.put("User-Password", password);
                headers.put("Token", token);
                headers.put("Device-Id", android_id);
                headers.put("Os-Type", "android");
                headers.put("Os-Version", release);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
    }

    private void put_logout() {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("logout response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {

                            } else {
                                //Toast.makeText(getApplicationContext(), "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(Home.this, e.toString());
                        }

                        // looping through All Contacts
                        //progressDialog.dismiss();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "" + error);
                        //progressDialog.dismiss();
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("device_id", android_id);
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("user", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }


            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        /*progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Adding Bookmark...");
        progressDialog.show();*/


    }

    public void set_notification_count(int notification_count) {
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        View view = tab.getCustomView();
        TextView txtCount = (TextView)
                view.findViewById(R.id.tab_tv);
        if (notification_count == 0) {
            txtCount.setVisibility(View.GONE);
        } else {
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText(notification_count + "");
        }
    }

    public void beginTask(Intent intent) {

        message = intent.getStringExtra("message");
        title = intent.getStringExtra("title");
        body = intent.getStringExtra("body");
        content_id = intent.getStringExtra("content_id");
        content_type = intent.getStringExtra("content_type");
        notification_count++;
        //adapter.getTabView(1, notification_count);
        //viewPager.notifyAll();
        //viewPager.getAdapter().notifyDataSetChanged();
        if (active) {
            if (tab_current_position == 1) {
                notification_count = 0;
                data_notif_id.clear();
                SharedPreferences pref3 = getApplicationContext().getSharedPreferences("Notification_Count", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref3.edit();
                editor.putInt("notification_count", notification_count);
                editor.commit();
                set_notification_count(notification_count);
            } else {
                TabLayout.Tab tab = tabLayout.getTabAt(1);
                View view = tab.getCustomView();
                TextView txtCount = (TextView)
                        view.findViewById(R.id.tab_tv);
                if (notification_count == 0) {
                    txtCount.setVisibility(View.GONE);
                } else {
                    txtCount.setVisibility(View.VISIBLE);
                    txtCount.setText(notification_count + "");
                }
                SharedPreferences pref3 = getApplicationContext().getSharedPreferences("Notification_Count", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref3.edit();
                editor.putInt("notification_count", notification_count);
                editor.commit();
            }
        } else {
            SharedPreferences pref3 = getApplicationContext().getSharedPreferences("Notification_Count", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref3.edit();
            editor.putInt("notification_count", notification_count);
            editor.commit();
        }
        Log.e("GCM", "Message Received from Server - " + message + "," + body + "," + title);


    }
    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        animateFAB();
        return super.onTouchEvent(event);
    }*/

    /*listView.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (floatingActionsMenu.isExpanded())
                floatingActionsMenu.collapse();
            return false;
        }
    });*/
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                animateFAB();
                break;
            case R.id.ll_ques:
                final Intent intent;
                intent = new Intent(Home.this, PinchAQues.class);
                intent.putExtra("isHouseProfileActivity", "false");
                startActivityForResult(intent, 10);
                break;
            case R.id.ll_post:
                intent = new Intent(Home.this, PinchAPost.class);
                intent.putExtra("isHouseProfileActivity", "false");
                startActivityForResult(intent, 10);
                break;
            case R.id.fab_pinch_a_post:
                intent = new Intent(Home.this, PinchAPost.class);
                intent.putExtra("isHouseProfileActivity", "false");
                startActivityForResult(intent, 10);
                break;
            case R.id.fab_pinch_a_question:
                intent = new Intent(Home.this, PinchAQues.class);
                intent.putExtra("isHouseProfileActivity", "false");
                startActivityForResult(intent, 10);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10)
                ((MainFeedRecyclerView) adapter.mFragmentList.get(0)).get_feed_refreshed();
            else if (requestCode == FEED_DELETE)
                ((MainFeedRecyclerView) adapter.mFragmentList.get(0)).get_feed_deleted(Integer.parseInt(data.getStringExtra("id")));
        }
    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab_pinch_question.startAnimation(fab_close);
            fab_pinch_post.startAnimation(fab_close);
            ll_ques.setVisibility(View.GONE);
            ll_post.setVisibility(View.GONE);
            fab_pinch_question.setClickable(false);
            fab_pinch_post.setClickable(false);
            //fl.setVisibility(View.INVISIBLE);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab_pinch_question.startAnimation(fab_open);
            fab_pinch_post.startAnimation(fab_open);
            ll_ques.setVisibility(View.VISIBLE);
            ll_post.setVisibility(View.VISIBLE);
            fab_pinch_question.setClickable(true);
            fab_pinch_post.setClickable(true);
            //fl.setVisibility(View.VISIBLE);
            isFabOpen = true;

        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == 1) {
                notification_count = 0;
                data_notif_id.clear();
                SharedPreferences pref3 = getApplicationContext().getSharedPreferences("Notification_Count", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref3.edit();
                editor.putInt("notification_count", notification_count);
                editor.commit();
                set_notification_count(notification_count);
            } else {
                // still pages are left
                //ll.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
        adapter.addFragment(new MainFeedRecyclerView(), "ONE");
        adapter.addFragment(new NotificationsFragment(), "MY_FRAGMENT");
        adapter.addFragment(new TrendingFragment(), "TRENDING");
        viewPager.setAdapter(adapter);
    }

    /*@Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.view_pager, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Searching by: "+ query, Toast.LENGTH_SHORT).show();

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
        }
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_home) {
            startActivity(new Intent(Home.this, SearchActivity.class));
            return true;
        } else if (id == R.id.notification_home) {
            startActivity(new Intent(Home.this, ExploreActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public View getTabView(int position, int notification_count) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
            tab_tv = (TextView) v.findViewById(R.id.tab_tv);
            if (notification_count == 0) {
                tab_tv.setVisibility(View.GONE);
            } else {
                tab_tv.setVisibility(View.VISIBLE);
                tab_tv.setText(notification_count + "");
            }
            ImageView img = (ImageView) v.findViewById(R.id.tab_im);
            //img.setImageResource(imageResId[position]);
            return v;
        }

        @Override
        public Fragment getItem(int position) {
            /*if(position==1){
                ll.setVisibility(View.VISIBLE);
            }
            else
            ll.setVisibility(View.GONE);*/
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
            //return mFragmentTitleList.get(position);
            return null;
        }
    }


    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //View headerLayout = navigationView.getHeaderView(1);
        //ImageView im_user_image = (ImageView) findViewById(R.id.user_image);
        /*View headerLayout =
                navigationView.inflateHeaderView(R.layout.navigation_header);
        ImageView im = (ImageView) headerLayout.findViewById(R.id.user_image);
        Picasso.with(this)
                .load(user_image)
                .transform(new RoundedCornersTransform())
                .into(im);*/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.home:
                        //Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                        final Intent intent;
                        intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("profilename", name);
                        intent.putExtra("username", username);
                        intent.putExtra("profile_id", user_id + "");
                        drawerLayout.closeDrawers();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                            }
                        }, 200);
                        break;
                    case R.id.settings:
                        drawerLayout.closeDrawers();
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Home.this, SettingsActivity.class));
                            }
                        }, 200);
                        break;
                    /*case R.id.messages:
                        Toast.makeText(getApplicationContext(), "Messages", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;*/
                    case R.id.bookmarks:
                        drawerLayout.closeDrawers();
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Home.this, BookMarksRecyclerView.class));
                            }
                        }, 200);
                        break;
                    case R.id.your_houses:
                        drawerLayout.closeDrawers();
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Home.this, YourHouses.class));
                            }
                        }, 200);
                        break;
                    case R.id.feedback:
                        drawerLayout.closeDrawers();
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Home.this, FeedBack.class));
                            }
                        }, 200);
                        break;
                    case R.id.on_the_way_features:
                        drawerLayout.closeDrawers();
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Home.this, OnTheWay.class));
                            }
                        }, 200);
                        break;
                    case R.id.faq:
                        drawerLayout.closeDrawers();
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Home.this, FAQs.class));
                            }
                        }, 200);
                        break;
                    case R.id.rateus:
                        drawerLayout.closeDrawers();
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                // To count with Play market backstack, After pressing back button,
                                // to taken back to our application, we need to add following flags to intent.
                                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                try {
                                    startActivity(goToMarket);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                                }
                            }
                        }, 200);
                        break;
                    case R.id.logout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                        builder.setIcon(R.drawable.ic_dialog_alert);
                        builder.setTitle("Closing Activity");
                        builder.setMessage("Are you sure you want to log out?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                put_logout();
                                if (g == 10) {
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                            new ResultCallback<Status>() {
                                                @Override
                                                public void onResult(Status status) {
                                                    SharedPreferences settings1 = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                                                    settings1.edit().clear().commit();
                                                    Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putBoolean("state", false);
                                                    editor.commit();
                                                    SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor2 = pref2.edit();
                                                    editor2.putBoolean("state", false);
                                                    editor2.commit();
                                                    SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor4 = pref4.edit();
                                                    editor4.putBoolean("state", false);
                                                    editor4.commit();
                                                    startActivity(new Intent(Home.this, Main.class));
                                                    finish();
                                                }
                                            });
                                } else {
                                    FacebookSdk.sdkInitialize(getApplicationContext());
                                    LoginManager.getInstance().logOut();
                                    Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putBoolean("state", false);
                                    editor.commit();
                                    SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = pref2.edit();
                                    editor2.putBoolean("state", false);
                                    editor2.commit();
                                    SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor4 = pref4.edit();
                                    editor4.putBoolean("state", false);
                                    editor4.commit();
                                    startActivity(new Intent(Home.this, Main.class));
                                    finish();
                                }
                            }

                        });
                        builder.setNegativeButton("No", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(Color.GREEN);
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.RED);
                        break;
                    case R.id.invite:
                        /*Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.vending");
                        startActivity(launchIntent);*/
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        //sharingIntent.setType("image/*");
                        //String shareBody = "Body";
                        String title = "Join me on Firstpinch" + "" + "";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                        //sharingIntent.putExtra(Intent.EXTRA_STREAM, current.getTitle());
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey! Join me on Firstpinch, I am savouring it!! Download the App here - https://play.google.com/store/apps/details?id=" + getPackageName());
                        startActivity(Intent.createChooser(sharingIntent, "Invite via"));
                        break;

                }
                return true;
            }
        });
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        username = sp.getString("uname", "");
        email = sp.getString("email", "");
        created_at = sp.getString("created_at", "");
        updated_at = sp.getString("updated_at", "");
        name = sp.getString("name", "");
        gender = sp.getString("gender", "");
        user_image = sp.getString("image", " ");
        password = sp.getString("password", "");
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView) header.findViewById(R.id.tv_email);
        tv_email.setText("@" + username);
        ImageView im = (ImageView) header.findViewById(R.id.user_image);
        Picasso.with(this)
                .load(user_image)
                .placeholder(R.drawable.placeholder_image)
                .transform(new RoundedCornersTransform())
                .into(im);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("profile_id", "" + user_id);
                startActivity(intent);
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {


            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_dehaze_white_24dp);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (ll_ques.getVisibility() == View.VISIBLE) {
            animateFAB();
        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                this.overridePendingTransition(R.anim.do_nothing,
                        R.anim.top_to_bottom);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (ll_ques.getVisibility() == View.VISIBLE) {
            animateFAB();
        }
        //Unregistering receiver on activity paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        IntentFilter filter = new IntentFilter();
        filter.addAction("www.firstpinch.com.firstpinch2.Home");
        LocalBroadcastManager.getInstance(this).registerReceiver((mBroadcastReceiver), filter);
        initNavigationDrawer();
        SharedPreferences pref2 = getApplicationContext().getSharedPreferences("Notification_Count", MODE_PRIVATE);
        notification_count = pref2.getInt("notification_count", 0);
        set_notification_count(notification_count);

    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        active = true;
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        active = false;
    }

    /*//Fires after the OnStop() state
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            trimCache(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }*/

}

/*ll = (LinearLayout) findViewById(R.id.ll_notifbuttons);
        ll.setVisibility(View.GONE);
        bt_yn = (Button) findViewById(R.id.yn);
        bt_hn = (Button) findViewById(R.id.hn);
        bt_yn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.viewpager, new YourNotificationsFragment(),"MY_FRAGMENT");
                ft.addToBackStack("");
                ft.commit();
            }
        });
        bt_hn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* Fragment fr=new HouseNotificationsFragment();
                FragmentChangeListener fc=(FragmentChangeListener)getActivity();
                fc.replaceFragment(fr);*//*
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer, new HouseNotificationsFragment());
                ft.addToBackStack("");
                ft.commit();
            }
        });*/
