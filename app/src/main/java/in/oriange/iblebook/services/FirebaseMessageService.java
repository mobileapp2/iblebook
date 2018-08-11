package in.oriange.iblebook.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import in.oriange.iblebook.R;
import in.oriange.iblebook.activities.MainDrawer_Activity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessageService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
        }
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }


//        String title = remoteMessage.getNotification().getTitle();
//        String body = remoteMessage.getNotification().getBody();
//        Log.i("MESSSSSSSSSAGE",""+title+" "+body);
//        int notificationId = new Random().nextInt(60000);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.icon_notification)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(notificationId , notificationBuilder.build());

    }

    private void sendNotification(String messageBody, String messageTitle) {
        Intent intent = new Intent(this, MainDrawer_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setSmallIcon(R.drawable.icon_address_email);
        else
            notificationBuilder.setSmallIcon(R.drawable.icon_address_email);

        notificationBuilder.setContentTitle("Address Book");
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setContentTitle(messageTitle);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setVibrate(new long[]{100, 250});
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


}