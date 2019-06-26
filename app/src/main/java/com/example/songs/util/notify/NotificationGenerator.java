package com.example.songs.util.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.example.songs.R;
import com.example.songs.reciever.MediaButtonIntentReceiver;
import com.example.songs.service.SimpleMusicService;

import static com.example.songs.util.UtilConstants.ACTION_NEXT;
import static com.example.songs.util.UtilConstants.ACTION_PREVIOUS;
import static com.example.songs.util.UtilConstants.ACTION_TOGGLE;

public class NotificationGenerator {

    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_NAME_1 = "CHANNEL_ONE";

    private SimpleMusicService mMusicService;

    private NotificationManager manager;

    public NotificationGenerator(SimpleMusicService mMusicService) {
        this.mMusicService = mMusicService;
    }

    public void buildNotification() {
        if(mMusicService==null) {
            return;
        }
        Log.e("CHECKING", "buildNotification: " + "I got called.");
        createNotification();
    }

    private  void createNotification() {
        manager = (NotificationManager) mMusicService.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        Bitmap artwork = BitmapFactory.decodeResource(mMusicService.getResources(), R.drawable.clown);

//        NotificationCompat.Action previousAction = new NotificationCompat.Action(R.drawable.ic_fast_rewind_white,
//                "Previous",
//                MediaButtonReceiver.buildMediaButtonPendingIntent(mMusicService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
//        NotificationCompat.Action toggleAction = new NotificationCompat.Action(R.drawable.ic_pause_light,
//                "Toggle",
//                MediaButtonReceiver.buildMediaButtonPendingIntent(mMusicService, PlaybackStateCompat.ACTION_PAUSE));
//        NotificationCompat.Action nextAction = new NotificationCompat.Action(R.drawable.ic_fast_forward_white,
//                "Next",
//                MediaButtonReceiver.buildMediaButtonPendingIntent(mMusicService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        NotificationCompat.Action previousAction = new NotificationCompat.Action(R.drawable.ic_fast_rewind_white,
                "Previous",
                retrievePendingIntent(ACTION_PREVIOUS));
        NotificationCompat.Action toggleAction = new NotificationCompat.Action(R.drawable.ic_pause_light,
                "Previous",
                retrievePendingIntent(ACTION_TOGGLE));
        NotificationCompat.Action nextAction = new NotificationCompat.Action(R.drawable.ic_fast_forward_white,
                "Previous",
                retrievePendingIntent(ACTION_NEXT));

        Notification notification = new NotificationCompat.Builder(mMusicService, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_headset_grey)
                .setContentTitle(mMusicService.getSongTitle())
                .setContentText(mMusicService.getArtistName())
                .setLargeIcon(artwork)
                .addAction(previousAction)
                .addAction(toggleAction)
                .addAction(nextAction)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(mMusicService.getMediaSession().getSessionToken()))
                .setVibrate(null)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        manager.notify(1, notification);

    }

    private PendingIntent retrievePendingIntent(final String action) {
        final ComponentName componentName = new ComponentName(mMusicService, SimpleMusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(componentName);

        return PendingIntent.getService(mMusicService, 1, intent, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
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

}
