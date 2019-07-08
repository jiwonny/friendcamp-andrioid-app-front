package com.example.week1.ui.posts;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import androidx.core.app.ActivityCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Function {


    static final String KEY_LOGIN_ID = "login_id";
    static final String KEY_URL = "url";
    static final String KEY_FILE = "file";
    static final String KEY_TIMESTAMP = "timestamp";



    public static HashMap<String, String> mappingInbox(String login_id, String url, String file, String timestamp)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_LOGIN_ID, login_id);
        map.put(KEY_URL, url);
        map.put(KEY_FILE, file);
        map.put(KEY_TIMESTAMP, timestamp);
        return map;
    }


    public static String converToTime(String timestamp)
    {
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm");
        return formatter.format(date);
    }

    public static Long converToTimeStamp(String time){
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = null;
        try {
            date = (Date)formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long output=date.getTime()/1000L;
        String str=Long.toString(output);
        long timestamp = Long.parseLong(str);
        return timestamp;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

}