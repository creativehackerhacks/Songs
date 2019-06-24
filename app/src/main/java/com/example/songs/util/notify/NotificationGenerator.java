package com.example.songs.util.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import com.example.songs.R;
import com.example.songs.service.SimpleMusicService;

public class NotificationGenerator {

    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_NAME_1 = "CHANNEL_ONE";

    private static NotificationManager manager;

    public static void buildNotification(SimpleMusicService service, String what) {
        if(service==null) {
            return;
        }
        Log.e("CHECKING", "buildNotification: " + "I got called.");
        createNotification(service);
    }

    private static void createNotification(SimpleMusicService mService) {
        manager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(mService);
        }

        Bitmap artwork = BitmapFactory.decodeResource(mService.getResources(), R.drawable.clown);

        Notification notification = new NotificationCompat.Builder(mService, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_headset_grey)
                .setContentTitle(mService.getSongTitle())
                .setContentText(mService.getArtistName())
                .setLargeIcon(artwork)
                .addAction(R.drawable.ic_fast_rewind_white, "Previous", null)
                .addAction(R.drawable.ic_pause_light, "Pause", null)
                .addAction(R.drawable.ic_fast_forward_white, "Next", null)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(new MediaSessionCompat(mService, "First").getSessionToken()))
//                .setSubText("Sub Text")
//                .setOngoing(true)
                .setVibrate(null)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        manager.notify(1, notification);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createChannel(SimpleMusicService service) {
//        manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel_1 = new NotificationChannel(
                CHANNEL_ID_1,
                CHANNEL_NAME_1,
                NotificationManager.IMPORTANCE_LOW
        );

        notificationChannel_1.setDescription("Media Playback Controls");
        notificationChannel_1.setShowBadge(false);
        notificationChannel_1.enableVibration(false);
        notificationChannel_1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        manager.createNotificationChannel(notificationChannel_1);
    }


    public static void buildMyNotify(SimpleMusicService service, String what) {
        if(service == null) {
            return;
        }

    }

}
