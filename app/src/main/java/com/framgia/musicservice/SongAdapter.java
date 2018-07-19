package com.framgia.musicservice;

import android.content.Context;
import android.net.Uri;
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
    private ArrayList<Song> mLists;

    public SongAdapter(Context context, ArrayList<Song> lists) {
        mContext = context;
        mLists = lists;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_song,parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = mLists.get(position);
        holder.mName.setText(song.getName());
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        TextView mName;

        SongViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_name);
        }
    }
}

