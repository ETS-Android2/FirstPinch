/*
package www.firstpinch.com.firstpinch2;

*/
/**
 * Created by Rianaa Admin on 24-10-2016.
 *//*


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.internal.Utility;
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.mycollege.mait.AnalyticsApplication;
import com.mycollege.mait.ApplicationConstants;
import com.mycollege.mait.GCMMain;
import com.mycollege.mait.JsonParsing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

*/
/**
 * Created by anshul garg on 24/8/16.
 *//*

public class Test extends AppCompatActivity {

    AutoCompleteTextView univ, coll, cour, bran, yr, dep;

    JSONObject json;
    String universityjson, collegejson, coursejson, branchjson, departjson;
    String university[];
    //String college[];
    // ArrayAdapter ar_obj1;
    public static String unislug, collslug, courslug;


    int i = 0, m = 0;

    //strings to get shared pref data
    String gname, gemail, gurl;
    String fbname, fbemail, fbid, fbplink;

    //EditText emailid;
    Spinner identity;
    TextView tv;
    EditText societyname;
    LinearLayout ll_coll, ll_cour, ll_bran, ll_yr, ll_university, ll_department, ll_societyn;
    Button b_submit;
    //List<NameValuePair> nameValuePairs;
    //HttpPost httppost;
    StringBuffer buffer;
    //HttpResponse response;
    // HttpClient httpclient;
    InputStream inputStream;
    byte[] data;
    ProgressDialog pDialog;
    public static Activity huu;
    //String SignUp="";
    GCMMain gcmMain;
    GoogleCloudMessaging gcmObj;
    RequestParams params = new RequestParams();
    String regId = "";
    public static final String REG_ID = "regId";
    public static final String EMAIL_ID = "eMailId";
    public static final String UNIV = "univer";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Context context = this;
    ArrayAdapter<String> adapter1 = null;
    ArrayAdapter<String> adapter2 = null;
    ArrayAdapter<String> adapter3 = null;
    ArrayAdapter<String> adapter4 = null;
    ArrayAdapter<String> adapter5 = null;
    ArrayAdapter<String> arrayAdapter = null;
    String identify;
    String sociname;
    int loc_univer, loc_college, loc_course, loc_branch, loc_year;
    private Tracker mTracker;
    Utility utility;
    Boolean check;
    //String university[]={"GGSIPU","DU","MRU","DTU","guru gobind singh indraprasth university"};
    String college[] = {"MAIT", "NIEC", "MSIT", "NSIT"};
    String course[] = {"BTECH", "BSC", "MSC", "MTECH"};
    String branch[] = {"IT", "CSE", "MAE", "ECE"};
    String year[] = {"1", "2", "3", "4"};
    String department[] = {"CSE", "IT", "ECE", "MAE"};
    String unidata = null, colldata = null, courdata = null, brandata = null, yrdata = null, depdata = null;
    int unil = 0, colll = 0, courl = 0, branl = 0, yrl = 0, depl = 0, socinl = 0;
    //google api client
    private GoogleApiClient mGoogleApiClient;

    //for slug
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_prefer);
        getSupportActionBar().hide();
        new Data_getuni().execute();

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();


        SharedPreferences sp = getSharedPreferences("prefs",
                Activity.MODE_PRIVATE);


        final SharedPreferences sp1 = getSharedPreferences("prefs", Activity.MODE_PRIVATE);

        //context = getApplicationContext();
        huu = this;
        ll_coll = (LinearLayout) findViewById(R.id.ll_college);
        ll_cour = (LinearLayout) findViewById(R.id.ll_course);
        ll_bran = (LinearLayout) findViewById(R.id.ll_branch);
        ll_yr = (LinearLayout) findViewById(R.id.ll_year);
        ll_university = (LinearLayout) findViewById(R.id.ll_university);
        ll_department = (LinearLayout) findViewById(R.id.ll_department);
        ll_societyn = (LinearLayout) findViewById(R.id.ll_societyn);
        tv = (TextView) findViewById(R.id.tv_noinfo);

        b_submit = (Button) findViewById(R.id.p_submit);


        identity = (Spinner) findViewById(R.id.you_are);//fetch the spinner from layout file
        adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.you_r));//setting the country_array to spinner
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        identity.setAdapter(adapter1);
        //if you want to set any action you can do in this listener
        identity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long id) {
                identify = parent.getItemAtPosition(position).toString();
                Log.d("Identify", "" + identify);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);


                if (identify.contentEquals("Student")) {
                    //Toast.makeText(this, "now set visible", Toast.LENGTH_SHORT).show();
                    ll_university.setVisibility(View.VISIBLE);
                    ll_coll.setVisibility(View.VISIBLE);
                    ll_cour.setVisibility(View.VISIBLE);
                    ll_bran.setVisibility(View.VISIBLE);
                    ll_yr.setVisibility(View.VISIBLE);
                    ll_department.setVisibility(View.GONE);
                    ll_societyn.setVisibility(View.GONE);
                } else if (identify.contentEquals("Teacher")) {
                    //Toast.makeText(this, "now set visible", Toast.LENGTH_SHORT).show();
                    ll_university.setVisibility(View.VISIBLE);
                    ll_coll.setVisibility(View.VISIBLE);
                    ll_department.setVisibility(View.VISIBLE);
                    ll_cour.setVisibility(View.GONE);
                    ll_bran.setVisibility(View.GONE);
                    ll_yr.setVisibility(View.GONE);
                    ll_societyn.setVisibility(View.GONE);
                } else if (identify.contentEquals("Society")) {
                    //Toast.makeText(this, "now set visible", Toast.LENGTH_SHORT).show();
                    ll_societyn.setVisibility(View.VISIBLE);
                    ll_university.setVisibility(View.VISIBLE);
                    ll_coll.setVisibility(View.VISIBLE);
                    ll_department.setVisibility(View.GONE);
                    ll_cour.setVisibility(View.GONE);
                    ll_bran.setVisibility(View.GONE);
                    ll_yr.setVisibility(View.GONE);
                    //  ll_department.setVisibility(View.VISIBLE);
                }


            }


            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        societyname = (EditText) findViewById(R.id.p_societyname);


        // coll = (Spinner) findViewById(R.id.p_college);
        //String[] data = university.toArray(new String[university.size()]);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        //Log.d("checkuniversity","" +university);
        univ = (AutoCompleteTextView) findViewById(R.id.p_univer);

        univ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                unidata = univ.getText().toString();
                Log.d("universitydata", "" + unidata);
                unislug = toSlug(unidata);
                Log.d("unislug", "" + unislug);
                unil = unidata.length();
                new Data_getcoll().execute();
            }
        });

        coll = (AutoCompleteTextView) findViewById(R.id.p_college);

        coll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                colldata = coll.getText().toString();
                Log.d("collegedata", "" + colldata);
                collslug = toSlug(colldata);
                colll = colldata.length();
                Log.d("collegeslud", "" + collslug);

                if (identify.contentEquals("Teacher")) {
                    new Data_getdept().execute();
                } else {
                    new Data_getcour().execute();
                }
            }
        });


        cour = (AutoCompleteTextView) findViewById(R.id.p_course);

            */
