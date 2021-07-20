package www.firstpinch.com.firstpinch2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import www.firstpinch.com.firstpinch2.ProfilePages.ProfileActivity;

/**
 * Created by Rianaa Admin on 06-09-2016.
 */
public class ContainerLayouts extends AppCompatActivity {

    Button bt_profile,bt_pinch_post,bt_pinch_ques,bt_notification,bt_club_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouts_container);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        bt_profile = (Button) findViewById(R.id.bt_profile_layout);
        bt_pinch_post = (Button) findViewById(R.id.bt_pinch_a_post);
        bt_pinch_ques = (Button) findViewById(R.id.bt_pinch_a_question);
        bt_club_page = (Button) findViewById(R.id.bt_club_profile_page);
        bt_notification = (Button) findViewById(R.id.bt_notification_page_layout);

        bt_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*launchHomeScreen();*/
                startActivity(new Intent(ContainerLayouts.this, ProfileActivity.class));
                //finish();
            }
        });

        bt_pinch_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*launchHomeScreen();*/
                startActivity(new Intent(ContainerLayouts.this, PinchAQues.class));
                //finish();
            }
        });

    }
}

