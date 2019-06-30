package com.example.week1.ui.contact;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashSet;

public class JSON {

    Context c;
    SQLiteDatabase db;
    ContactDBHelper helper;

    public JSON(Context c){
        this.c =c;
        helper = new ContactDBHelper(c);
    }

    protected JSONObject SQLtoJSON(){

        db = helper.getReadableDatabase() ;

        JSONObject jsonMain = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        String sqlQueryTbl = "SELECT * FROM CONTACT_T";

        Cursor cursor = null;
        cursor = db.rawQuery(sqlQueryTbl, null);

        if (cursor.moveToFirst()) {
            do{
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("NAME", cursor.getString(1));
                    jsonObject.put("PHONE",cursor.getString(2));
                }catch (JSONException e) {
                        e.printStackTrace();
                }
                jsonArray.put(jsonObject);

            } while (cursor.moveToNext());
        }

        try {
            jsonMain.put("ContacDataSet", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonMain;
    }
}
