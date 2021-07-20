package www.firstpinch.com.firstpinch2;

/**
 * Created by Rianaa Admin on 12-10-2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import org.json.JSONObject;

public class ProfilePic extends Activity {
    String uid, link, flink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new profile_asynk().execute();

    }

    public void put() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        uid = pref.getString("kuserid", "");

        // Log.d("check userid:",""+uid);


    }


    private class profile_asynk extends AsyncTask<String, String, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... strings) {
            put();


            String url = "https://www.googleapis.com/plus/v1/people/" + uid + "?fields=image&key=AIzaSyCT9wOaaLaxzcumis2w5Oh8cH_Hr2wn3C4";
            // Log.d("URL", "" + url);

            JSONObject json = JSONParsing.getJSONfromURL(url);
            // Log.d("JSON", "" + json);
            return json;

        }

        public void sharedp() {
            // editor.putBoolean("key_name1", true);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("kurl", flink);

            editor.commit();

            Log.i("check link : ", " " + flink);


        }

        protected void onPostExecute(JSONObject jsonObj) {
            try {
                JSONObject json2 = jsonObj.getJSONObject("image");
                //Log.i("main",""+json2);
                //int l=json2.length();
                //Log.d("len",""+l);
                link = json2.getString("url");
                Log.d("url::", "" + link);
                String[] separated = link.split("=");
                flink = separated[0] + "=200";


                sharedp();

                Intent i = new Intent(ProfilePic.this, SelectInterests.class);
                startActivity(i);


            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObj);
        }


    }

}