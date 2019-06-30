package com.example.week1.ui.gallery;

public class GalleryDBCtrct {

    static final String COL_ID = "ID";
    static final String COL_ALBUM = "ALBUM_NAME";
    static final String COL_PATH = "PATH";
    static final String COL_TIMESTAMP = "TIMESTAMP";
    static final String COL_TIME = "DATE";
    static final String COL_COUNT = "COUNT";


    public static final String TBL_GALLERY = "GALLERY_T" ;

    // CREATE TABLE IF NOT EXISTS TBL_GALLERY (NO INTEGER NOT NULL, NAME TEXT, PHONE TEXT, OVER20 INTEGER)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_GALLERY +" "+
            "(" +
            COL_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
            COL_PATH            + " TEXT"                              + ", " +
            COL_ALBUM           + " TEXT"                              + ", " +
            COL_TIMESTAMP       + " TEXT"                              +
            ")" ;

    // DROP TABLE IF EXISTS CONTACT_T
    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_GALLERY ;

    // SELECT * FROM CONTACT_T
    public static final String SQL_SELECT = "SELECT * FROM " + TBL_GALLERY ;

    // INSERT OR REPLACE INTO CONTACT_T (NAME, PHONE) VALUES (x, x)
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_GALLERY + " " +
            "(" + COL_PATH + ", " + COL_ALBUM  + ", " + COL_TIMESTAMP + ") VALUES " ;

    // UPDATE CONTACT_T SET
    public static final String SQL_UPDATE = "UPDATE " + TBL_GALLERY + " SET ";

    // DELETE FROM CONTACT_T
    public static final String SQL_DELETE = "DELETE FROM " + TBL_GALLERY ;

}
