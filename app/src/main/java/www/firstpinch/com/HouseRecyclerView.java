package www.firstpinch.com.firstpinch2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import www.firstpinch.com.firstpinch2.MainFeed.MainFeedRecyclerView;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileAdapter;
import www.firstpinch.com.firstpinch2.ProfilePages.YourHousesProfileObject;

/**
 * Created by Rianaa Admin on 06-09-2016.
 */
public class HouseRecyclerView extends AppCompatActivity {

    public static String house, result = "", GET_HOUSES_URL = "http://54.169.84.123/api/choose_clubs",
            POST_JOIN_HOUSE_URL="http://54.169.84.123/api/user_clubs";
    ;
    int count,user_id;
    private RecyclerView recyclerView;
    private ChooseHousesAdapter recyclerAdapter;
    List<YourHousesProfileObject> data = new ArrayList<YourHousesProfileObject>();
    Button btn_go, btn_go_bottom;
    ProgressDialog progressDialog;
    Boolean status=false;
    View greysLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_recyclerview);
        Log.i("Unit Testing","HouseRecyclerView oncreate");
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Cabin-Regular.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Lato-Regular.ttf");

        TextView chooseHouseheaderTextStyle = (TextView)findViewById(R.id.chooseHouseHeaderText);
        TextView chooseHousebodyTextStyle = (TextView)findViewById(R.id.textView);
        greysLine = (View) findViewById(R.id.greys_line);

        chooseHouseheaderTextStyle.setTypeface(custom_font);
        chooseHousebodyTextStyle.setTypeface(custom_font1);
        recyclerView = (RecyclerView) findViewById(R.id.house_recyclerview);
        btn_go = (Button) findViewById(R.id.btn_go);
        //btn_go_bottom = (Button) findViewById(R.id.btn_go_bottom);
        //selectedHouses = new SelectedInterestsObject();
        recyclerAdapter = new ChooseHousesAdapter(this, data);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        /*int spanCount = 2; // 3 columns
        int spacing = 4; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));*/


        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Unit Testing","Select House button onclick");
                List<YourHousesProfileObject> list = recyclerAdapter.getList();
                count = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getJoin_exit() == 1) {
                        count++;
                    }
                }
                Log.e("count=", "" + count);
                if (count >= 3) {
                    house = "";
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getJoin_exit() == 1) {
                            house = house.concat(list.get(i).getYourHouseName() + ",\n");
                        }
                    }
                    int len = house.length();
                    char[] arr = house.toCharArray();
                    arr[len - 1] = ' ';
                    house = String.valueOf(arr);
                    Log.e("houses are = ", house);
                    SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                    SharedPreferences.Editor editor4 = pref4.edit();
                    editor4.putBoolean("state", true);
                    editor4.commit();
                    Intent intent = new Intent(getApplicationContext(), Main.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(HouseRecyclerView.this, "Choose "+(3-count)+" more Houses!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        get_houses();


    }

    private void get_houses() {
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        int user_id = sp.getInt("id", 0);
        StringRequest stringRequest = new StringRequest(GET_HOUSES_URL + "?user_id=" + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(SelectInterests.this, response, Toast.LENGTH_LONG).show();
                        result = response;
                        try {
                            JSONObject jsonObj = new JSONObject(result);
                            JSONArray arr = jsonObj.getJSONArray("userinterestsandclubs");
                            int house_count=0;
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject c = arr.getJSONObject(i);
                                JSONArray c2 = c.getJSONArray("clubs");
                                Log.e("c2=", "" + c2);
                                for(int j=0;j<c2.length();j++,house_count++){
                                    YourHousesProfileObject current = new YourHousesProfileObject();
                                    JSONObject c3 = c2.getJSONObject(j);
                                    current.setId(c3.getString("id"));
                                    current.setYourHouseName(c3.getString("name"));
                                    current.setYourHouseImageUrl(c3.getString("image"));
                                    current.setYourHouseDesc(c3.getString("description"));
                                    current.setJoin_exit(c3.getInt("status"));
                                    data.add(current);
                                }

                            }
                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(HouseRecyclerView.this, e.toString());
                        }

                        recyclerAdapter = new ChooseHousesAdapter(getApplicationContext(), data);
                        recyclerView.setAdapter(recyclerAdapter);
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HouseRecyclerView.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Houses...");
        progressDialog.show();
    }

    public static List<YourHousesProfileObject> getData() {
        List<YourHousesProfileObject> data = new ArrayList<YourHousesProfileObject>();
        String yourHouseImageUrlList[] = {"http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg"};
        String yourHouseNameList[] = {"Decor Ideas", "My Travel Diaries", "Fitness & Health", "Decor Ideas", "My Travel Diaries",
                "Fitness & Health", "My Travel Diaries", "Fitness & Health", "Decor Ideas", "My Travel Diaries", "Fitness & Health", "Decor Ideas",
                "My Travel Diaries", "Fitness & Health"};
        String yourHouseDescList[] = {"In descriptive writing, the author does not tell the reader what was seen, " +
                "felt, tested, smelled, or heard.  Rather, he describes something that he   ",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg" +
                        "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg" +
                        "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg" +
                        "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg" +
                        "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/original/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg"};
        Integer join_exitList[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < yourHouseNameList.length && i < join_exitList.length; i++) {
            YourHousesProfileObject current = new YourHousesProfileObject();
            current.setYourHouseName(yourHouseNameList[i]);
            current.setYourHouseImageUrl(yourHouseImageUrlList[i]);
            current.setJoin_exit(join_exitList[i]);
            current.setYourHouseDesc(yourHouseDescList[i]);
            data.add(current);
        }
        return data;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.ic_dialog_alert);
                builder.setTitle("Closing Activity");
                builder.setMessage("Are you sure you want to close this activity?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                });
                builder.setNegativeButton("No", null);
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.GREEN);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.RED);
        count = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        count = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        count = 0;
    }
}
