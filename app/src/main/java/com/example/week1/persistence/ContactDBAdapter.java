package com.example.week1.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.week1.ui.contact.ContactItem;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;

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
    public boolean insert_contact(String login_id, String name, String phone) {
        try {
            db = helper.getWritableDatabase();
            String sqlInsert = ContactDBCtrct.SQL_INSERT + "(" + "'" + login_id + "'" + ","+ "'" + name + "'" + "," + "'" + phone + "'" + ")";
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

    // Update data from (name, phone) to (new_name, new_phone)
    public boolean update_contact(String name, String phone, String new_name, String new_phone) {
        try{
            db = helper.getWritableDatabase();
            String sqlUpdate = ContactDBCtrct.SQL_UPDATE +  ContactDBCtrct.COL_NAME     + " = " +   "'" + new_name + "', " +
                                                            ContactDBCtrct.COL_PHONE    + " = " +   "'" + new_phone + "' " +
                                                "WHERE " +
                                                            ContactDBCtrct.COL_NAME     + " == " +   "'" + name + "' " +"AND "+
                                                            ContactDBCtrct.COL_PHONE    + " == " +   "'" + phone + "' " ;
            System.out.println(sqlUpdate);
            db.execSQL(sqlUpdate);

            return true;
        }catch(SQLException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
        return false;
    }

    // Delete (name, phone)
    public boolean delete_contact(String name, String phone) {
        try{
            db=helper.getWritableDatabase();
            String sqlDelete = ContactDBCtrct.SQL_DELETE + " WHERE " +
                                            ContactDBCtrct.COL_NAME     + " == " +   "'" + name + "' " +"AND "+
                                            ContactDBCtrct.COL_PHONE    + " == " +   "'" + phone + "' " ;
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


    // Select all and retreive ContactItem Arraylist
    public ArrayList<ContactItem> retreive_all_contacts() {
        ArrayList<ContactItem> contactItems;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(ContactDBCtrct.SQL_SELECT, null);

        LinkedHashSet<ContactItem> hashlist = new LinkedHashSet<>();

        if (cursor.moveToFirst()) {
            do {
                ContactItem contactItem = new ContactItem();

                contactItem.setId(cursor.getInt(0));
                contactItem.setUser_Name(cursor.getString(2));
                contactItem.setUser_phNumber(cursor.getString(3));

                hashlist.add(contactItem);

            } while (cursor.moveToNext());
        }
        cursor.close();
        contactItems = new ArrayList<>(hashlist);

        helper.close();
        return contactItems;
    }

    // Select part of contact list randomly(??)
    public ArrayList<ContactItem> retreive_rand_contacts(int number){
        ArrayList<ContactItem> contactItems;
        contactItems = this.retreive_all_contacts();
        ArrayList<ContactItem> resultItems = new ArrayList<>();
        resultItems.clear();

        if(contactItems.size() < number){
            return contactItems;
        }

        Random random = new Random();
        for(int cnt = 0 ; cnt < number ; cnt ++){
            resultItems.add( contactItems.get(random.nextInt(number)));
        }

        return resultItems;
    }
}
