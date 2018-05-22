package ru.droidwelt.prototype4;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFcmListenerService extends FirebaseMessagingService {


    public MyFcmListenerService() {
    }


    @Override
    public void onMessageReceived(RemoteMessage message) {
     //   String from = message.getFrom();
      //  Log.d("EEEEEE", "message.getFrom(): " + message.getFrom());
        Map data = message.getData();
        if (message.getData().size() > 0) {
         //   Log.d("EEEEEE", "message.getData(): " + message.getData().toString());
            String title = "";
            if (data.get("TITLE") != null)
             title = data.get("TITLE").toString();
            String text = "";
            if (data.get("TEXT") != null)
                text = data.get("TEXT").toString();
            generateNotification(Appl.getContext(), title, text);

            try {
                Appl.loadMsaAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (message.getNotification() != null) {
          //  Log.d("EEEEEE", "message.getNotification(): " + message.getNotification().toString());
          //  Log.d("EEE", "message.getNotification().getTitle(): " + message.getNotification().getTitle());
            String title = "";
            if (data.get("TITLE") != null)
                title = message.getNotification().getTitle();
            String text = "";
            if (data.get("TEXT") != null)
                text = message.getNotification().getBody();
            generateNotification(Appl.getContext(), title, text);

            try {
                Appl.loadMsaAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static void generateNotification(Context context, String MSA_TITLE, String MSA_TEXT) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  // TYPE_NOTIFICATION

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(MSA_TEXT).setBigContentTitle(MSA_TITLE))
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setLights(Color.BLUE, 500, 500)
                .setContentTitle(MSA_TITLE)
                .setContentText(MSA_TEXT)
                .setAutoCancel(true)
                .setTicker(MSA_TITLE /*context.getString(R.string.s_message_sendind)*/)
                .setSound(alarmSound)
                .setWhen(System.currentTimeMillis())
                .setVibrate(new long[]{100, 250});

     /*   final NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(nBuilder);
        style.bigText(MSA_TEXT).setBigContentTitle(MSA_TITLE);
        nBuilder.setStyle(style);*/

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0 /*PendingIntent.FLAG_ONE_SHOT*/);
        nBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
   //     NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, nBuilder.build());
    }

}
