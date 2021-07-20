package www.firstpinch.com.firstpinch2.ErrorLogs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Rianaa Admin on 12-01-2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String str = intent.getAction();
        //Log.e("intent.getAction()","-"+str);
        // For our recurring task, we'll just display a message
        //Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int mMin = calendar.get(Calendar.MINUTE);
        int mHour = calendar.get(Calendar.HOUR);
        Log.e("time ", mHour + ":" + mMin + "");
        if (mHour == 11 && mMin == 45) {
            Log.e("time inside if", mHour + ":" + mMin + "");
            Intent service = new Intent(context, ErrorLogService.class);
            context.startService(service);
        }
        Log.e("AlarmReceiver", "reciever called");
    }
}