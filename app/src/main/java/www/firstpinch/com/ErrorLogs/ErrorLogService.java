package www.firstpinch.com.firstpinch2.ErrorLogs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.firstpinch.com.firstpinch2.SQLite.Log_DatabaseHandler;
import www.firstpinch.com.firstpinch2.SQLite.Log_Error_Object;

/**
 * Created by Rianaa Admin on 13-01-2017.
 */

public class ErrorLogService extends Service {

    String PUT_ERROR_LOGS_URL = "http://54.169.84.123/api/errorLog",errors="";
    Log_DatabaseHandler db;
    //List<Log_Error_Object> errors = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        db = new Log_DatabaseHandler(ErrorLogService.this);
        db.onOpen(db.getWritableDatabase());
        errors = db.getAllErrorData();
        Log.e("error data from service","="+errors);
        /*for(int i=0;i<errors.size();i++){
            Log_Error_Object current = errors.get(i);
            Log.e("error data",current.getLog_error_id()+", "+current.getDevice_id()+", "+current.getDevice_width()+
                    ", "+current.getDevice_height()+", "+current.getLog_error());
        }*/

        sendErrorsToServer(errors);

        stopSelf();
        return Service.START_NOT_STICKY;
    }

    private void sendErrorsToServer(final String errors) {
        final RequestQueue requestQueue = Volley.newRequestQueue(ErrorLogService.this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, PUT_ERROR_LOGS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("error logs response", "" + response);
                        JSONObject jsonObj = null;
                        try {

                            jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.contentEquals("1")) {
                                db.removeAllError();
                                stopSelf();
                            } else {
                                //Toast.makeText(c, "Image Not Uploaded - Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Error_GlobalVariables error_global = new Error_GlobalVariables();
                            error_global.initializeVar(ErrorLogService.this, e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("check", "" + error);
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                HashMap<String, String> questionHash = new HashMap<String, String>();
                questionHash.put("errors", errors);
                HashMap<String, String> mainquestionHash = new HashMap<String, String>();
                mainquestionHash.put("error", String.valueOf(new JSONObject(questionHash)));
                JSONObject questionObj = new JSONObject(mainquestionHash);
                Log.e("error log data", "" + questionObj.toString());
                return questionObj.toString().getBytes();
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}