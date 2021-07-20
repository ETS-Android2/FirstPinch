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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

/**
 * Created by Rianaa Admin on 25-08-2016.
 */

public class SelectInterests extends AppCompatActivity {


    public static String interest, result = "", GET_INTERESTS_URL = "http://54.169.84.123/api/interest",
            POST_INTERESTS_URL = "http://54.169.84.123/api/user_interest/apply";
    int count, user_id;
    Boolean status = false;
    //SelectedInterestsObject selectedHouses = new SelectedInterestsObject();
    //ArrayList<SelectedInterestsObject> arListselHouses = new ArrayList<SelectedInterestsObject>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    private RecyclerView recyclerView;
    private SelectInterestsRecyclerAdapter recyclerAdapter;
    List<SelectedInterestsObject> data = new ArrayList<SelectedInterestsObject>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectinterests);
        Log.i("Unit Testing","SelectInterests oncreate");
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        TextView headerTextStyle = (TextView) findViewById(R.id.headerTextFont);
        TextView bodyTextStyle = (TextView) findViewById(R.id.bodyTextFont);


        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");


        headerTextStyle.setTypeface(custom_font);
        bodyTextStyle.setTypeface(custom_font1);
        //GridView gridview = (GridView) findViewById(R.id.gridview);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        final Button btn_continue = (Button) findViewById(R.id.btn_continue);
        btn_continue.setTypeface(custom_font2);
        //selectedHouses = new SelectedInterestsObject();
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        /*int spanCount = 2; // 3 columns
        int spacing = 8; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));*/
        /*RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(3000);
        itemAnimator.setRemoveDuration(3000);
        recyclerView.setItemAnimator(itemAnimator);*/

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Unit Testing","Select Interests button onclick");
                count = 0;
                List<SelectedInterestsObject> list = recyclerAdapter.getList();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getCheck()) {
                        count++;
                    }
                }
                Log.e("count=", "" + count);
                if (count >= 3) {
                    interest = "";
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getCheck()) {
                            interest = interest.concat(list.get(i).getId() + ",");
                        }
                    }
                    int len = interest.length();
                    char[] arr = interest.toCharArray();
                    arr[len - 1] = ' ';
                    interest = String.valueOf(arr).trim();
                    Log.e("interests are = ", interest);
                    postInterestsInDb(interest, user_id);

                } else {
                    Toast.makeText(SelectInterests.this, "Choose "+(3-count)+" more interests!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        get_interests();

    }

    private void postInterestsInDb(final String interest, final int user_id) {
        StringRequest myReq = new StringRequest(Request.Method.POST,
                POST_INTERESTS_URL,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {

            @Override
            public byte[] getBody() {
                String str = "{\"user\":{\"user_id\":\"" + user_id + "\"" +
                        ",\"interests_id\":\"" + interest + "\"}}";
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SelectInterests", "data from server - " + response);
                if (response != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        // looping through All Contacts

                        status = jsonObj.getBoolean("status");
                        //if(status) {
                        SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = pref2.edit();
                        editor2.putBoolean("state", true);
                        editor2.commit();
                        SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                        SharedPreferences.Editor editor4 = pref4.edit();
                        editor4.putBoolean("state", false);
                        editor4.commit();
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), Main.class);
                        startActivity(intent);
                        finish();
                        /*}else{
                            Toast.makeText(getApplicationContext(),
                                    "error",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }*/

                    } catch (final JSONException e) {
                        Log.e("SelectInterests", "Json parsing error: " + e.getMessage());
                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });*/
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(SelectInterests.this, e.toString());

                    }
                } else {
                    Log.e("SelectInterests", "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
                progressDialog.dismiss();
            }
        };
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SignUp", "error connect - " + error);
                progressDialog.dismiss();
            }
        };
    }

    private void get_interests() {
        StringRequest stringRequest = new StringRequest(GET_INTERESTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(SelectInterests.this, response, Toast.LENGTH_LONG).show();
                        result = response;
                        try {
                            JSONObject jsonObj = new JSONObject(result);
                            JSONArray arr = jsonObj.getJSONArray("interests");
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject c = arr.getJSONObject(i);
                                SelectedInterestsObject current = new SelectedInterestsObject();
                                current.setId(c.getString("id"));
                                current.setHouseName(c.getString("title"));
                                current.setImageurl(c.getString("image"));
                                current.setCheck(false);
                                data.add(current);
                            }
                        } catch (JSONException e) {
                            Log.e("INTERESTS", "" + e.toString());
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(SelectInterests.this, e.toString());
                        }

                        recyclerAdapter = new SelectInterestsRecyclerAdapter(getApplicationContext(), data);
                        recyclerView.setAdapter(recyclerAdapter);
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(SelectInterests.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("INTERESTS", "" + error);
                        progressDialog.dismiss();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Interests...");
        progressDialog.show();

    }

    public static List<SelectedInterestsObject> getData() throws JSONException {
        List<SelectedInterestsObject> data = new ArrayList<SelectedInterestsObject>();
        String[] interestList = {"Health & Fitness", "Beauty & Personal care", "Home & Living", "Travel & Outdoors", "Trivia",
                "Children & parenting", "New Moms & Toddlers", "Quotes & More", "Books & Literature"};
        String[] imgurl = {"http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/79/normal/1c174cd5a9570dfa9fe62f1115f93336.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/79/normal/1c174cd5a9570dfa9fe62f1115f93336.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/normal/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/normal/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/94/normal/Word_Search.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/4/normal/Homeopathy___Ayurveda.jpg",
                "http://s3.amazonaws.com/cdn.firstpinch.com/spree/club_pic/79/normal/1c174cd5a9570dfa9fe62f1115f93336.jpg"};

        JSONObject jsonObj = new JSONObject(result);

        for (int i = 0; i < jsonObj.length(); i++) {
            SelectedInterestsObject current = new SelectedInterestsObject();
            current.setHouseName(jsonObj.getString("title"));
            current.setImageurl(jsonObj.getString("image"));
            current.setCheck(false);
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

