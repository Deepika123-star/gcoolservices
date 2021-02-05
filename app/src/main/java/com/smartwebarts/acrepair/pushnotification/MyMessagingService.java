package com.smartwebarts.acrepair.pushnotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.SplashActivity;
import com.smartwebarts.acrepair.productlist.ProductDetailActivity;

import static com.google.firebase.iid.FirebaseInstanceId.getInstance;

public class MyMessagingService extends FirebaseMessagingService {

    private String messageBody;
    private String msg;
    private String img;
    private String title;
//    private String key;
//    private String uid;


    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = getString(R.string.default_notification_channel_id);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setAutoCancel(false)
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        String refreshedToken = getInstance().getToken();
        Log.e("onNewToken", "Refreshed token: " + refreshedToken);
        new AccessToken().setAccess_token(getApplicationContext(), refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String str_from = "" + remoteMessage.getFrom();
        Log.e("From : ", str_from);

        title = "" + remoteMessage.getData().get("title")!=null?remoteMessage.getData().get("title"):remoteMessage.getNotification().getTitle();
        Log.e("Title", ""+title);

        messageBody = "" + remoteMessage.getData().get("body")!=null?remoteMessage.getData().get("body"):remoteMessage.getNotification().getBody();
        Log.e("Body", ""+messageBody);

        msg = "" + remoteMessage.getData().get("msg");
        Log.e("Msg",""+ msg);

        img = "" + remoteMessage.getData().get("img");
        Log.e("Img", ""+img);

        sendNotification();

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     */

    private void sendNotification() {
        Intent intent;
        if (msg == null || msg.isEmpty() || msg.equalsIgnoreCase("null")){
            intent = new Intent(this, SplashActivity.class);
        } else {
            intent = new Intent(this, ProductDetailActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ProductDetailActivity.ID, msg);
        intent.putExtra("messageBody", messageBody);
        intent.putExtra("msg", msg);
        intent.putExtra("img", img);
//        intent.putExtra("uid", uid);
//        intent.putExtra("key", key);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int numMessages = 0;

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logon))
                        .setContentTitle(title)
                        .setNumber(++numMessages)
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setContentText(messageBody)
                        .setAutoCancel(true)
//                        .addAction(0, "Accept", pendingIntent)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (notificationManager!=null){

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 , notificationBuilder.build());

            registerReceiver(broadcastReceiver,
                    new IntentFilter("com.from.notification"));

            Intent intentNotification = new Intent();
            intentNotification.setAction("com.from.notification");
            intentNotification.putExtra(ProductDetailActivity.ID, msg);
            intentNotification.putExtra("messageBody", messageBody);
            intentNotification.putExtra("msg", msg);
            intentNotification.putExtra("img", img);
//            intentNotification.putExtra("uid", uid);
//            intentNotification.putExtra("key", key);
            sendBroadcast(intentNotification);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // do your stuff related to start activity
            Toast.makeText(context, context.getString(R.string.app_name) + " has received a new notification", Toast.LENGTH_SHORT).show();

            Intent intent1;
            if (msg == null || msg.isEmpty() || msg.equalsIgnoreCase("null")){
                intent1 = new Intent(getApplicationContext(), SplashActivity.class);
            } else {
                intent1 = new Intent(getApplicationContext(), ProductDetailActivity.class);
            }
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra(ProductDetailActivity.ID, msg);
            intent1.putExtra("messageBody", messageBody);
            intent1.putExtra("msg", msg);
            intent1.putExtra("img", img);
//            intent1.putExtra("uid", uid);
//            intent1.putExtra("key", key);
            getApplicationContext().startActivity(intent1);

        }
    };
}
