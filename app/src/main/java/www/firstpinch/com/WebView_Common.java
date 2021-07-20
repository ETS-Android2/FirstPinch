package www.firstpinch.com.firstpinch2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;

/**
 * Created by vishallappy on 4/2/2015.
 */
public class WebView_Common extends AppCompatActivity {

    WebView ourBrow;
    ProgressBar progressBar;
    //GlobalVariblesClass commonurl;
    private ColorDrawable mActionBarBackgroundDrawable;
    //GlobalVariblesClass colorVar;
    int currentColor;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_website);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setSubtitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        ourBrow = (WebView) findViewById(R.id.wvBrowser);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final SharedPreferences sp = getSharedPreferences("abcolor",
                Activity.MODE_PRIVATE);
        currentColor = sp.getInt("color", 0xFFE45167);
        mActionBarBackgroundDrawable = new ColorDrawable(currentColor);
        ourBrow.getSettings().setJavaScriptEnabled(true);
        ourBrow.getSettings().setLoadWithOverviewMode(true);
        ourBrow.getSettings().setUseWideViewPort(true);
        ourBrow.getSettings().setBuiltInZoomControls(true);
        ourBrow.getSettings().setAllowFileAccess(true);
        ourBrow.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                getSupportActionBar().setTitle("Loading...");
                progressBar.setVisibility(View.VISIBLE);
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if (progress == 100) {
                    getSupportActionBar().setTitle(R.string.app_name);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        try {
            ourBrow.setWebViewClient(new OurViewClient());
        } catch (Exception e) {
            e.printStackTrace();
            Error_GlobalVariables error_global = new Error_GlobalVariables();
            error_global.initializeVar(WebView_Common.this, e.toString());

        }

        SharedPreferences sp1 = getSharedPreferences("webviewurl", Activity.MODE_PRIVATE);
        String url = sp1.getString("url", "");
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
            getSupportActionBar().setSubtitle(view.getUrl());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Do something when page loading finished
            //Toast.makeText(getApplicationContext(), "Page loaded", Toast.LENGTH_SHORT).show();

            // Update the action bar
            getSupportActionBar().setTitle(view.getTitle());
            getSupportActionBar().setSubtitle(view.getOriginalUrl());
        }

    }
}