/*ArrayAdapter ar_obj3 = new ArrayAdapter<String>(Prefer.this, android.R.layout.select_dialog_item, course);
            cour.setAdapter(ar_obj3);*//*

        cour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                courdata = cour.getText().toString();
                Log.d("coursedata", "" + courdata);
                courslug = toSlug(courdata);
                Log.d("courslslda", "" + courslug);
                new Data_getbran().execute();
                courl = courdata.length();
            }
        });


        bran = (AutoCompleteTextView) findViewById(R.id.p_branch);
            */
/*ArrayAdapter ar_obj4 = new ArrayAdapter<String>(Prefer.this, android.R.layout.select_dialog_item, branch);
            bran.setAdapter(ar_obj4);*//*

        bran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                brandata = bran.getText().toString();
                Log.d("branchdata", "" + brandata);

                branl = brandata.length();
            }
        });


        yr = (AutoCompleteTextView) findViewById(R.id.p_year);
        ArrayAdapter ar_obj5 = new ArrayAdapter<String>(Prefer.this, android.R.layout.select_dialog_item, year);
        yr.setAdapter(ar_obj5);
        yr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                yrdata = yr.getText().toString();
                Log.d("yeardata", "" + yrdata);
                yrl = yrdata.length();
            }
        });


        dep = (AutoCompleteTextView) findViewById(R.id.p_department);
        ArrayAdapter ar_obj6 = new ArrayAdapter<String>(Prefer.this, android.R.layout.select_dialog_item, department);
        dep.setAdapter(ar_obj6);
        dep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                depdata = dep.getText().toString();
                Log.d("departmentdata", "" + depdata);
                depl = depdata.length();
            }
        });

        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               */
