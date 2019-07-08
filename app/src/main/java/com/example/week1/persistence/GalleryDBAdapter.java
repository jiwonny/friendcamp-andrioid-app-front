package com.example.week1.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.week1.ui.gallery.Function;
import com.example.week1.ui.gallery.MapComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GalleryDBAdapter {

    Context c;
    SQLiteDatabase db;
    GalleryDBHelper helper;

    static final String KEY_ALBUM = "album_name";
    static final String KEY_PATH = "path";
    static final String KEY_TIMESTAMP = "timestamp";
    static final String KEY_TIME = "date";
    static final String KEY_COUNT = "count";

    // Initialize DB helper and pass it a context

    public GalleryDBAdapter(Context c){
        this.c =c;
        helper = new GalleryDBHelper(c);
    }

    // Insert data to DB
    public boolean insert_photo(String login_id, String url, String file, String time_stamp) {
        try {
            db = helper.getWritableDatabase();
            String sqlInsert = GalleryDBCtrct.SQL_INSERT + "(" + "'" + login_id + "'" + "," + "'" + url + "'" +","+ "'" + file + "'" +","+"'" + time_stamp + "'" + ")";
            System.out.println(sqlInsert);
            db.execSQL(sqlInsert);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            helper.close();
        }
        return false;
    }

    public boolean delete_photo(String login_id, String url) {
        try{
            db=helper.getWritableDatabase();
            String sqlDelete = GalleryDBCtrct.SQL_DELETE + " WHERE " +
                                    GalleryDBCtrct.COL_LOGIN_ID     + " == " +   "'" + login_id     + "' " +"AND "+
                                    GalleryDBCtrct.COL_URL          + " == " +   "'" + url          + "' " ;
            System.out.println(sqlDelete);
            db.execSQL(sqlDelete);

            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
        return false;
    }

    // Select ALl
    public ArrayList<HashMap<String, String>> sellect_all(){
        ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlSelect = GalleryDBCtrct.SQL_SELECT;
        Log.d("SQL", sqlSelect);
        Cursor cursor = db.rawQuery(sqlSelect, null);

        if (cursor.moveToFirst()) {
            do {
                String login_id = cursor.getString(1);
                String url = cursor.getString(2);
                String file = cursor.getString(3);
                String timestamp = cursor.getString(4);


                imageList.add(Function.mappingInbox(login_id, url, file, timestamp));
            } while(cursor.moveToNext());
        }
        cursor.close();
        helper.close();
        Log.d("length", String.format("aaaaaaaaaaaaaa : %d aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", imageList.size() ));
        return imageList;
    }

/*
    // Select * Group by Album
    public ArrayList<HashMap<String, String>> retreive_photos_byAlbums() {

        ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlGROUPBY = GalleryDBCtrct.SQL_SELECT_1 + " GROUP BY " + GalleryDBCtrct.COL_ALBUM;
        System.out.println(sqlGROUPBY);
        Cursor cursor = db.rawQuery(sqlGROUPBY, null);

        if (cursor.moveToFirst()) {
            do {

                String path = null;
                String album = null;
                String timestamp = null;
                String countPhoto = null;

                path = cursor.getString(0);
                album = cursor.getString(1);
                timestamp = cursor.getString(2);
                countPhoto = getCount(album);

                //System.out.println(String.format("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa, %s,%s, %s, %s ", path, album, timestamp, countPhoto));

                albumList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), countPhoto));

            } while (cursor.moveToNext());
        }
        cursor.close();
        helper.close();
        Collections.sort(albumList, new MapComparator(KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
        return albumList;
    }

    // Select * Where Album

    public ArrayList<HashMap<String, String>> retreive_photos_inAlbum(String album_name) {

        ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlWHERE = GalleryDBCtrct.SQL_SELECT + " WHERE " + GalleryDBCtrct.COL_ALBUM + " == " + "'"+album_name+"'";
        System.out.println(sqlWHERE);
        Cursor cursor = db.rawQuery(sqlWHERE, null);

        if (cursor.moveToFirst()) {
            do {

                String path = null;
                String album = null;
                String timestamp = null;

                path = cursor.getString(1);
                album = cursor.getString(2);
                timestamp = cursor.getString(3);

                System.out.println(String.format("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb, %s,%s, %s,", path, album, timestamp));

                imageList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), null));

            } while (cursor.moveToNext());
        }

        cursor.close();
        helper.close();
        Collections.sort(imageList, new MapComparator(KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
        return imageList;
    }
    */

    public String getCount(String album_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlSelect1 = GalleryDBCtrct.SQL_SELECT + " WHERE " + GalleryDBCtrct.COL_LOGIN_ID + " == " + "'"+album_name+"'";
        Cursor cursor = db.rawQuery(sqlSelect1,null);

        return cursor.getCount()+" Photos";
    }

}
