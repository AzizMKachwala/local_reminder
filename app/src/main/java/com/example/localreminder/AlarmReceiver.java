package com.example.localreminder;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
        player.start();

//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//        ringtone.play();

        Intent nextActivity = new Intent(context, UpcomingFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_IMMUTABLE);
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm);
        String description = intent.getStringExtra("description");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "androidKnowledge")
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle("Reminder")
                .setContentText(description)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(123, builder.build());

    }
}
