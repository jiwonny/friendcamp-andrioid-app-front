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
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.ApiService;
import com.example.week1.network.User;
import com.example.week1.persistence.ContactDBAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;

import static android.content.Context.MODE_PRIVATE;

/**
 * A placeholder fragment containing a simple view.
 */

public class TabFragment1 extends Fragment {

    static final int REQ_ADD_CONTACT =1;
    static final int REQ_EDIT_CONTACT =2;
    static final int REQ_DELETE_CONTACT =3;
    static final int REQ_CALL_CONTACT =4;
    static final int REQ_SYNC_CONTACT=5;

    ArrayList<ContactItem> contact_items = new ArrayList<ContactItem>();
    RecyclerView recyclerView;
    ContactAdapter adapter;
    Loadcontacts loadcontactTask;
    Sync_contacts synchronization;
    APIClient apiClient;


    public TabFragment1(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = APIClient.getInstance(getActivity(), "143.248.39.49",4500).createBaseApi();

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tabfragment1, container, false);

        // ADD CONTACT Button
        Button add_contact = root.findViewById(R.id.add_contact);
        add_contact.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), Edit_Contact.class) ;
                startActivityForResult(intent, REQ_ADD_CONTACT);
            }
        });

        // SYNCHRONIZATION CONTACT Button
        FloatingActionButton sync_contact = root.findViewById(R.id.sync_Button);
        sync_contact.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                synchronization = new TabFragment1.Sync_contacts();
                synchronization.execute();
            }
        });

        recyclerView = root.findViewById(R.id.contact_recycler);

        loadcontactTask = new TabFragment1.Loadcontacts();
        loadcontactTask.execute();


        return root;
    }

    // Run LoadContacts in Background Thread
    class Loadcontacts extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            contact_items.clear();
        }

        @Override
        protected String doInBackground(String... args) {
            String xml = "";

            // Load Contacts from DB
            contact_items= load_contacts();

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            Log.d("main","generate recycleview 11111111111111111111111111111111111111111111111111111111");
            adapter = new ContactAdapter(contact_items);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            // EDIT & DELETE & CALL CONTACT
            adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, int request_code){
                    switch (request_code){
                        case REQ_EDIT_CONTACT:{
                            Intent intent = new Intent(getActivity(), Edit_Contact.class);
                            intent.putExtra("position",position);
                            intent.putExtra("name", contact_items.get(position).getUser_Name());
                            intent.putExtra("number", contact_items.get(position).getUser_phNumber());
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

                    //TODO : CHECK & FIX
                    db.insert_contact("aa" ,name,number);
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
    User current_user = new User();
    ArrayList<User> current_user_friends = new ArrayList<User>();
    private boolean isFirstTime(){
        if (firstTime == null) {
            SharedPreferences mPreferences = getActivity().getSharedPreferences("first_time", MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }

    class Sync_contacts extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            String xml = "";
            final Uri[] uri = {ContactsContract.CommonDataKinds.Phone.CONTENT_URI};
            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            };
            final String[][] selectionArgs = {null};
            String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            Cursor cursor = getActivity().getContentResolver().query(uri[0], projection, null, selectionArgs[0], sortOrder);

            ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();
            ContactDBAdapter db = new ContactDBAdapter(getActivity());

            //------현재 user 정보 불러오기-------
            SharedPreferences sf = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
            String current_login_id = sf.getString("currentUser_email", "");
            String current_name = sf.getString("currentUser_name", "");


            apiClient.getUserfrom_Name_LoginId(current_name, current_login_id, new APICallback() {
                @Override
                public void onError(Throwable t) { }
                @Override
                public void onSuccess(int code, Object receivedData) {
                    current_user = (User) receivedData;
                    Log.d("current_friend", "hi "+ current_user.getFriends());
                    Log.d("current", "current user 넣기"+current_user.getName());

                    if(current_user.getFriends() == null){
                        return;
                    }else{
                        Iterator iterator = current_user.getFriends().iterator();

                        while(iterator.hasNext()){
                            current_user_friends.add((User) iterator.next());
                        }

                    }


                }
                @Override
                public void onFailure(int code) {
                }
            });

            //------현재 user 정보 불러오기 끝----

            if (cursor.moveToFirst()) {
                do {
                    String Name = cursor.getString(1);
                    String phNumber = cursor.getString(0);
                    phNumber.replace("-", "");
                    apiClient.getUserfrom_Name_Number(Name, phNumber, new APICallback() {
                        @Override
                        public void onError(Throwable t) { }
                        @Override
                        public void onSuccess(int code, Object receivedData) {
                            User data = (User) receivedData;
                            String login_id = data.getLogin_id(); // 불러오는 login_id

                            int checkDuplicate = -1;
                            for(User user : current_user_friends){
                                if(user.getLogin_id().equals(login_id)){
                                    checkDuplicate ++;
                                    break;
                                }
                                else continue;
                            }

                            if(checkDuplicate == -1){
                                Log.i("D", "Theres no duplicate");
                                current_user_friends.add(data);
                            }

                        }
                        @Override
                        public void onFailure(int code) {
                            Log.e("FAIL", String.format("code : %d", code));
                        }
                    });
                } while (cursor.moveToNext());
            }

            // ------ 서버 디비에 넣기 위한 작업 ----------
            current_user.setFriends(current_user_friends);

            for(User user : current_user_friends){
                String login_id = user.getLogin_id();
                String name = user.getName();
                String number = user.getNumber();
                db.insert_contact(login_id,name,number);
            }


            // 서버 디비에 친구목록 추가
            apiClient.update_User(current_login_id, current_user, new APICallback() {
                @Override
                public void onError(Throwable t) { }

                @Override
                public void onSuccess(int code, Object receivedData) {
                    User data = (User) receivedData;
                    Log.d("Success_add", data.getName());
                }

                @Override
                public void onFailure(int code) {
                    Log.e("FRIEND ADD FAIL", String.format("code : %d", code));
                }
            });

            // ----------서버 디비 넣기 작업 끝 -----------
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {
            getActivity().recreate();
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