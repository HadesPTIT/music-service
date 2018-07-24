package com.framgia.musicservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, MusicService.Callbacks {

    private static final String TAG = MusicActivity.class.getSimpleName();

    private ImageView mImageState;
    private ImageView mImagePrevious;
    private ImageView mImageNext;
    private TextView mTvStart;
    private TextView mTvEnd;
    private SeekBar mSeekBarTime;

    private RecyclerView mSongRecyclerView;
    private SongAdapter mSongAdapter;
    private ArrayList<Song> mSongs;

    private Intent mPlayerIntent;
    private ServiceConnection mConnection;
    private MusicService mService;
    private long mDuration = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initView();

        mSongs = RawUtil.getRawSongs(this);
        mSongAdapter = new SongAdapter(this, mSongs);
        mSongRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mSongRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSongRecyclerView.setAdapter(mSongAdapter);


        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.ServiceBinder binder = (MusicService.ServiceBinder) service;
                mService = binder.getService();
                mService.setListSongs(mSongs);
                mService.setCallback(MusicActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        if (mPlayerIntent == null) {
            mPlayerIntent = new Intent(MusicActivity.this, MusicService.class);
            bindService(mPlayerIntent, mConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayerIntent);
        }

        mImageState.setOnClickListener(this);
        mImageNext.setOnClickListener(this);
        mImagePrevious.setOnClickListener(this);
        mSongAdapter.setListener(new OnSongClickListener() {
            @Override
            public void onSongClick(int position) {
                mImageState.setImageResource(android.R.drawable.ic_media_pause);
                mService.setSelectedSong(position);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        mService.stopUpdate();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_state:
                handlePlayPause();

                break;
            case R.id.image_previous:
                handlePrevious();

                break;
            case R.id.image_next:
                handleNext();

                break;
        }
    }

    @Override
    public void updateClientCurrent(long data) {
        mTvStart.setText(TimeFormat.format(data));
        mSeekBarTime.setProgress((int) (data * 100 / mDuration));
    }

    @Override
    public void updateClientDuration(long duration) {
        mDuration = duration;
        mTvEnd.setText(TimeFormat.format(duration));
    }


    private void handleNext() {
        mService.nextSong();
        mImageState.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void handlePrevious() {
        mService.previousSong();
        mImageState.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void handlePlayPause() {
        mService.playPauseSong();
        if (mService.isPlayingSong()) {
            mImageState.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            mImageState.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void initView() {
        mImageState = findViewById(R.id.image_state);
        mImageNext = findViewById(R.id.image_next);
        mImagePrevious = findViewById(R.id.image_previous);
        mTvStart = findViewById(R.id.text_start);
        mTvEnd = findViewById(R.id.text_end);
        mSeekBarTime = findViewById(R.id.seekbar_time);
        mSongRecyclerView = findViewById(R.id.recycler_view_song);
    }


}
