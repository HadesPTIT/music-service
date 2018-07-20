package com.framgia.musicservice;

public class TimeFormat {

    public static String format(long milis) {
        int hours = (int) ((milis / (1000 * 60 * 60)) % 24);
        int minutes = (int) ((milis / (1000 * 60)) % 60);
        int seconds = (int) (milis / 1000) % 60;
        return String.valueOf(hours + ":" + minutes + ":" + seconds);
    }
}
