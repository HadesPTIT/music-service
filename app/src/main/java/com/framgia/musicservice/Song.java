package com.framgia.musicservice;

import android.net.Uri;

public class Song {

    private String mName;
    private int mResourceId;
    private Uri mUri;

    public Song(String name, int resourceId, Uri uri) {
        mName = name;
        mResourceId = resourceId;
        mUri = uri;
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

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }


}
