package com.framgia.musicservice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Song> mSongs;
    private OnSongClickListener mListener;

    public SongAdapter(Context context, ArrayList<Song> songs) {
        mContext = context;
        mSongs = songs;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, final int position) {
        Song song = mSongs.get(position);
        holder.mName.setText(song.getName());
        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSongClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongs == null ? 0 : mSongs.size();
    }

    public void setListener(OnSongClickListener listener) {
        mListener = listener;
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView mName;

        public SongViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_name);
        }
    }
}