/* LoginTask login = new LoginTask();
                login.execute();*//*

                sociname = societyname.getText().toString();
                socinl = sociname.length();
                Log.d("socinl", "" + socinl);


                check = feed();
                // check = true;
                if (check.equals(true)) {
                    registerInBackground();
                    //save value in shared preference of user
                    mainshared();
                    Intent i = new Intent(Prefer.this, SignUp.class);
                    startActivity(i);
                }


            }

        });


    }


    //slug function
    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
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
        super.onStart();
    }

    //get shared pref data
    public void put1() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        gname = pref.getString("kname", "");
        gemail = pref.getString("kemail", "");
        gurl = pref.getString("kurl", "");
        i = pref.getInt("integer", 0);

        Log.d("check the value a :", "" + gname);
        Log.d("check the value b :", "" + gemail);
        Log.d("check the value u :", "" + gurl);
        Log.i("i", "" + i);


    }

    public void putt() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreference", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        fbname = pref.getString("name", "");
        fbemail = pref.getString("email", "");
        fbid = pref.getString("fid", "");
        fbplink = pref.getString("plink", "");
        m = pref.getInt("int", 0);

        Log.d("check the value 1 :", "" + fbname);
        Log.d("check the value 2 :", "" + fbemail);
        Log.d("check the value 3 :", "" + fbid);
        Log.d("check the value m :", "" + m);
    }

    @Override
    public void onBackPressed() {
        putt();
        put1();


        if (m == 20) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            LoginManager.getInstance().logOut();
            Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            SharedPreferences settings = context.getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
            settings.edit().clear().commit();

            SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("state", false);
            editor.commit();

            SharedPreferences settings1 = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            settings1.edit().clear().commit();

            Intent i = new Intent(Prefer.this, FbLogin.class);
            startActivity(i);

        } else if (i == 10) {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                            Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                            SharedPreferences settings = context.getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                            settings.edit().clear().commit();

                            SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("state", false);
                            editor.commit();

                            SharedPreferences settings1 = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                            settings1.edit().clear().commit();

                            Intent i = new Intent(getApplicationContext(), FbLogin.class);
                            startActivity(i);
                        }
                    });
        }


      */
