package www.firstpinch.com.firstpinch2;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.GCM.GCMRegistrationIntentService;

/**
 * Created by Rianaa Admin on 31-08-2016.
 */
public class ChooseSignUp extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    Button signup, btnSignIn;

    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    String fname, femail, fgender, fbday, plink, id, accessToken;
    //for name and email id
    String gname, gemail, guserid, gimage, gtoken, ggender, FB_REGISTER_URL = "http://54.169.84.123/api/sociallogin";
    int FB_LOGGED_IN = 1, RC_SIGN_IN = 2;
    String token, android_id, release, saved_base64image = "", fbprofilePicUrl;
    int sdkVersion;
    ProgressDialog progressDialog;
    Bitmap bitmap = null;
    View parent;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.choosesignup);
        parent = findViewById(R.id.choosesignup);

        btnSignIn = (Button) findViewById(R.id.btn_signin);
        ImageView signlogo = (ImageView)findViewById(R.id.logoc);




        release = Build.VERSION.RELEASE;
        sdkVersion = Build.VERSION.SDK_INT;

        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

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
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
// basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .requestIdToken(getResources().getString(R.string.server_client_id))
                .build();
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        /*signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setColorScheme(0);
        setGooglePlusButtonText(signInButton,"Continue with Google");*/
        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        signInButton.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("public_profile email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    Log.i("Unit Testing", "Facebook button onclick");
                    RequestData();

                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        signup = (Button) findViewById(R.id.choossingup_button_signin);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*launchHomeScreen();*/
                startActivity(new Intent(ChooseSignUp.this, SignUp.class));
                //finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*launchHomeScreen();*/
                startActivity(new Intent(ChooseSignUp.this, SignIn.class));
                //finish();
            }
        });

    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Log.i("Unit Testing", "Google button onclick");
                signIn();
                break;
        }
    }

    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //request data from facebook
    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        JSONObject data = response.getJSONObject();
                        if (data.has("picture")) {
                            //fbprofilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                            // set profile image to imageview using Picasso or Native methods
                            Log.e("saved_base64image url", saved_base64image);
                        }
                        AccessToken fbtoken = AccessToken.getCurrentAccessToken();
                        accessToken = fbtoken.getToken();
                        fname = json.getString("name");
                        femail = json.getString("email");
                        plink = json.getString("link");
                        id = json.getString("id");
                        fbprofilePicUrl = "https://graph.facebook.com/" + id + "/picture?type=large";
                        String gndr = json.getString("gender");
                        if (gndr.contentEquals("male")) {
                            fgender = "1";
                        } else {
                            fgender = "2";
                        }
                        Log.e("gender and id", fgender+"-"+id);
                        //fbday= json.getString("birthday");
                        share();
                        //new GetBitmapFromFBImageURL().execute();
                        registerFbUser();


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Error_GlobalVariables error_global = new Error_GlobalVariables();
                    error_global.initializeVar(ChooseSignUp.this, e.toString());
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture,gender");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void registerFbUser() {

        StringRequest myReq = new StringRequest(Request.Method.POST,
                FB_REGISTER_URL,
                createFBUSERSuccessListener(),
                createFBUSERErrorListener()) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {

                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("uname", id);
                questionHash.put("name", fname);
                questionHash.put("gender", fgender);
                questionHash.put("email", femail);
                questionHash.put("password", accessToken);
                questionHash.put("token", token);
                questionHash.put("device_id", android_id);
                questionHash.put("os_type", "android");
                questionHash.put("os_version", release);
                questionHash.put("signup_type", "other");
                questionHash.put("uid", id);
                questionHash.put("provider", "facebook");
                questionHash.put("image", fbprofilePicUrl);
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("user", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);

                Log.e("data", "" + questionObj.toString());
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

    private Response.Listener<String> createFBUSERSuccessListener() {
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
                            editor.putString("image", fbprofilePicUrl);
                            editor.putString("password", "scl");
                            editor.putString("accesstoken", accessToken);
                            editor.commit();
                            SharedPreferences pref5 = getApplicationContext().getSharedPreferences("logintype", MODE_PRIVATE);
                            SharedPreferences.Editor editor5 = pref5.edit();
                            editor5.putString("type", "social");
                            editor5.commit();
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = pref.edit();
                            editor1.putBoolean("state", true);
                            editor1.commit();
                            SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = pref2.edit();
                            editor2.putBoolean("state", false);
                            editor2.commit();
                            SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                            SharedPreferences.Editor editor4 = pref4.edit();
                            editor4.putBoolean("state", false);
                            editor4.commit();
                            progressDialog.dismiss();
                            Intent i = new Intent(ChooseSignUp.this, Main.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }
                        if (jsonObj.getString("status").contentEquals("2")) {
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
                            editor.putString("image", fbprofilePicUrl);
                            editor.putString("password", "scl");
                            editor.putString("accesstoken", accessToken);
                            editor.commit();
                            SharedPreferences pref5 = getApplicationContext().getSharedPreferences("logintype", MODE_PRIVATE);
                            SharedPreferences.Editor editor5 = pref5.edit();
                            editor5.putString("type", "social");
                            editor5.commit();
                            progressDialog.dismiss();
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = pref.edit();
                            editor1.putBoolean("state", true);
                            editor1.commit();
                            SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = pref2.edit();
                            editor2.putBoolean("state", true);
                            editor2.commit();
                            SharedPreferences pref4 = getApplicationContext().getSharedPreferences("houseselected", MODE_PRIVATE);
                            SharedPreferences.Editor editor4 = pref4.edit();
                            editor4.putBoolean("state", true);
                            editor4.commit();
                            Intent i = new Intent(ChooseSignUp.this, Main.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else if (jsonObj.getString("status").contentEquals("0")) {
                            Log.e("message=", jsonObj.getString("message"));
                            Snackbar snack = Snackbar.make(parent, jsonObj.getString("message"), Snackbar.LENGTH_INDEFINITE);
                            snack.setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                                    .setActionTextColor(getResources().getColor(android.R.color.white
                                    )).show();

                            View view = snack.getView();
                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.WHITE);

                            progressDialog.dismiss();

                        }

                    } catch (final JSONException e) {
                        Log.e("SignUp", "Json parsing error: " + e.getMessage());
                        progressDialog.dismiss();
                        /*Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();*/
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(ChooseSignUp.this, e.toString());
                    }
                } else {
                    Log.e("SignUp", "Couldn't get json from server.");
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG);
                }

            }
        };
    }

    private Response.ErrorListener createFBUSERErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SignUp", "error connect - " + error);
                progressDialog.dismiss();
            }
        };
    }

    private void registerGoogleUser() {

        StringRequest myReq = new StringRequest(Request.Method.POST,
                FB_REGISTER_URL,
                createGoogleSuccessListener(),
                createGoogleErrorListener()) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {

                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("uname", guserid);
                questionHash.put("name", gname);
                questionHash.put("gender", ggender);
                questionHash.put("email", gemail);
                questionHash.put("password", guserid);
                questionHash.put("token", token);
                questionHash.put("device_id", android_id);
                questionHash.put("os_type", "android");
                questionHash.put("os_version", release);
                questionHash.put("signup_type", "other");
                questionHash.put("uid", guserid);
                questionHash.put("provider", "google_oauth2");
                //questionHash.put("image", gimage);
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("user", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);

                Log.e("Gdata", "" + questionObj.toString());
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

    private Response.Listener<String> createGoogleSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("GSignUp response", "data from server - " + response);
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
                            editor.putString("uname", guserid);
                            editor.putString("email", gemail);
                            editor.putString("name", gname);
                            editor.putString("gender", gender);
                            editor.putString("image", image);
                            editor.putString("password", "scl");
                            editor.putString("accesstoken", gtoken);
                            editor.commit();
                            progressDialog.dismiss();
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = pref.edit();
                            editor1.putBoolean("state", true);
                            editor1.commit();
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
                            editor5.putString("type", "social");
                            editor5.commit();
                            Intent i = new Intent(ChooseSignUp.this, Main.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }

                        if (jsonObj.getString("status").contentEquals("2")) {
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
                            editor.putString("password", "scl");
                            editor.putString("accesstoken", gtoken);
                            editor.commit();
                            progressDialog.dismiss();
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = pref.edit();
                            editor1.putBoolean("state", true);
                            editor1.commit();
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
                            editor5.putString("type", "social");
                            editor5.commit();
                            Intent i = new Intent(ChooseSignUp.this, Main.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else if (jsonObj.getString("status").contentEquals("0")) {
                            Log.e("message=", jsonObj.getString("message"));
                            Snackbar snack = Snackbar.make(parent, jsonObj.getString("message"), Snackbar.LENGTH_INDEFINITE);
                            snack.setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                                    .setActionTextColor(getResources().getColor(android.R.color.white
                                    )).show();

                            View view = snack.getView();
                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.WHITE);
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {

                                        }
                                    });
                            FacebookSdk.sdkInitialize(getApplicationContext());
                            LoginManager.getInstance().logOut();

                            progressDialog.dismiss();

                        }

                    } catch (final JSONException e) {
                        Log.e("SignUp", "Json parsing error: " + e.getMessage());
                        progressDialog.dismiss();
                        /*Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();*/
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(ChooseSignUp.this, e.toString());
                    }
                } else {
                    Log.e("SignUp", "Couldn't get json from server.");
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

    private Response.ErrorListener createGoogleErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SignUp", "error connect - " + error);
                progressDialog.dismiss();
            }
        };
    }

    public void share() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPreference", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", fname);
        editor.putString("email", femail);
        editor.putString("link", plink);
        editor.putString("fid", id);
        editor.putString("gender", fgender);
        //editor.putString("bday", fbday);
        editor.putString("token", accessToken);
        editor.putInt("int", 20);
        editor.commit();

        Log.i("check the value f1 :", "" + fname);
        Log.i("check plink : ", " " + plink);
        Log.i("check the value f2 :", "" + femail);
        Log.i("check the value fid :", "" + id);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //iffbsignin
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //If signing+
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
       /* if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
                mFullName = acct.getDisplayName();
                mEmail = acct.getEmail();
            }
        }*/
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("handleSignInResult:", "" + result);
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            //Displaying name and email-save name and email
            //Log.e("Google response", String.valueOf(acct));
            gname = acct.getDisplayName();
            gemail = acct.getEmail();
            //Log.e("google photo",""+acct.getPhotoUrl().toString());
            gtoken = acct.getIdToken();
            Log.e("gtoken",gtoken+" ");
            Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (person.hasGender()) {
                if (person.getGender() == 1) { //female
                    ggender = "2";
                } else if (person.getGender() == 2) {  //other
                    ggender = "3";
                } else if (person.getGender() == 0) {  //male
                    ggender = "1";
                }
            } else {
                ggender = "3";
            }
            //ggender = String.valueOf(person.getGender());
            guserid = person.getId();
            //gimage = "https://plus.google.com/s2/photos/profile/" + guserid + "?sz=100";
            //Log.e("Google response", "Gender: " + person.getGender());
            shared();
            registerGoogleUser();
            //new GetBitmapFromGoogleImageURL().execute(gimage);
            //registerGoogleUser();
            /*Log.i(TAG, "AboutMe: " + person.getAboutMe());
            Log.i(TAG, "Birthday: " + person.getBirthday());
            Log.i(TAG, "Current Location: " + person.getCurrentLocation());
            Log.i(TAG, "Language: " + person.getLanguage());*/
            //asynk class
            //new profile_asynk().execute();
            //new ProfilePic();
            /*SharedPreferences pref = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("state", true);
            editor.commit();
            SharedPreferences pref2 = getApplicationContext().getSharedPreferences("interestselected", MODE_PRIVATE);
            SharedPreferences.Editor editor2 = pref2.edit();
            editor2.putBoolean("state", false);
            editor2.commit();
            shared();*/

            //Intent i = new Intent(ChooseSignUp.this, SelectInterests.class);
            //startActivity(i);
            //finish();
        } else {
            //If login fails
            //Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    //fuction for shared reference
    public void shared() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("kname", gname);
        editor.putString("kemail", gemail);
        editor.putString("kuserid", guserid);
        // editor.putString("kurl",glink);
        editor.putInt("integer", 10);


        editor.commit();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    class GetBitmapFromFBImageURL extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL imageURL = new URL(fbprofilePicUrl);
                bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                saved_base64image = Base64.encodeToString(b, Base64.DEFAULT);
                saved_base64image = "data:image/jpeg;base64," + saved_base64image;

                return saved_base64image;
            } catch (Exception e) {
                this.exception = e;
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(ChooseSignUp.this, e.toString());
                return null;
            }
        }

        protected void onPostExecute(String fbbase64Image) {
            // TODO: check this.exception
            // TODO: do something with the feed
            //registerFbUser(fbbase64Image);
        }
    }

    class GetBitmapFromGoogleImageURL extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL imageURL = new URL(fbprofilePicUrl);
                bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                saved_base64image = Base64.encodeToString(b, Base64.DEFAULT);
                saved_base64image = "data:image/jpeg;base64," + saved_base64image;

                return saved_base64image;
            } catch (Exception e) {
                this.exception = e;
                Error_GlobalVariables error_global = new Error_GlobalVariables();
                error_global.initializeVar(ChooseSignUp.this, e.toString());
                return null;
            }
        }

        protected void onPostExecute(String base64Image) {
            // TODO: check this.exception
            // TODO: do something with the feed
            //registerGoogleUser(base64Image);
        }
    }
}
