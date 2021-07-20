package www.firstpinch.com.firstpinch2.Settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import www.firstpinch.com.firstpinch2.MyLinearLayoutManager;
import www.firstpinch.com.firstpinch2.R;
import www.firstpinch.com.firstpinch2.Search.SearcActivityProfileAdapter;
import www.firstpinch.com.firstpinch2.Search.SearchActivityObject;
import www.firstpinch.com.firstpinch2.Search.SearchActivityOptionsAdapter;

/**
 * Created by Rianaa Admin on 03-10-2016.
 */

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSettings;
    private SettingsAdapter recyclerSettingsAdapter;
    List<SettingsObject> data = new ArrayList<SettingsObject>();
    Toolbar toolbar;
    String login_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingsactivity);
        Log.e("log-", "onCreate");
        SharedPreferences sp = getSharedPreferences("logintype",
                Activity.MODE_PRIVATE);
        login_type = sp.getString("type","");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerViewSettings = (RecyclerView) findViewById(R.id.settings_recyclerview);
        recyclerSettingsAdapter = new SettingsAdapter(this, getData());
        recyclerViewSettings.setAdapter(recyclerSettingsAdapter);
        recyclerViewSettings.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        this.overridePendingTransition(R.anim.right_to_left,
                R.anim.do_nothing);

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
        this.overridePendingTransition(R.anim.do_nothing,
                R.anim.left_to_right);
    }

    public List<SettingsObject> getData() {
        List<SettingsObject> data = new ArrayList<SettingsObject>();
        if(login_type.contentEquals("social")){
            String settingTitleList[] = {"Change Username"};
            for (int i = 0; i < settingTitleList.length; i++) {
                SettingsObject current = new SettingsObject();
                current.setSetting_title(settingTitleList[i]);
                data.add(current);
            }
        }else{
            String settingTitleList[] = {"Change Password", "Change Username"};
            for (int i = 0; i < settingTitleList.length; i++) {
                SettingsObject current = new SettingsObject();
                current.setSetting_title(settingTitleList[i]);
                data.add(current);
            }
        }
        return data;
    }
}
