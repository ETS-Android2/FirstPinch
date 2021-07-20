package www.firstpinch.com.firstpinch2.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import www.firstpinch.com.firstpinch2.MainFeed.MainFeedDetailedActivity;
import www.firstpinch.com.firstpinch2.R;

/**
 * Created by Rianaa Admin on 07-11-2016.
 */

//GCM notification reciever service
public class GCMPushReceiverService extends GcmListenerService {

    //Local broadcast to send data and referesh feeds
    LocalBroadcastManager mBroadcaster;

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String body = data.getString("body");
        String title = data.getString("title");
        String message = data.getString("message");
        String id = data.getString("id");
        if (body != null) {
            Intent i = new Intent();
            i.setAction("www.firstpinch.com.firstpinch2.Home");
            i.putExtra("message", message);
            i.putExtra("id", id);
            mBroadcaster.sendBroadcast(i);
        }
        String content_type = data.getString("content_type");
        String content_id = data.getString("content_id");

        //Displaying a notiffication with the message
        Log.e("Notification Data", data.toString());
        sendNotification(message, body, title, content_type, content_id);
    }

    //delay, vibrate, sleep, vibrate, sleep
    long[] ln = {500, 300, 300, 300, 500};

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message, String body, String title, String content_type, String content_id) {
        //Home hm = new Home();
        //hm.adapter.getTabView(1,"1");
        //intent to call detailed activity on notification onClick
        Intent intent = new Intent(this, MainFeedDetailedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (content_type.contentEquals("story")) {
            intent.putExtra("post_ques", 0);
        } else if (content_type.contentEquals("question")) {
            intent.putExtra("post_ques", 1);
        }
        intent.putExtra("question_id", content_id);
        int requestCode = 0;
        int id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.notification_small_icon)
                .setContentText(body)
                .setContentTitle(title)
                .setContentInfo(message)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setVibrate(ln)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, noBuilder.build()); //0 = ID of notification
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBroadcaster = LocalBroadcastManager.getInstance(this);
    }

}
