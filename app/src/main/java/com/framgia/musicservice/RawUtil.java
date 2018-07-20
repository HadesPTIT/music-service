package com.framgia.musicservice;

import android.content.Context;
import android.net.Uri;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RawUtil {


    public static ArrayList<Song> getRawSongs(Context context) {

        ArrayList<Song> songList = new ArrayList<>();
        String prefix = context.getResources().getString(R.string.prefix_resource);
        String symbol = context.getResources().getString(R.string.symbol_ss);
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            try {
                songList.add(new Song(field.getName(), field.getInt(field),
                        Uri.parse(prefix + context.getPackageName() + symbol + field.getInt(field))));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return songList;
    }

}
