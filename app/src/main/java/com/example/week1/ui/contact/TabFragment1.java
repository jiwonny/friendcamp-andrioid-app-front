package com.example.week1.ui.contact;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.R;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * A placeholder fragment containing a simple view.
 */

public class TabFragment1 extends Fragment {

    SQLiteDatabase sqliteDB ;

    public TabFragment1 () {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqliteDB = call_database();
        init_contact_tables();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tabfragment1, container, false);


        ArrayList<ContactItem> list;

        if (isFirstTime()) {
            list = getContactList();
        } else {
            //list = getContactList();
            list = load_contacts();
        }

        RecyclerView recyclerView = root.findViewById(R.id.contact_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ContactAdapter adapter = new ContactAdapter(list);
        recyclerView.setAdapter(adapter);

        JSONObject j = JSON.SQLtoJSON(sqliteDB);
        System.out.println(j.toString());

        return root;
    }

    private Boolean firstTime = null;

    private boolean isFirstTime(){
        if (firstTime == null) {
            SharedPreferences mPreferences = getActivity().getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }

    public ArrayList<ContactItem> getContactList() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts._ID
        };
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, selectionArgs, sortOrder);

        LinkedHashSet<ContactItem> hashlist = new LinkedHashSet<>();

        if (cursor.moveToFirst()) {
            do {
                long photo_id = cursor.getLong(2);
                long person_id = cursor.getLong(3);
                ContactItem contactItem = new ContactItem();
                contactItem.setUser_phNumber(cursor.getString(0));
                contactItem.setUser_Name(cursor.getString(1));
                contactItem.setPhoto_id(photo_id);
                contactItem.setPerson_id(person_id);
                hashlist.add(contactItem);

                // put in database (name,phone)
                insert_contact(cursor.getString(1),cursor.getString(0));

            } while (cursor.moveToNext());
        }

        ArrayList<ContactItem> contactItems = new ArrayList<>(hashlist);
        for (int i = 0; i < contactItems.size(); i++) {
            contactItems.get(i).setId(i);
        }
        return contactItems;
    }

    private SQLiteDatabase call_database() {

        SQLiteDatabase db = null ;

        File file = new File(getActivity().getFilesDir(), "Database.db") ;

        System.out.println("PATH : " + file.toString()) ;
        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null) ;
        } catch (SQLiteException e) {
            e.printStackTrace() ;
        }

        if (db == null) {
            System.out.println("DB call failed. " + file.getAbsolutePath()) ;
        }
        return db ;
    }

    private void init_contact_tables() {
        if (sqliteDB != null) {
            String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS CONTACT_T (" +
                    "ID "           + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME "         + "TEXT," +
                    "PHONE "        + "TEXT"  + ")" ;

            //System.out.println(sqlCreateTbl) ;
            sqliteDB.execSQL(sqlCreateTbl) ;
        }
    }

    private void insert_contact(String name, String phone) {
        if (sqliteDB != null) {

            String sqlInsert = "INSERT INTO CONTACT_T " + "(NAME, PHONE) VALUES ('" + name + "','" + phone + "')";

            System.out.println(sqlInsert) ;
            sqliteDB.execSQL(sqlInsert) ;
        }
    }

    public ArrayList<ContactItem> load_contacts() {

        if (sqliteDB != null) {
            String sqlQueryTbl = "SELECT * FROM CONTACT_T";
            Cursor cursor = null;

            cursor = sqliteDB.rawQuery(sqlQueryTbl, null);

            LinkedHashSet<ContactItem> hashlist = new LinkedHashSet<>();

            if (cursor.moveToFirst()) {
                do {
                    ContactItem contactItem = new ContactItem();

                    contactItem.setUser_Name(cursor.getString(1));
                    contactItem.setUser_phNumber(cursor.getString(2));
                    contactItem.setId(cursor.getInt(0));
                    hashlist.add(contactItem);

                } while (cursor.moveToNext());
            }

            ArrayList<ContactItem> contactItems = new ArrayList<>(hashlist);
            return contactItems;
        }
        return null;
    }
}