package www.firstpinch.com.firstpinch2.Settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import www.firstpinch.com.firstpinch2.ErrorLogs.Error_GlobalVariables;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 07-01-2017.
 */

public class UpdateUsernameSocial extends AppCompatActivity {

    String UPDATE_PASSWORD = "http://54.169.84.123/api/updateSocialinfo", USERNAME_CHECK_URL = "http://54.169.84.123/api/checkuname",
            username, GET_PROFILE_DETAILS_URL = "http://54.169.84.123/api/users/profile/1",accessToken="";
    EditText et_username;
    int user_id;
    ImageView image_close, im_update_check;
    Button bt_update_username;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_username_social);
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        get_profile_detail(user_id);
        username = sp.getString("uname", "");
        et_username = (EditText) findViewById(R.id.edit_username);
        image_close = (ImageView) findViewById(R.id.post_cross_img);
        im_update_check = (ImageView) findViewById(R.id.edit_update_img);
        bt_update_username = (Button) findViewById(R.id.button_update_username);


        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");

        et_username.setTypeface(custom_font1);


        image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        im_update_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_pref()) {
                    check_username();
                }
                /*saved_name = et_profileName.getText().toString();
                saved_bio = et_profileBio.getText().toString();
                edit_profile_post();*/
                //finish();
            }
        });

        bt_update_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_pref()) {
                    check_username();
                }
            }
        });

    }

    public Boolean check_pref() {

        if (et_username.getText().toString().trim().length() == 0) {
            et_username.setError("*Required Field");
            return false;
        }
        if (et_username.getText().toString().contains(" ")) {
            et_username.setError("*Blank Spaces Not Allowed");
            return false;
        }

        return true;
    }

    private void updateUsername() {

        final String str_new_username = et_username.getText().toString().trim();
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        accessToken = sp.getString("accesstoken", "");
        StringRequest myReq = new StringRequest(Request.Method.PUT,
                UPDATE_PASSWORD,
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
                questionHash.put("user_id", String.valueOf(user_id));
                questionHash.put("uname", str_new_username);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.show();


    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            SharedPreferences pref3 = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref3.edit();

            @Override
            public void onResponse(String response) {
                Log.e("SignUp", "data from server - " + response);
                if (response != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        // looping through All Contacts

                        if (jsonObj.getString("status").contentEquals("1")) {
                            Toast.makeText(getApplicationContext(), "" + jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                            editor.putString("uname", et_username.getText().toString().trim());
                            editor.commit();
                            onBackPressed();
                        } else if (jsonObj.getString("status").contentEquals("4")) {
                            //Toast.makeText(getApplicationContext(), "" + jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();

                    } catch (final JSONException e) {
                        Log.e("SignUp", "Json parsing error: " + e.getMessage());
                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();
                                progressDialog.dismiss();
                            }
                        });*/
                        Error_GlobalVariables error_global = new Error_GlobalVariables();
                        error_global.initializeVar(UpdateUsernameSocial.this, e.toString());

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

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SignUp", "error connect - " + error);
                progressDialog.dismiss();
            }
        };
    }

    private void check_username() {
        final String str_new_username = et_username.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                USERNAME_CHECK_URL + "?uname=" + str_new_username,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(SignUp.this, response, Toast.LENGTH_LONG).show();
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(response);
                            if (jsonObj.getBoolean("status")) {
                                et_username.setError("*Username already Exists!");
                            } else {
                                updateUsername();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void get_profile_detail(final int user_id) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_PROFILE_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            JSONObject profile = jsonObj.getJSONObject("profile");
                            //name = profile.getString("name");
                            username = profile.getString("uname");
                            et_username.setText(username);
                            /*image_url= profile.getString("image");
                            profile_desc = profile.getString("bio");
                            tv_profileName.setText(name);
                            tv_username.setText("@" + username);
                            tv_profileDesc.setText(profile_desc);*/
                            /*Picasso.with(getApplicationContext())
                                    .load(image_url)
                                    .transform(new RoundedCornersTransform())
                                    .into(im_profile_image);*/

                        } catch (JSONException e) {
                            Log.e("LOG", "" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "check" + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-Id", String.valueOf(user_id));
                return headers;
            }

        };
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}