/*  moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);*//*



    }

    public Boolean feed() {
        if (identify.equals("Student")) {
            if (unil == 0) {
                univ.setError("*Select from given");
                //Log.d("unice","" +"empty");
                return false;
            }

            if (colll == 0) {
                coll.setError("*Required Field");
                return false;
            }

            if (courl == 0) {
                cour.setError("*Required Field");
                return false;
            }

            if (branl == 0) {
                bran.setError("*Required Field");
                return false;
            }

            if (yrl == 0) {
                yr.setError("*Required Field");
                return false;
            }
        } else if (identify.equals("Teacher")) {
            if (unil == 0) {
                univ.setError("*Required Field");
                return false;
            }

            if (colll == 0) {
                coll.setError("*Required Field");
                return false;
            }

            if (depl == 0) {
                dep.setError("*Required Field");
                return false;
            }

        } else if (identify.equals("Society")) {
            if (unil == 0) {
                univ.setError("*Required Field");
                return false;
            }

            if (colll == 0) {
                coll.setError("*Required Field");
                return false;
            }

            if (socinl == 0) {
                societyname.setError("*Required Field");
                return false;
            }

        }


        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Prefer Activity", "Setting screen name: " + " Prefer");
        mTracker.setScreenName("Image~" + " Prefer");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    // AsyncTask to register Device in GCM Server
    public void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(Prefer.this);
                pDialog.setMessage("Validating...");
                pDialog.show();

            }


            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcmObj
                            .register(ApplicationConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    storeRegIdinSharedPref(regId, depdata, unidata, colldata, courdata, brandata, yrdata);
                    //Toast.makeText(getApplicationContext(), "Registered with GCM Server successfully.\n"+msg,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            //"Reg ID Creation Failed." +
                            "\n\nMake sure you enabled Internet and try registering again after some time."
                                    + "", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    // Store RegId and Email entered by User in SharedPref
    private void storeRegIdinSharedPref(String regId, String dept, String univer, String college
            , String course, String branch, String year) {
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        // editor.putString(EMAIL_ID, SignUp);
        editor.putString("univer", unidata);
        editor.putString("coll", colldata);
        editor.putString("course", courdata);
        editor.putString("branch", brandata);
        editor.putString("year", yrdata);
        editor.putString("dept", depdata);
        editor.putString("societyname", sociname);

        editor.commit();
        storeRegIdinServer(regId, dept, univer, college, course, branch, year);

    }


    // Share RegID and Email ID with GCM Server Application (Php)
    private void storeRegIdinServer(String regId2, String dept, String univer, String college
            , String course, String branch, String year) {
        //prgDialog.show();
        // params.put("eMailId", SignUp);
        params.put("univer", unidata);
        params.put("coll", colldata);
        params.put("course", courdata);
        params.put("branch", brandata);
        params.put("year", yrdata);
        params.put("dept", depdata);
        params.put("societyname", sociname);
        params.put("regId", regId);
        System.out.println(" Reg Id = " + regId);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle regId1 = client.post(ApplicationConstants.APP_SERVER_URL, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        //Toast.makeText(context, "Reg Id shared successfully with Web App ",Toast.LENGTH_LONG).show();


                        */
/*Log.d("login", "Logged");
                        Toast.makeText(Prefer.this, "Logged In", Toast.LENGTH_SHORT).show();
                        SharedPreferences sp = getSharedPreferences("login", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("state", false);
                        editor.commit();*//*


                        Intent intent = new Intent("android.intent.action.HOMELOGGED");
                        startActivity(intent);


                        if (pDialog.isShowing())
                            pDialog.dismiss();

                        //new checkDetails().execute();

                    }
                });
    }

    // Check if Google Playservices is installed in Device or not
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
               */
/* Toast.makeText(
                        context,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();*//*

                finish();
            }
            return false;
        } else {
            */
/*Toast.makeText(
                    context,
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();*//*

        }
        return true;
    }

    private class Data_getuni extends AsyncTask<String, String, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... strings) {

            String url = "http://52.88.175.7/universities/";
            Log.d("URL", "" + url);

            json = JsonParsing.getJSONfromURL(url);
            Log.d("JSON", "" + json);

            JSONArray jsonArray = null;
            try {
                jsonArray = json.getJSONArray("universities");
                Log.d("jsonarray", ">" + jsonArray);
                int length = jsonArray.length();
                Log.d("length", "" + length);

                universityjson = json.getJSONArray("universities").toString();
                universityjson = universityjson.replaceAll("\\[|\\]", "").replaceAll("\"", "");
                Log.d("check", "" + universityjson);
                university = universityjson.split(",");
                */
/*String checkuni = university[0];
                Log.d("checkuni", "" +checkuni);*//*

                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObj) {
            try {

                ArrayAdapter ar_obj1 = new ArrayAdapter<String>(Prefer.this, android.R.layout.select_dialog_item, university);
                univ.setAdapter(ar_obj1);

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObj);
        }
    }

    private class Data_getcoll extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {


            String url = "http://52.88.175.7/" + unislug + "/college/";
            Log.d("URL", "" + url);

            json = JsonParsing.getJSONfromURL(url);
            Log.d("JSON", "" + json);

            JSONArray jsonArray = null;
            try {
                jsonArray = json.getJSONArray("colleges");
                Log.d("jsonarray", ">" + jsonArray);
                int length = jsonArray.length();
                Log.d("length", "" + length);

                collegejson = json.getJSONArray("colleges").toString();
                collegejson = collegejson.replaceAll("\\[|\\]", "").replaceAll("\"", "");
                Log.d("check", "" + collegejson);
                college = collegejson.split(",");
                */
