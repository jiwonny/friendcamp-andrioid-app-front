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

    public boolean delete_all_photos(){
        try{
            db=helper.getWritableDatabase();
            db.execSQL(ContactDBCtrct.SQL_DELETE);

            return true;
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            helper.close();
        }
        return false;
    }


}
