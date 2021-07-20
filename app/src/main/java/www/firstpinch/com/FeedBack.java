package www.firstpinch.com.firstpinch2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

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
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileAdapter;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;

/**
 * Created by Rianaa Admin on 28-12-2016.
 */

public class FeedBack extends AppCompatActivity {

    WebView ourBrow;
    int currentColor;
    String username="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        username = sp.getString("uname","");
        ourBrow = (WebView) findViewById(R.id.wvBrowser);
        ourBrow.getSettings().setJavaScriptEnabled(true);
        ourBrow.getSettings().setLoadWithOverviewMode(true);
        ourBrow.getSettings().setUseWideViewPort(true);
        ourBrow.getSettings().setAllowFileAccess(true);
        ourBrow.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //progressBar.setProgress(progress);
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                getSupportActionBar().setTitle("Loading...");
                //progressBar.setVisibility(View.VISIBLE);
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if (progress == 100) {
                    getSupportActionBar().setTitle("Suggestions and Feedback");
                    //progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        try {
            ourBrow.setWebViewClient(new FeedBack.OurViewClient());
        } catch (Exception e) {
            e.printStackTrace();
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(FeedBack.this, e.toString());

        }

        String url = "https://www.firstpinch.com/"+username+"/feedback?type=mobile";
        Log.e("Feedback url", ""+url);
        //url = getIntent().getStringExtra("link");

        ourBrow.loadUrl(url);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ourBrow.onPause();
        finish();
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    public class OurViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // Do something on page loading started
            //Toast.makeText(getApplicationContext(), "Page loading started", Toast.LENGTH_SHORT).show();
            // Update the action bar
            //getSupportActionBar().setSubtitle(view.getUrl());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Do something when page loading finished
            //Toast.makeText(getApplicationContext(), "Page loaded", Toast.LENGTH_SHORT).show();

            // Update the action bar
            getSupportActionBar().setTitle("Suggestions and Feedback");
            //getSupportActionBar().setSubtitle(view.getOriginalUrl());
        }

    }
}