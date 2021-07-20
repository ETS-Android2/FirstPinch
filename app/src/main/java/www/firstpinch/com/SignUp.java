package www.firstpinch.com.firstpinch2;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.GCM.GCMRegistrationIntentService;

/**
 * Created by Rianaa Admin on 25-08-2016.
 */
public class SignUp extends AppCompatActivity {

    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Button btn_signup;
    TextView termsconditions, tv_and, tv_privacy;
    String link, REGISTER_URL = "http://54.169.84.123/api/users", USERNAME_CHECK_URL = "http://54.169.84.123/api/checkuname",
            USERNAME_CHECK_EMAILID = "http://54.169.84.123/api/checkemail";
    Intent intent;
    EditText emailid, full_name, username, password;
    int male_female;
    View radioButton, greydline;
    int radioButtonID;
    RadioGroup radioGroup;
    ProgressDialog progressDialog;
    String token, android_id, release, psswrd;
    int sdkVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        release = Build.VERSION.RELEASE;
        sdkVersion = Build.VERSION.SDK_INT;

        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

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
                    //Displaying the token as toast
                    Log.e("Registration token : ", token);
                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Log.e("GCM registration error!", "SignUp.java");
                } else {
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

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Cabin-Regular.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");

        btn_signup = (Button) findViewById(R.id.button_signup2);
        emailid = (EditText) findViewById(R.id.edittext_emailid);
        full_name = (EditText) findViewById(R.id.edittext_fullname);
        username = (EditText) findViewById(R.id.edittext_username);
        password = (EditText) findViewById(R.id.edittext_password);
        radioGroup = (RadioGroup) findViewById(R.id.ll_radiobutton);


        //male_female++;

        TextView radioBtnTextStyle1 = (TextView) findViewById(R.id.radioMale);
        TextView radioBtnTextStyle12 = (TextView) findViewById(R.id.radioFemale);
        greydline = (View) findViewById(R.id.greyd_line);

        radioBtnTextStyle1.setTypeface(custom_font1);
        radioBtnTextStyle12.setTypeface(custom_font1);

        btn_signup.setTypeface(custom_font1);
        emailid.setTypeface(custom_font1);
        full_name.setTypeface(custom_font1);
        username.setTypeface(custom_font1);
        password.setTypeface(custom_font1);


        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                check_username();
            }
        });

        emailid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                check_emailid();
            }
        });

        termsconditions = (TextView) findViewById(R.id.text_termsandconditions3);
        tv_and = (TextView) findViewById(R.id.text_termsandconditions5);
        tv_privacy = (TextView) findViewById(R.id.text_termsandconditions4);
        TextView termsconditionsby = (TextView) findViewById(R.id.text_termsandconditions2);
        termsconditions.setTypeface(custom_font1);
        tv_privacy.setTypeface(custom_font1);
        tv_and.setTypeface(custom_font1);
        termsconditionsby.setTypeface(custom_font1);


        SpannableString content = new SpannableString(termsconditions.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        termsconditions.setText(content);
        SpannableString content2 = new SpannableString(tv_privacy.getText());
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        tv_privacy.setText(content2);
        termsconditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                link = "https://www.firstpinch.com/terms?type=mobile";
                intent = new Intent("android.intent.action.WEBVIEW_COMMON");
                SharedPreferences prefs = getSharedPreferences("webviewurl", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("url", link);
                editor.commit();
                startActivity(intent);
            }
        });
        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                link = "https://www.firstpinch.com/privacy?type=mobile";
                intent = new Intent("android.intent.action.WEBVIEW_COMMON");
                SharedPreferences prefs = getSharedPreferences("webviewurl", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("url", link);
                editor.commit();
                startActivity(intent);
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*launchHomeScreen();*/
                Log.i("Unit Testing","Sign Up button onclick");
                if (check_pref()) {
                    //check_username();
                    registerUser();
                }
            }
        });
        //new FetchFromServerTask().execute("https://jsonplaceholder.typicode.com/posts/1");

    }

    public Boolean check_pref() {

        if (emailid.getText().toString().trim().length() == 0) {
            emailid.setError("*Required Field");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailid.getText().toString().trim()).matches()) {
            emailid.setError("*Enter Valid Email");
            return false;
        }
        if (full_name.getText().toString().trim().length() == 0) {
            full_name.setError("*Required Field");
            return false;
        }
        if (username.getText().toString().trim().length() == 0) {
            username.setError("*Required Field");
            return false;
        }
        if (username.getText().toString().contains(" ")) {
            username.setError("*Blank Spaces Not Allowed");
            return false;
        }
        if (!username.getText().toString().matches("[a-zA-Z_1234567890]*")) {
            username.setError("Alpha-Numeric characters or _ is allowed");
            return false;
        }
        if (password.getText().toString().trim().length() < 6) {
            password.setError("*six char mini.");
            return false;
        }
        return true;
    }

    private void check_username() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                USERNAME_CHECK_URL + "?uname=" + username.getText().toString().trim(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(SignUp.this, response, Toast.LENGTH_LONG).show();
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(response);
                            if (jsonObj.getBoolean("status")) {
                                username.setError("*Username already Exists!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(SignUp.this, e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUp.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void check_emailid() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                USERNAME_CHECK_EMAILID + "?email=" + emailid.getText().toString().trim(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("email check : ", "" + response);
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(response);
                            if (jsonObj.getString("status").contentEquals("1")) {
                                emailid.setError("*Email ID already Exists. Please Login!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUp.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void registerUser() {

        final String str_username = username.getText().toString().trim();
        psswrd = password.getText().toString().trim();
        final String str_email_id = emailid.getText().toString().trim();
        final String str_fullname = full_name.getText().toString().trim();
        radioButtonID = radioGroup.getCheckedRadioButtonId();
        radioButtonID = radioGroup.getCheckedRadioButtonId();
        radioButton = radioGroup.findViewById(radioButtonID);
        male_female = radioGroup.indexOfChild(radioButton);
        male_female++;
        Log.e("", "");


        StringRequest myReq = new StringRequest(Request.Method.POST,
                REGISTER_URL,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {

                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("uname", str_username);
                questionHash.put("name", str_fullname);
                questionHash.put("gender", String.valueOf(male_female));
                questionHash.put("email", str_email_id);
                questionHash.put("password", psswrd);
                questionHash.put("token", token);
                questionHash.put("device_id", android_id);
                questionHash.put("os_type", "android");
                questionHash.put("os_version", release);
                questionHash.put("signup_type", "self");
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("user", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);

                Log.e("sent data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(myReq);
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();


    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SignUp", "data from server - " + response);
                if (response != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        // looping through All Contacts

                        if (jsonObj.getString("status").contentEquals("1")) {
                            int id = jsonObj.getInt("id");
                            String username = jsonObj.getString("uname");
                            String email = jsonObj.getString("email");
                            String name = jsonObj.getString("name");
                            String gender = jsonObj.getString("gender");
                            String image = jsonObj.getString("image");
                            SharedPreferences pref3 = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref3.edit();
                            editor.putInt("id", id);
                            editor.putString("uname", username);
                            editor.putString("email", email);
                            editor.putString("name", name);
                            editor.putString("gender", gender);
                            editor.putString("image", image);
                            editor.putString("password", psswrd);
                            editor.putString("accesstoken", " ");
                            editor.commit();
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor3 = pref.edit();
                            editor3.putBoolean("state", true);
                            editor3.commit();
                            SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = pref2.edit();
                            editor2.putBoolean("state", false);
                            editor2.commit();
                            SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                            SharedPreferences.Editor editor4 = pref4.edit();
                            editor4.putBoolean("state", false);
                            editor4.commit();
                            SharedPreferences pref5 = getApplicationContext().getSharedPreferences("logintype", MODE_PRIVATE);
                            SharedPreferences.Editor editor5 = pref5.edit();
                            editor5.putString("type", "self");
                            editor5.commit();
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), Main.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    } catch (final JSONException e) {
                        Log.e("SignUp", "Json parsing error: " + e.getMessage());
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                    }
                } else {
                    Log.e("SignUp", "Couldn't get json from server.");
                    progressDialog.dismiss();
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

    @Override
    protected void onPause() {
        super.onPause();
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
    }

    @Override
    public void onBackPressed() {
        if (emailid.getText().toString().trim().length() != 0 ||
                full_name.getText().toString().trim().length() != 0 ||
                username.getText().toString().trim().length() != 0 ||
                password.getText().toString().trim().length() > 6) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setIcon(R.drawable.ic_dialog_alert);
                    builder.setTitle("Closing Activity");
                    builder.setMessage("Are you sure you want to close this activity?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
        } else {
            super.onBackPressed();
        }
    }

}
