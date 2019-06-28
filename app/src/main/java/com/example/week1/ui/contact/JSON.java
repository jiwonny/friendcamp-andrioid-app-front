package com.example.week1.ui.contact;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashSet;

public class JSON {

    protected static JSONObject SQLtoJSON(SQLiteDatabase sqliteDB){

        JSONObject jsonMain = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        String sqlQueryTbl = "SELECT * FROM CONTACT_T";

        Cursor cursor = null;
        cursor = sqliteDB.rawQuery(sqlQueryTbl, null);

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
