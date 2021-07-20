package www.firstpinch.com.firstpinch2.Settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * Created by Rianaa Admin on 09-11-2016.
 */

public class UpdatePassword extends AppCompatActivity {

    String UPDATE_PASSWORD = "http://54.169.84.123/api/userUpdate";
    EditText et_newPass, et_currPass, et_confirmPass;
    int user_id;
    ImageView image_close, im_update_check;
    Button bt_update_pass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_password);
        SharedPreferences sp = getSharedPreferences("UserDetails",
                Activity.MODE_PRIVATE);
        user_id = sp.getInt("id", 0);
        et_currPass = (EditText) findViewById(R.id.edit_currpass);
        et_newPass = (EditText) findViewById(R.id.edit_newpass);
        et_confirmPass = (EditText) findViewById(R.id.edit_confirmpass);
        image_close = (ImageView) findViewById(R.id.post_cross_img);
        im_update_check = (ImageView) findViewById(R.id.edit_update_img);
        bt_update_pass = (Button) findViewById(R.id.button_update_password);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");

        et_currPass.setTypeface(custom_font1);
        et_newPass.setTypeface(custom_font1);
        et_confirmPass.setTypeface(custom_font1);
        bt_update_pass.setTypeface(custom_font1);

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
                    updatePassword();
                }
                /*saved_name = et_profileName.getText().toString();
                saved_bio = et_profileBio.getText().toString();
                edit_profile_post();*/
                //finish();
            }
        });

        bt_update_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_pref()) {
                    updatePassword();
                }
            }
        });

    }

    public Boolean check_pref() {

        if (et_currPass.getText().toString().trim().length() == 0) {
            et_currPass.setError("*Required Field");
            return false;
        } else if (et_currPass.getText().toString().trim().length() < 6) {
            et_currPass.setError("*Wrong password ");
            return false;
        } else if (et_newPass.getText().toString().trim().length() == 0) {
            et_newPass.setError("*Required Field");
            return false;
        } else if (et_newPass.getText().toString().trim().length() < 6) {
            et_newPass.setError("*Wrong password");
            return false;
        } else if (et_confirmPass.getText().toString().trim().length() == 0) {
            et_confirmPass.setError("*Required Field");
            return false;
        } else if (et_confirmPass.getText().toString().trim().length() < 6) {
            et_confirmPass.setError("*Wrong password");
            return false;
        } else if (!et_newPass.getText().toString().contentEquals(et_confirmPass.getText().toString())) {
            et_confirmPass.setError("*Passwords Don't Match");
            return false;
        }
        return true;
    }

    private void updatePassword() {

        final String str_currPass = et_currPass.getText().toString().trim();
        final String str_newPass = et_newPass.getText().toString().trim();

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
                questionHash.put("current_password", str_currPass);
                questionHash.put("password", str_newPass);
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
            @Override
            public void onResponse(String response) {
                SharedPreferences pref3 = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref3.edit();
                Log.e("SignUp", "data from server - " + response);
                if (response != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        // looping through All Contacts

                        if (jsonObj.getString("status").contentEquals("1")) {
                            Toast.makeText(getApplicationContext(), "" + jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                            editor.putString("password", et_newPass.getText().toString().trim());
                            editor.commit();
                            onBackPressed();
                            progressDialog.dismiss();
                        } else if (jsonObj.getString("status").contentEquals("4")) {
                            et_currPass.setError("*Wrong password");
                            //Toast.makeText(getApplicationContext(), "" + jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }else if (jsonObj.getString("status").contentEquals("5")) {
                            //et_currPass.setError("*");
                            Toast.makeText(getApplicationContext(), "" + jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();
                        }

                    } catch (final JSONException e) {
                        Log.e("SignUp", "Json parsing error: " + e.getMessage());
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
                        error_global.initializeVar(UpdatePassword.this, e.toString());

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
