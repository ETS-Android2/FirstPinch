package www.firstpinch.com.firstpinch2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Rianaa Admin on 05-12-2016.
 */

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
        /*setContentView(R.layout.splashactivity);
        Log.e("log-", "onCreate");
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                *//* Create an Intent that will start the Menu-Activity. *//*
                Intent mainIntent = new Intent(SplashActivity.this,Main.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
        *//*this.overridePendingTransition(R.anim.right_to_left,
                R.anim.do_nothing);*/

    }
}