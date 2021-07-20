package www.firstpinch.com.firstpinch2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rianaa Admin on 12-10-2016.
 */

public class Main extends AppCompatActivity {

    boolean login = false;
    boolean interestSelected = false, houseSelected = false;
    String YOUR_PROJECT_TOKEN = "d8d6ccf648c6edbd1ea825bd63a3bcd4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        String projectToken = YOUR_PROJECT_TOKEN; // e.g.: "1ef7e30d2a58d27f4b90c42e31d6d7ad"
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
        SharedPreferences sp = getSharedPreferences("login",
                Activity.MODE_PRIVATE);
        login = sp.getBoolean("state", false);
        SharedPreferences sp2 = getSharedPreferences("interestselected",
                Activity.MODE_PRIVATE);
        interestSelected = sp2.getBoolean("state", false);
        SharedPreferences sp3 = getSharedPreferences("houseselected",
                Activity.MODE_PRIVATE);
        houseSelected = sp3.getBoolean("state", false);

        /*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {

                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(Main.this,"Your message", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }.start();
                try
                {
                    Thread.sleep(4000); // Let the Toast display before app will get shutdown
                }
                catch (InterruptedException e) {    }
                System.exit(2);
            }
        });*/

        if (login) {
            if (interestSelected) {
                if (houseSelected) {
                    startActivity(new Intent(Main.this, Home.class));
                } else {
                    startActivity(new Intent(Main.this, HouseRecyclerView.class));
                }
            } else
                startActivity(new Intent(Main.this, SelectInterests.class));

        }
        else {

            startActivity(new Intent(Main.this, IntroActivity.class));
        }

        try {
            JSONObject props = new JSONObject();
            props.put("Gender", "Female");
            props.put("Logged in", false);
            mixpanel.track("Main - onCreate called", props);
        } catch (JSONException e) {
            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
        }


    }
}