/*String checkuni = university[0];
                Log.d("checkuni", "" +checkuni);*//*

                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObj) {
            try {

                ArrayAdapter ar_obj2 = new ArrayAdapter<String>(Prefer.this, android.R.layout.select_dialog_item, college);
                coll.setAdapter(ar_obj2);

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObj);
        }
    }

    private class Data_getcour extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {


            String url = "http://52.88.175.7/courses/";
            Log.d("URL", "" + url);
            json = JsonParsing.getJSONfromURL(url);
            Log.d("JSON", "" + json);

            JSONArray jsonArray = null;
            try {
                jsonArray = json.getJSONArray("courses");
                Log.d("jsonarray", ">" + jsonArray);
                int length = jsonArray.length();
                Log.d("length", "" + length);

                coursejson = json.getJSONArray("courses").toString();
                coursejson = coursejson.replaceAll("\\[|\\]", "").replaceAll("\"", "");
                Log.d("check", "" + coursejson);
                course = coursejson.split(",");
                */
/*String checkuni = university[0];
                Log.d("checkuni", "" +checkuni);*//*

                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObj) {
            try {

                ArrayAdapter ar_obj3 = new ArrayAdapter<String>(Prefer.this, android.R.layout.select_dialog_item, course);
                cour.setAdapter(ar_obj3);

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObj);
        }
    }

    private class Data_getbran extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {


            String url = "http://52.88.175.7/" + courslug + "/branches";
            Log.d("URL", "" + url);
            json = JsonParsing.getJSONfromURL(url);
            Log.d("JSON", "" + json);

            JSONArray jsonArray = null;
            try {
                jsonArray = json.getJSONArray("branches");
                Log.d("jsonarray", ">" + jsonArray);
                int length = jsonArray.length();
                Log.d("length", "" + length);

                branchjson = json.getJSONArray("branches").toString();
                branchjson = branchjson.replaceAll("\\[|\\]", "").replaceAll("\"", "");
                Log.d("check", "" + branchjson);
                branch = branchjson.split(",");
                */
/*String checkuni = university[0];
                Log.d("checkuni", "" +checkuni);*//*

                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObj) {
            try {

                ArrayAdapter ar_obj4 = new ArrayAdapter<String>(Prefer.this, android.R.layout.select_dialog_item, branch);
                bran.setAdapter(ar_obj4);

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObj);
        }
    }

    private class Data_getdept extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {


            String url = "http://52.88.175.7/departments/";
            Log.d("URL", "" + url);
            json = JsonParsing.getJSONfromURL(url);
            Log.d("JSON", "" + json);

            JSONArray jsonArray = null;
            try {
                jsonArray = json.getJSONArray("departments");
                Log.d("jsonarray", ">" + jsonArray);
                int length = jsonArray.length();
                Log.d("length", "" + length);

                departjson = json.getJSONArray("departments").toString();
                departjson = departjson.replaceAll("\\[|\\]", "").replaceAll("\"", "");
                Log.d("check", "" + departjson);
                department = departjson.split(",");
                */
/*String checkuni = university[0];
                Log.d("checkuni", "" +checkuni);*//*

                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObj) {
            try {

                ArrayAdapter ar_obj6 = new ArrayAdapter<String>(Prefer.this, android.R.layout.select_dialog_item, department);
                dep.setAdapter(ar_obj6);

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObj);
        }
    }

    public void mainshared() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MainShared", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("you_are", identify);
        editor.putString("university", unidata);
        editor.putString("college", colldata);
        editor.putString("course", courdata);
        editor.putString("branch", brandata);
        editor.putString("year", yrdata);
        editor.putString("societyname", sociname);
        editor.putString("department", depdata);
        editor.putString("reg_id", regId);
        editor.commit();

        Log.i("check the course :", "" + courdata);
        Log.d("check the regid :", "" + regId);


    }


}
*/
