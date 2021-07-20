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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.GCM.GCMRegistrationIntentService;
import www.firstpinch.com.firstpinch2.MainFeed.RoundedCornersTransform;

/**
 * Created by Rianaa Admin on 25-08-2016.
 */
public class SignIn extends AppCompatActivity {

    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String SIGN_IN_URL = "http://54.169.84.123/api/login";
    TextView forgotpass;
    EditText et_emailid,et_password;
    Button bt_signin;
    ProgressDialog progressDialog;
    String token, android_id,release,psswrd;
    int sdkVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

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
                    Log.e("Registration token : " , token);

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
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

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Cabin-Regular.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Lato-Regular.ttf");

        et_emailid = (EditText) findViewById(R.id.edittext_emailid);
        et_password = (EditText)findViewById(R.id.edittext_password);
        bt_signin = (Button) findViewById(R.id.button_signin);
        ImageView signinlogo = (ImageView)findViewById(R.id.signinlogo);

        et_emailid.setTypeface(custom_font1);
        et_password.setTypeface(custom_font1);
        forgotpass = (TextView) findViewById(R.id.text_forgotpassword);
        SpannableString content = new SpannableString(forgotpass.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotpass.setText(content);
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, ForgotPassword.class));
            }
        });
        bt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Unit Testing","Sign In button onclick");
                if(check_pref()) {
                    sing_in();
                }
            }
        });

    }

    public Boolean check_pref() {

        if (et_emailid.getText().toString().trim().length() == 0) {
            et_emailid.setError("*Required Field");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_emailid.getText().toString().trim()).matches()) {
            et_emailid.setError("*Enter Valid Email");
            return false;
        }
        if (et_password.getText().toString().trim().length() ==0) {
            et_password.setError("*Required Field");
            return false;
        }
        if (et_password.getText().toString().trim().length() < 6) {
            et_password.setError("*Password is invalid");
            return false;
        }
        return true;
    }

    private void sing_in() {

        final String email_id = et_emailid.getText().toString();
        psswrd = et_password.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, SIGN_IN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Sing In response",response);
                            JSONObject jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if(status.contentEquals("0")){
                                et_password.setError("*Password is invalid");
                                progressDialog.dismiss();
                            }
                            if(status.contentEquals("1")){
                                int id = jsonObj.getInt("user_id");
                                String username = jsonObj.getString("user_uname");
                                String name = jsonObj.getString("user_name");
                                String gender = jsonObj.getString("gender");
                                String user_image = jsonObj.getString("image");
                                SharedPreferences pref3 = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref3.edit();
                                editor.putInt("id", id);
                                editor.putString("uname", username);
                                editor.putString("email", email_id);
                                editor.putString("name", name);
                                editor.putString("gender", gender);
                                editor.putString("image",user_image);
                                editor.putString("password", psswrd);
                                editor.putString("accesstoken", " ");
                                editor.commit();
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                                SharedPreferences.Editor editor3 = pref.edit();
                                editor3.putBoolean("state", true);
                                editor3.commit();
                                SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = pref2.edit();
                                editor2.putBoolean("state", true);
                                editor2.commit();
                                SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                                SharedPreferences.Editor editor4 = pref4.edit();
                                editor4.putBoolean("state", true);
                                editor4.commit();
                                SharedPreferences pref5 = getApplicationContext().getSharedPreferences("logintype", MODE_PRIVATE);
                                SharedPreferences.Editor editor5 = pref5.edit();
                                editor5.putString("type", "self");
                                editor5.commit();
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), Main.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else if(status.contentEquals("2")){
                                String message = jsonObj.getString("message");
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Login Failed : "+message,Toast.LENGTH_SHORT).show();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            //Toast.makeText(getApplicationContext(),"Login Failed : "+e.toString(),Toast.LENGTH_SHORT).show();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(SignIn.this, e.toString());
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Sorry, Login Failed. Check your Internet Connection and Try Again",Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Email-Id", email_id);
                headers.put("User-Password", psswrd);
                headers.put("Token", token);
                headers.put("Device-Id", android_id);
                headers.put("Os-Type", "android");
                headers.put("Os-Version", release);
                return headers;
            }

        };
        requestQueue.add(stringRequest);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

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
        if (et_emailid.getText().toString().trim().length() != 0||
                et_password.getText().toString().trim().length() > 6) {
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
                    builder.show();
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
