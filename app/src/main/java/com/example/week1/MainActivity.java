package com.example.week1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.Image_f;
import com.example.week1.network.User;
import com.example.week1.persistence.ContactDBAdapter;
import com.example.week1.persistence.ContactDBHelper;
import com.example.week1.persistence.GalleryDBAdapter;
import com.example.week1.ui.gallery.Function;
import com.example.week1.ui.gallery.TabFragment2;
import com.example.week1.ui.login.LoginActivity;
import com.example.week1.ui.main.SectionsPagerAdapter;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    static final int REQ_PICK_IMAGE = 1 ;

    APIClient apiClient;

    ContactDBHelper dbHelper = null ;

    String user_name;
    String user_id;
    String user_number;
    String user_profile;
    IPInfo ip = new IPInfo();
    String address = ip.IPAddress;
    int port = ip.Port;

    View headerLayout;
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    //update_profileimage update_profileimage;
    boolean isLoggedIn;
    boolean isFacebook;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        SharedPreferences sf = getSharedPreferences("userFile", MODE_PRIVATE);
        SharedPreferences add_sf = getSharedPreferences("add_user_file", MODE_PRIVATE);

        isFacebook = sf.getBoolean("Facebook", true);
        user_id = sf.getString("currentUser_email", "");
        user_name = sf.getString("currentUser_name", "");
        user_number = sf.getString("currentUser_number", "");
        user_profile = sf.getString("currentUser_profile","");

        Log.d("MainActivity", String.format("id: %s , name: %s , number %s, profile, %s", user_id,user_name,user_number,user_profile));


        apiClient = APIClient.getInstance(this, address,port).createBaseApi();
        ContactDBAdapter db = new ContactDBAdapter(this);
        GalleryDBAdapter db1 = new GalleryDBAdapter(this);


        setContentView(R.layout.activity_main);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        Toolbar tb = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(tb);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.NavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        ImageView current_image = headerLayout.findViewById(R.id.profile_image);
        TextView current_name = headerLayout.findViewById(R.id.current_name);
        TextView current_email = headerLayout.findViewById(R.id.current_id);
        TextView current_number = headerLayout.findViewById(R.id.current_number);
        current_name.setText(user_name);
        current_email.setText(user_id);
        current_number.setText(user_number);
        if(user_profile != null){
            try {

                Glide.with(this)
                        .load( user_profile ).dontAnimate() // Url of the picture
                        .into(current_image);

            } catch (Exception e) {}
        }

        //--- Edit Profile Listner ----//
        Fragment tab2 = TabFragment2.newInstance();
        ((TabFragment2) tab2).setOnItemClickListener(new TabFragment2.OnItemClickListener() {
            @Override
            public void onItemClick(String url, int request_code) {
                ImageView current_image = headerLayout.findViewById(R.id.profile_image);
                Glide.with(mContext)
                        .load(url).dontAnimate() // Url of the picture
                        .into(current_image);
            }
        });


        //---logout manager-----//
        LoginButton f_logoutButton = findViewById(R.id.facebook_log_button);
        Button o_logoutButton = findViewById(R.id.origin_log_button);
        if(isFacebook)
        {
            o_logoutButton.setVisibility(View.GONE);
            f_logoutButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    LoginManager.getInstance().logOut();

                    SharedPreferences.Editor editor = sf.edit();
                    SharedPreferences.Editor add_editor = add_sf.edit();
                    editor.clear();
                    editor.commit();
                    isLoggedIn = false;

                    add_editor.clear();
                    add_editor.commit();

                    if(db.delete_all_contact()){
                        Log.d("drop_table", "dropdrop");
                    }else{
                        Log.d("drop_table", "drop_실패!");
                    }


                    Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(logoutIntent);
                    return;
                }
            });
        }else{
            f_logoutButton.setVisibility(View.GONE);
            o_logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sf.edit();
                    SharedPreferences.Editor add_editor = add_sf.edit();
                    editor.clear();
                    editor.commit();
                    isLoggedIn = false;

                    add_editor.clear();
                    add_editor.commit();

                    if(db.delete_all_contact()){
                        Log.d("drop_table", "dropdrop");
                    }else{
                        Log.d("drop_table", "drop_실패!");
                    }

                    Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(logoutIntent);
                    return;

                }
            });

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.ContactItem){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer);
            if( drawer.isDrawerOpen(Gravity.LEFT)){
                drawer.closeDrawer(Gravity.LEFT);
            }
            sectionsPagerAdapter.getItem(0);

        }else if(id == R.id.GalleryItem){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer);
            if( drawer.isDrawerOpen(Gravity.LEFT)){
                drawer.closeDrawer(Gravity.LEFT);
            }
            sectionsPagerAdapter.getItem(1);
           // trans.replace(R.id.tabfragment1, tabFragment2);

        }else if(id == R.id.SearchItem) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer);
            if (drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            }
            sectionsPagerAdapter.getItem(2);
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search :
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up_info,R.anim.no_change);
                return true ;
            case R.id.Heart :
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer);
                if( !drawer.isDrawerOpen(Gravity.LEFT)){
                    drawer.openDrawer(Gravity.LEFT);
                }
                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();


        finishAffinity();
        super.onBackPressed();
    }


    /*------------------------------------*/

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