package com.example.week1.persistence;

public class GalleryDBCtrct {

    static final String COL_ID = "ID";
    static final String COL_LOGIN_ID = "LOGIN_ID";
    static final String COL_URL = "URL";
    static final String COL_FILE = "FILE";
    static final String COL_TIMESTAMP = "TIMESTAMP";



    public static final String TBL_GALLERY = "GALLERY_T" ;

    // CREATE TABLE IF NOT EXISTS TBL_GALLERY (NO INTEGER NOT NULL, NAME TEXT, PHONE TEXT, OVER20 INTEGER)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_GALLERY +" "+
            "(" +
            COL_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
            COL_LOGIN_ID        + " TEXT"                              + ", " +
            COL_URL             + " TEXT UNIQUE"                       + ", " +
            COL_FILE            + " TEXT"                              + ", " +
            COL_TIMESTAMP       + " TEXT"                              +
            ")" ;

    // DROP TABLE IF EXISTS CONTACT_T
    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_GALLERY ;

    // SELECT * FROM CONTACT_T
    public static final String SQL_SELECT = "SELECT * FROM " + TBL_GALLERY ;


    // INSERT OR REPLACE INTO GALLERY_T (LOGIN_ID, URL, FILE, TIMESTAMP) VALUES (x, x, x, x)
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_GALLERY + " " +
            "(" + COL_LOGIN_ID + ", " + COL_URL  + ", " + COL_FILE  + ", " +COL_TIMESTAMP + ") VALUES " ;

    // UPDATE CONTACT_T SET
    public static final String SQL_UPDATE = "UPDATE " + TBL_GALLERY + " SET ";

    // DELETE FROM CONTACT_T
    public static final String SQL_DELETE = "DELETE FROM " + TBL_GALLERY ;

}
