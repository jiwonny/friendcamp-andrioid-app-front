package com.example.week1.ui.contact;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ContactDBAdapter {

    Context c;
    SQLiteDatabase db;
    ContactDBHelper helper;

    // Initialize DB helper and pass it a context

    public ContactDBAdapter(Context c){
        this.c =c;
        helper = new ContactDBHelper(c);
    }

    // Insert data to DB
    public boolean insert_contact(String name, String phone) {
        try {
            db = helper.getWritableDatabase();
            String sqlInsert = ContactDBCtrct.SQL_INSERT + "(" + "'" + name + "'" + "," + "'" + phone + "'" + ")";
            System.out.println(sqlInsert);
            db.execSQL(sqlInsert);

            return true;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }finally {
            helper.close();
        }
        return false;
    }

    // Select all and make Arraylist

    public ArrayList<ContactItem> retreive_all_contacts() {
        ArrayList<ContactItem> contactItems;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(ContactDBCtrct.SQL_SELECT, null);

        LinkedHashSet<ContactItem> hashlist = new LinkedHashSet<>();

        if (cursor.moveToFirst()) {
            do {
                ContactItem contactItem = new ContactItem();

                contactItem.setId(cursor.getInt(0));
                contactItem.setUser_Name(cursor.getString(1));
                contactItem.setUser_phNumber(cursor.getString(2));

                hashlist.add(contactItem);

            } while (cursor.moveToNext());
        }
        contactItems = new ArrayList<>(hashlist);

        helper.close();
        return contactItems;
    }

}
