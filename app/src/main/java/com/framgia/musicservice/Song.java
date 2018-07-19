package com.framgia.musicservice;

public class Song {

    private String mName;
    private int mResourceId;

    public Song(String name, int resourceId) {
        mName = name;
        mResourceId = resourceId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getResourceId() {
        return mResourceId;
    }

    public void setResourceId(int resourceId) {
        mResourceId = resourceId;
    }

    @Override
    public String toString() {
        return "Song{" +
                "mName='" + mName + '\'' +
                ", mResourceId='" + mResourceId + '\'' +
                '}';
    }
}
