package www.firstpinch.com.firstpinch2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

/**
 * Created by Rianaa Admin on 31-08-2016.
 */
public class ForgotPassword extends AppCompatActivity {

    EditText et_email;
    String USERNAME_CHECK_EMAILID="http://54.169.84.123/api/checkemail",
            PUT_SEND_EMAIL="http://54.169.84.123/api/resetPassword";
    Button bt_forgot_pass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);
        et_email = (EditText) findViewById(R.id.forgotpass_edittext_emailid);
        bt_forgot_pass = (Button) findViewById(R.id.button_forgot);
        ImageView logo = (ImageView) findViewById(R.id.forgotlogo);
        bt_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_pref()){
                    check_emailid();
                }
            }
        });

    }

    public Boolean check_pref() {

        if (et_email.getText().toString().trim().length() == 0) {
            et_email.setError("*Required Field");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()) {
            et_email.setError("*Enter Valid Email");
            return false;
        }

        return true;
    }

    private void check_emailid() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                USERNAME_CHECK_EMAILID + "?email=" + et_email.getText().toString().trim(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("email check : ", ""+response);
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(response);
                            if (jsonObj.getString("status").contentEquals("0")) {
                                et_email.setError("*We couldn't find any account for this Email-Id");
                            }else{
                                sendMail();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(ForgotPassword.this, e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgotPassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void sendMail() {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_SEND_EMAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("put ques bkmrk response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {
                                Toast.makeText(getApplicationContext(), "Email Sent on your Mail", Toast.LENGTH_SHORT).show();
                                et_email.setText("");
                            } else {
                                Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(ForgotPassword.this, e.toString());
                        }

                        // looping through All Contacts
                        progressDialog.dismiss();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "" + error);
                        progressDialog.dismiss();
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Email-Id",et_email.getText().toString().trim());
                return headers;
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending mail...");
        progressDialog.show();


    }

    @Override
    public void onBackPressed() {
        if (et_email.getText().toString().trim().length() != 0) {
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
