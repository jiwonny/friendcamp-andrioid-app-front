package com.example.week1.ui.contact;

public class ContactDBCtrct {

    public static final String COL_ID = "NO" ;
    public static final String COL_NAME = "NAME" ;
    public static final String COL_PHONE = "PHONE" ;


    public static final String TBL_CONTACT = "CONTACT_T" ;

    // CREATE TABLE IF NOT EXISTS CONTACT_T (NO INTEGER NOT NULL, NAME TEXT, PHONE TEXT, OVER20 INTEGER)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_CONTACT +" "+
            "(" +
            COL_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
            COL_NAME         + " TEXT"                              + ", " +
            COL_PHONE        + " TEXT"                              +
            ")" ;

    // DROP TABLE IF EXISTS CONTACT_T
    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_CONTACT ;

    // SELECT * FROM CONTACT_T
    public static final String SQL_SELECT = "SELECT * FROM " + TBL_CONTACT +" ORDER BY " + COL_NAME +" ASC";

    // INSERT OR REPLACE INTO CONTACT_T (NAME, PHONE) VALUES (x, x)
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_CONTACT + " " +
            "(" + COL_NAME + ", " + COL_PHONE  + ") VALUES " ;

    // UPDATE CONTACT_T SET
    public static final String SQL_UPDATE = "UPDATE " + TBL_CONTACT + " SET ";

    // DELETE FROM CONTACT_T
    public static final String SQL_DELETE = "DELETE FROM " + TBL_CONTACT ;

}
