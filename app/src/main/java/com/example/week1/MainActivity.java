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
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.Image_f;
import com.example.week1.network.User;
import com.example.week1.persistence.ContactDBAdapter;
import com.example.week1.persistence.ContactDBHelper;
import com.example.week1.ui.gallery.Function;
import com.example.week1.ui.main.SectionsPagerAdapter;
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
    View headerLayout;
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    //update_profileimage update_profileimage;

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        SharedPreferences sf = getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = sf.getString("currentUser_email", "");
        user_name = sf.getString("currentUser_name", "");
        user_number = sf.getString("currentUser_number", "");
        user_profile = sf.getString("currentUser_profile","");

        Log.d("MainActivity", String.format("id: %s , name: %s , number %s, profile, %s", user_id,user_name,user_number,user_profile));

        IPInfo ip = new IPInfo();
        String address = ip.IPAddress;
        apiClient = APIClient.getInstance(this, address,4500).createBaseApi();
        ContactDBAdapter db = new ContactDBAdapter(this);


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
                        .load( user_profile ) // Url of the picture
                        .into(current_image);

            } catch (Exception e) {}
        }

        Button edit_button = headerLayout.findViewById(R.id.profile_edit);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQ_PICK_IMAGE);
            }
        });


        //---logout manager-----//
        LoginButton logoutButton = findViewById(R.id.facebook_log_button);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LoginManager.getInstance().logOut();

                SharedPreferences.Editor editor = sf.edit();
                editor.clear();
                editor.commit();

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

    AlertDialog dlg;
    public void showImage(Bitmap bmp){
        ImageView img = new ImageView(this);
        img.setImageBitmap(bmp);
        dlg = new AlertDialog.Builder(this)
                .setView(img)
                .setTitle("image test")
                .setCancelable(true)
                .show();
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


    /*------------------------------------*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQ_PICK_IMAGE: {
                if (resultCode == RESULT_OK) {
                    Uri photoUri = intent.getData();
                    Cursor cursor = null;
                    try {
                        String[] proj = {MediaStore.Images.Media.DATA};
                        assert photoUri != null;
                        cursor = getContentResolver().query(photoUri, proj, null, null, null);
                        assert cursor != null;
                        cursor.moveToNext();
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        cursor.close();
                        Log.d("path", path);

                        uploadImageToServer(path, user_id);

                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                break;
            }
        }
    }

    public void uploadImageToServer(String filePath, String login_id){
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("Gallery", file.getName(), fileReqBody);
        Log.d("filename", file.getName());

        //TODO : PUT SERVER URL
        String url =  String.format("http://%s:%d/%s", "143.248.39.49",4500,  user_id+'_'+file.getName());

        apiClient.uploadImage(part, login_id, new APICallback() {
            @Override
            public void onError(Throwable t) {
                Log.e("LOG", t.toString());
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                Log.d("SUCCESS", String.format("code : %d", code));

                apiClient.update_UserProfile(user_id, url, new APICallback() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e("LOG", t.toString());
                    }

                    @Override
                    public void onSuccess(int code, Object receivedData) {
                        User data = (User) receivedData;
                        ImageView current_image = headerLayout.findViewById(R.id.profile_image);
                        Log.d("urlrul", url);
                        Glide.with(mContext)
                                .load(url) // Url of the picture
                                .into(current_image);
                        SharedPreferences sf = getSharedPreferences("userFile", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sf.edit();
                        editor.remove("currentUser_profile");
                        editor.putString("currentUser_profile", url);
                        editor.commit();
                    }

                    @Override
                    public void onFailure(int code) {
                        Log.e("FAIL", String.format("code : %d", code));
                    }
                });

            }
            @Override
            public void onFailure(int code) {
                Log.e("FAIL", String.format("code : %d", code));
            }
        });
    }




}