package com.example.week1.ui.contact;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * A placeholder fragment containing a simple view.
 */

public class TabFragment1 extends Fragment {

    ContactDBHelper dbHelper = null ;

    public static TabFragment1 newInstance(){
        return new TabFragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tabfragment1, container, false);

        ContactDBAdapter db = new ContactDBAdapter(getActivity());

        // Load Contacts from DB
        ArrayList<ContactItem> contact_items;
        if (isFirstTime()) {
            contact_items = getContactList();
        } else{
            contact_items= load_contacts();
        }

        // ADD CONTACT Button
        Button add_contact = root.findViewById(R.id.add_contact);
        add_contact.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){


            }
        });



        RecyclerView recyclerView = root.findViewById(R.id.contact_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ContactAdapter adapter = new ContactAdapter(contact_items);
        recyclerView.setAdapter(adapter);

        //JSON json = new JSON(getActivity());
        //JSONObject j = json.SQLtoJSON();
        //System.out.println(j.toString());

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

                Bitmap photo = loadContactPhoto(getActivity().getContentResolver(),person_id,photo_id);
                contactItem.setUser_photo(photo);

                hashlist.add(contactItem);

                // put in database (name,phone)
                ContactDBAdapter db = new ContactDBAdapter(getActivity());
                db.insert_contact(cursor.getString(1),cursor.getString(0));

            } while (cursor.moveToNext());
        }

        ArrayList<ContactItem> contactItems = new ArrayList<>(hashlist);
        for (int i = 0; i < contactItems.size(); i++) {
            contactItems.get(i).setId(i);
        }
        return contactItems;
    }

    private ArrayList<ContactItem> load_contacts(){
        ContactDBAdapter db = new ContactDBAdapter(getActivity());
        return db.retreive_all_contacts();
    }

    public Bitmap loadContactPhoto(ContentResolver cr, long id, long photo_id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input != null)
            return resizingBitmap(BitmapFactory.decodeStream(input));
        else
            Log.d("PHOTO","first try failed to load photo");

        byte[] photoBytes = null;
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
        Cursor c = cr.query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
        try {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }

        if (photoBytes != null)
            return resizingBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));

        else
            Log.d("PHOTO", "second try also failed");
        return null;
    }

    public Bitmap resizingBitmap(Bitmap oBitmap) {
        if (oBitmap == null)
            return null;
        float width = oBitmap.getWidth();
        float height = oBitmap.getHeight();
        float resizing_size = 120;
        Bitmap rBitmap = null;
        if (width > resizing_size) {
            float mWidth = (float) (width / 100);
            float fScale = (float) (resizing_size / mWidth);
            width *= (fScale / 100);
            height *= (fScale / 100);

        } else if (height > resizing_size) {
            float mHeight = (float) (height / 100);
            float fScale = (float) (resizing_size / mHeight);
            width *= (fScale / 100);
            height *= (fScale / 100);
        }
        //Log.d("rBitmap : " + width + "," + height);
        rBitmap = Bitmap.createScaledBitmap(oBitmap, (int) width, (int) height, true);
        return rBitmap;
    }

}