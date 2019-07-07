package com.example.week1;

import android.content.Intent;
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
import com.example.week1.network.User;
import com.example.week1.persistence.ContactDBAdapter;
import com.example.week1.ui.contact.ContactItem;
import com.example.week1.ui.contact.ContactSearchAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    EditText editSearch;
    ContactSearchAdapter contactSearchAdapter;
    ArrayList<User> contact_items_search = new ArrayList<User>();
    ArrayList<ContactItem> temp_items = new ArrayList<ContactItem>();
    APIClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        apiClient = APIClient.getInstance(this, "143.248.39.49",4500).createBaseApi();

        //------- Recycler View ---------
        RecyclerView.LayoutManager mLayoutManager;
        editSearch = findViewById(R.id.editSearch);
        mRecyclerView = findViewById(R.id.recycler_view_search);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        contact_items_search = load_init_contacts(10);
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
            Log.d("searchText", charText);
            apiClient.searchUserfrom_LoginId(charText, new APICallback() {
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
