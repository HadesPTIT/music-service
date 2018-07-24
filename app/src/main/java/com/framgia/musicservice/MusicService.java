package com.framgia.musicservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = MusicService.class.getSimpleName();
    public static final long UPDATE_INTERVAL = 1000;
    private static final String ACTION_STOP = "com.framgia.musicservice.STOP";
    private static final String ACTION_NEXT = "com.framgia.musicservice.NEXT";
    private static final String ACTION_PREVIOUS = "com.framgia.musicservice.PREVIOUS";
    private static final String ACTION_PAUSE = "com.framgia.musicservice.PAUSE";

    private static final int STATE_PAUSED = 1;
    private static final int STATE_PLAYING = -1;

    private static final int REQUEST_CODE_PAUSE = 1111;
    private static final int REQUEST_CODE_PREVIOUS = 1112;
    private static final int REQUEST_CODE_NEXT = 1113;
    private static final int REQUEST_CODE_STOP = 1114;

    private static int NOTIFICATION_ID = 123;

    private final IBinder mIBinder = new ServiceBinder();
    private MediaPlayer mMediaPlayer;
    private Uri mSongUri;
    private ArrayList<Song> mListSongs;
    private int mPosition = 0;
    private int mState = 0;
    private Callbacks mClient;

    private Handler mHandler = new Handler();
    private Runnable mTimer;
    private long mCurrentMillis = 0;

    private Notification.Builder mBuilder;
    private Notification mNotification;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mBuilder = new Notification.Builder(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_PAUSE:
                        playPauseSong();
                    case ACTION_NEXT:
                        nextSong();
                    case ACTION_PREVIOUS:
                        previousSong();
                    case ACTION_STOP:
                        stopSong();
                        stopSelf();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mTimer);
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mMediaPlayer.reset();
        try {
            if (mPosition != mListSongs.size() - 1) {
                mPosition++;
            } else
                mPosition = 0;
            mMediaPlayer.setDataSource(getApplicationContext(), mListSongs.get(mPosition).getUri());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        final int duration = mMediaPlayer.getDuration();
        mClient.updateClientDuration(duration);
        mTimer = new Runnable() {
            @Override
            public void run() {
                mCurrentMillis = mMediaPlayer.getCurrentPosition();
                mClient.updateClientCurrent(mCurrentMillis);
                mHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        mTimer.run();
    }

    public void startSong(Uri songUri, String songName) {

        mMediaPlayer.reset();
        mState = STATE_PLAYING;
        mSongUri = songUri;
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), mSongUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        updateNotification(songName);
    }

    private void updateNotification(String songname) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification.contentView.setTextViewText(R.id.text_notify_name, songname);
        notificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    public void previousSong() {
        if (mPosition == 0) {
            mPosition = mListSongs.size() - 1;
        } else {
            mPosition -= 1;
        }
        startSong(mListSongs.get(mPosition).getUri(), mListSongs.get(mPosition).getName());
    }

    public void stopSong() {
        mMediaPlayer.stop();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void nextSong() {
        if (mPosition == mListSongs.size() - 1) {
            mPosition = 0;
        } else {
            mPosition += 1;
        }
        startSong(mListSongs.get(mPosition).getUri(), mListSongs.get(mPosition).getName());
    }

    public void playPauseSong() {
        if (mState == STATE_PAUSED) {
            mState = STATE_PLAYING;
            mMediaPlayer.start();
        } else {
            mState = STATE_PAUSED;
            mMediaPlayer.pause();
        }
    }

    public boolean isPlayingSong() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void seekTo(int progress) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(progress);
        }
    }

    public void setSelectedSong(int position) {
        mPosition = position;
        mSongUri = mListSongs.get(position).getUri();
        showNotification();
        startSong(mListSongs.get(position).getUri(), mListSongs.get(mPosition).getName());
    }

    private void showNotification() {
        PendingIntent pendingIntent;
        Intent intent;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.item_notification);

        notificationView.setTextViewText(R.id.text_notify_name, mListSongs.get(mPosition).getName());

        intent = new Intent(ACTION_STOP);
        pendingIntent = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_STOP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.image_notify_stop, pendingIntent);

        intent = new Intent(ACTION_PAUSE);
        pendingIntent = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_PAUSE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.image_notify_pause, pendingIntent);

        intent = new Intent(ACTION_PREVIOUS);
        pendingIntent = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_PREVIOUS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.image_notify_previous, pendingIntent);

        intent = new Intent(ACTION_NEXT);
        pendingIntent = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_NEXT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.image_notify_next, pendingIntent);

        mNotification = mBuilder
                .setSmallIcon(android.R.drawable.ic_input_add).setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContent(notificationView)
                .setDefaults(Notification.FLAG_NO_CLEAR)
                .build();
        notificationManager.notify(NOTIFICATION_ID, mNotification);
        startForeground(NOTIFICATION_ID, mNotification);
    }

    public void setListSongs(ArrayList<Song> listSongs) {
        mListSongs = listSongs;
    }

    public void setCallback(Callbacks client) {
        mClient = client;
    }

    public void stopUpdate() {
        mHandler.removeCallbacks(mTimer);
    }


    public interface Callbacks {
        void updateClientCurrent(long data);

        void updateClientDuration(long duration);
    }

    public class ServiceBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
    }
}
