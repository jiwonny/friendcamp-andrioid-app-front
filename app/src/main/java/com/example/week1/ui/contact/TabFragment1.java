package com.example.week1.ui.contact;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.week1.persistence.ContactDBAdapter;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */

public class TabFragment1 extends Fragment {

    static final int REQ_ADD_CONTACT = 1 ;
    static final int REQ_EDIT_CONTACT = 2 ;
    static final int REQ_DELETE_CONTACT = 3;
    static final int REQ_CALL_CONTACT =4;

    ArrayList<ContactItem> contact_items = new ArrayList<ContactItem>();
    RecyclerView recyclerView;
    ContactAdapter adapter;
    Loadcontacts loadcontactTask;


    public TabFragment1 (){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tabfragment1, container, false);

        ContactDBAdapter db = new ContactDBAdapter(getActivity());

        recyclerView = root.findViewById(R.id.contact_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadcontactTask = new TabFragment1.Loadcontacts();
        loadcontactTask.execute();


        // ADD CONTACT Button
        Button add_contact = root.findViewById(R.id.add_contact);
        add_contact.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), Edit_Contact.class) ;
                startActivityForResult(intent, REQ_ADD_CONTACT);
            }
        });



        //JSON json = new JSON(getActivity());
        //JSONObject j = json.SQLtoJSON();
        //System.out.println(j.toString());

        return root;
    }
    class Loadcontacts extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            contact_items.clear();
        }

        @Override
        protected String doInBackground(String... args) {
            String xml = "";

            if (isFirstTime()) { getContactList(); }

            // Load Contacts from DB
            contact_items= load_contacts();

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            adapter = new ContactAdapter(contact_items);
            recyclerView.setAdapter(adapter);

            // EDIT & DELETE & CALL CONTACT
            adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, int request_code){
                    switch (request_code){
                        case REQ_EDIT_CONTACT:{
                            Intent intent = new Intent(getActivity(), Edit_Contact.class);
                            intent.putExtra("position",position);
                            startActivityForResult(intent, request_code);
                            break;
                        }
                        case REQ_DELETE_CONTACT:{
                            ContactDBAdapter db = new ContactDBAdapter(getActivity());
                            ContactItem contactItem = contact_items.get(position);
                            String name = contactItem.getUser_Name();
                            String number = contactItem.getUser_phNumber();

                            db.delete_contact(name,number);
                            contact_items.remove(position);

                            adapter.onActivityResult(REQ_DELETE_CONTACT,1);
                            break;
                        }
                        case REQ_CALL_CONTACT:{
                            ContactItem contactItem = contact_items.get(position);
                            String number = contactItem.getPhNumberChanged();
                            String tel ="tel:" + number;
                            startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                            break;
                        }
                    }
                }
            });
        }
    }

    // get Result from add or edit contact
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch(requestCode){
            case REQ_ADD_CONTACT:{
                if (resultCode == Activity.RESULT_OK){
                    ContactDBAdapter db = new ContactDBAdapter(getActivity());

                    String name = intent.getStringExtra("contact_name");
                    String number = intent.getStringExtra("contact_number");

                    ContactItem contactItem = new ContactItem();
                    contactItem.setUser_Name(name);
                    contactItem.setUser_phNumber(number);

                    db.insert_contact(name,number);
                    ArrayList<ContactItem> new_contact_items = load_contacts();

                    int pos = new_contact_items.indexOf(contactItem);

                    contact_items.add(pos, contactItem);

                    adapter.onActivityResult(REQ_ADD_CONTACT,1);
                    break;
                }
            }

            case REQ_EDIT_CONTACT:{
                if (resultCode == Activity.RESULT_OK){

                    ContactDBAdapter db = new ContactDBAdapter(getActivity());
                    int pos = intent.getIntExtra("position",-1);

                    String new_name = intent.getStringExtra("contact_name");
                    String new_number = intent.getStringExtra("contact_number");

                    ContactItem contactItem = contact_items.get(pos);

                    String name = contactItem.getUser_Name();
                    String number = contactItem.getUser_phNumber();

                    contactItem.setUser_Name(new_name);
                    contactItem.setUser_phNumber(new_number);

                    db.update_contact(name,number,new_name,new_number);

                    //TODO : ASCENDING ORDER WHEN EDIT
                    //ArrayList<ContactItem> new_contact_items = load_contacts();
                    //int new_pos = new_contact_items.indexOf(contactItem);

                    contact_items.set(pos, contactItem);

                    adapter.onActivityResult(REQ_EDIT_CONTACT,1);
                    break;
                }
            }
        }
    }

    // check firstTime
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


    public void getContactList() {

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

                // put in database (name,phone)
                ContactDBAdapter db = new ContactDBAdapter(getActivity());
                db.insert_contact(cursor.getString(1),cursor.getString(0));

            } while (cursor.moveToNext());
        }
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