package com.example.week1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.User;
import com.example.week1.persistence.ContactDBAdapter;
import com.example.week1.ui.contact.ContactItem;
import com.example.week1.ui.contact.ContactSearchAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchActivity extends AppCompatActivity{
    RecyclerView mRecyclerView;
    EditText editSearch;
    private ContactSearchAdapter contactSearchAdapter;
    ArrayList<User> contact_items_search = new ArrayList<User>();
    ArrayList<ContactItem> temp_items = new ArrayList<ContactItem>();
    APIClient apiClient;
    User addUser = new User();
    User currentUser = new User();
    ContactDBAdapter db = new ContactDBAdapter(this);
    public Boolean check = true;
    Gson gson = new GsonBuilder().create();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        IPInfo ip = new IPInfo();
        String address = ip.IPAddress;

        apiClient = APIClient.getInstance(this, address,4500).createBaseApi();

        SharedPreferences sf = getSharedPreferences("userFile", MODE_PRIVATE);
        SharedPreferences add_sf = getSharedPreferences("add_user_file", MODE_PRIVATE);
//        String current_login_id = sf.getString("currentUser_email", "");
//        String current_name = sf.getString("currentUser_name", "");

        //###### 현재 로그인한 user 정보 불러오기.########
        String user_instance = sf.getString("currentUser", "");
        Log.d("user_instance check", user_instance);
        // 변환
        User currentUser = gson.fromJson(user_instance, User.class);
        //currentUser 란 현재 로그인한 user 정보.
        Log.d("gsonUser", currentUser.getNumber());
        ArrayList<User> current_user_friends = new ArrayList<User>();
        if(currentUser.getFriends() == null){
            Log.d("current user info", "-----------------");
        }
        else {
            Log.d("current user info", "--" + currentUser.getName());
            Iterator iterator = currentUser.getFriends().iterator();

            while (iterator.hasNext()) {
                current_user_friends.add((User) iterator.next());
            }
            Log.d("Friends list", current_user_friends.get(0).getName());
        }
        //#########현재 user 정보 불러오기 끝########

        //------- Recycler View ---------
        RecyclerView.LayoutManager mLayoutManager;
        editSearch = findViewById(R.id.editSearch);
        mRecyclerView = findViewById(R.id.recycler_view_search);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //contact_items_search = load_init_contacts(10);
        temp_items = load_all_contacts();

        //contact_items_search : 처음에 검색되는 items
        contactSearchAdapter = new ContactSearchAdapter(contact_items_search);
        mRecyclerView.setAdapter(contactSearchAdapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });

        final Button searchCancel = (Button) findViewById(R.id.search_cancel);
        searchCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
                overridePendingTransition(R.anim.no_change,R.anim.slide_down_info);
            }
        });

        //TODO : 친구 눌렀을 때 추가하기.
        // 추가하기
        String add_name = add_sf.getString("add_user_name","");
        String add_number = add_sf.getString("add_user_number","");

        try{
            new AsyncTask<Void, Void, Boolean>(){
                @Override
                protected Boolean doInBackground(Void... params){
                    apiClient.getUserfrom_Name_Number(add_name, add_number, new APICallback() {
                        @Override
                        public void onError(Throwable t) { }

                        @Override
                        public void onSuccess(int code, Object receivedData) {
                            // 추가할 user 에 대한 user 객체
                            check = true;
                            addUser = (User) receivedData;
                            Log.d("who are you", "addUser"+addUser.getName());
                        }

                        @Override
                        public void onFailure(int code) { check = false;}
                    });

                    return check;
                }

                @Override
                protected void onPostExecute(Boolean s){
                    super.onPostExecute(s);
                }
            }.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
        //추가할 user 가 누구인가 -> friend 목록에 추가.
//
//        apiClient.getUserfrom_Name_Number(add_name, add_number, new APICallback() {
//            @Override
//            public void onError(Throwable t) { }
//
//            @Override
//            public void onSuccess(int code, Object receivedData) {
//                // 추가할 user 에 대한 user 객체
//                addUser = (User) receivedData;
//                Log.d("who are you", "addUser"+addUser.getName());
//            }
//
//            @Override
//            public void onFailure(int code) { }
//        });
//
//        //db 에 연락처를 추가
//        db.insert_contact(addUser.getLogin_id(), addUser.getName(), addUser.getNumber());
//        currentUser.setFriends(current_user_friends);
//
//        // 서버 디비에 친구목록 추가
//        apiClient.update_User(currentUser.getLogin_id(), currentUser, new APICallback() {
//            @Override
//            public void onError(Throwable t) { }
//
//            @Override
//            public void onSuccess(int code, Object receivedData) {
//                User data = (User) receivedData;
//                Log.d("Success_add", data.getName());
//            }
//
//            @Override
//            public void onFailure(int code) {
//                Log.e("FRIEND ADD FAIL", String.format("code : %d", code));
//            }
//        });

    }

    private ArrayList<ContactItem> load_all_contacts(){
        ContactDBAdapter db = new ContactDBAdapter(this);
        return db.retreive_all_contacts();
    }
    private ArrayList<ContactItem> load_init_contacts(int number){
        ContactDBAdapter db = new ContactDBAdapter(this);
        return db.retreive_rand_contacts(number);
    }


    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        contact_items_search.clear();
        charText = charText.toLowerCase();

        // 검색하는 것에 공백이 있을 경우.
        charText = charText.replaceAll(" ", "");
        charText = charText.replaceAll("\\p{Z}", "");

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            contact_items_search.addAll(contact_items_search);
        }
        // 문자 입력을 할때..
        else
        {
//            // 리스트의 모든 데이터를 검색한다.
//            for(int i = 0;i < temp_items.size(); i++)
//            {
//                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
//                if (temp_items.get(i).getUser_Name().toLowerCase().replaceAll(" ", "").contains(charText))
//                {
//                    // 검색된 데이터를 리스트에 추가한다.
//                    contact_items_search.add(temp_items.get(i));
//                }
//            }
            Log.d("searchText", charText);
            apiClient.getUserfrom_LoginId(charText, new APICallback() {
                @Override
                public void onError(Throwable t) {
                    Log.e("Error", "error in char");
                }

                @Override
                public void onSuccess(int code, Object receivedData) {
                    List<User> users = (List<User>)receivedData;
                    Log.d("search", "hh "+ users);
                    for(User user : users){
                        contact_items_search.add(user);
                    }
                    // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
                    contactSearchAdapter.notifyDataSetChanged();
                }
                @Override
                public void onFailure(int code) {
                    Log.e("Failr", "failin char");
                }
            });
        }

    }
}
