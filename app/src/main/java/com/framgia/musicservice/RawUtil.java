package com.framgia.musicservice;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RawUtil {


    public static ArrayList<Song> getListRawSongs(Context context) {

        ArrayList<Song> songList = new ArrayList<>();

        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            try {
                songList.add(new Song(field.getName(), field.getInt(field)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return songList;
    }
}
