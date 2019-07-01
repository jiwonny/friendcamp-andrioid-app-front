package com.example.week1.ui.gallery;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GalleryDBAdapter {

    Context c;
    SQLiteDatabase db;
    GalleryDBHelper helper;

    // Initialize DB helper and pass it a context

    public GalleryDBAdapter(Context c){
        this.c =c;
        helper = new GalleryDBHelper(c);
    }

    // Insert data to DB
    public boolean insert_photo(String path, String album, String time_stamp) {
        try {
            db = helper.getWritableDatabase();
            String sqlInsert = GalleryDBCtrct.SQL_INSERT + "(" + "'" + path + "'" + "," + "'" + album + "'" +","+"'" + time_stamp + "'" + ")";
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

    public boolean delete_photo(String path, String album, String time_stamp) {
        try{
            db=helper.getWritableDatabase();
            String sqlDelete = GalleryDBCtrct.SQL_DELETE + " WHERE " +
                                    GalleryDBCtrct.COL_PATH     + " == " +   "'" + path + "' " +"AND "+
                                    GalleryDBCtrct.COL_ALBUM    + " == " +   "'" + album + "' " +"AND "+
                                    GalleryDBCtrct.COL_TIMESTAMP   + " == " +   "'" + time_stamp + "' ";
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

                System.out.println(String.format("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa, %s,%s, %s, %s ", path, album, timestamp, countPhoto));

                albumList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), countPhoto));

            } while (cursor.moveToNext());
        }
        cursor.close();
        helper.close();
        Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
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
        Collections.sort(imageList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
        return imageList;
    }

    public String getCount(String album_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlSelect1 = GalleryDBCtrct.SQL_SELECT + " WHERE " + GalleryDBCtrct.COL_ALBUM + " == " + "'"+album_name+"'";
        Cursor cursor = db.rawQuery(sqlSelect1,null);

        return cursor.getCount()+" Photos";
    }


}
