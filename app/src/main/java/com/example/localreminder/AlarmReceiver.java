package com.example.localreminder;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Play Default Ringtone
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        Ringtone ringtone = RingtoneManager.getRingtone(context,alarmUri);
//        ringtone.play();

        // Play the alarm sound
        MediaPlayer music = MediaPlayer.create(context, R.raw.alarm1);
        music.start();

        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("notification_clicked", true);
        mainIntent.putExtra("description", description);
        mainIntent.putExtra("date", date);
        mainIntent.putExtra("time", time);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int notificationId = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, mainIntent, PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm1);
        description = intent.getStringExtra("description");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "androidKnowledge")
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle("NOTIFICATION FOR ALARM")
                .setContentText(description)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        // Check for permission and then show the notification
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // If you haven't already, consider calling ActivityCompat#requestPermissions here
            // to request the missing permissions. Then, handle the case where the user grants the permission.
        } else {
            notificationManagerCompat.notify(notificationId, builder.build());
        }
    }
}
