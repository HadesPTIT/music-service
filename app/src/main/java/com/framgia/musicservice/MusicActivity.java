package com.framgia.musicservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {

    private static final String TAG = MusicActivity.class.getSimpleName();
    private ImageView mImageState, mImagePrevious, mImageNext;
    private TextView mTvStart, mTvEnd;
    private SeekBar mSeekBarTime;
    private MusicService mService;
    private RecyclerView mSongRecyclerView;
    private SongAdapter mAdapter;
    private ArrayList<Song> mListSongs;
    private RawUtil mRawUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initView();

        mListSongs = RawUtil.getListRawSongs(this);
        mAdapter = new SongAdapter(this,mListSongs);
        mSongRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mSongRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSongRecyclerView.setAdapter(mAdapter);

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
