package com.example.week1.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ContactDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1 ;
    public static final String DBFILE_CONTACT = "Database.db" ;

    public ContactDBHelper(Context context) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println(ContactDBCtrct.SQL_CREATE_TBL);
        db.execSQL(ContactDBCtrct.SQL_CREATE_TBL) ;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ContactDBCtrct.SQL_DROP_TBL) ;
        db.execSQL(ContactDBCtrct.SQL_CREATE_TBL) ;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // onUpgrade(db, oldVersion, newVersion);
    }
}
