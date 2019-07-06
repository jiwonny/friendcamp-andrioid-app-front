package com.example.week1;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.week1.network.Image_f;
import com.example.week1.network.User;
import com.example.week1.persistence.ContactDBAdapter;
import com.example.week1.persistence.ContactDBHelper;
import com.example.week1.ui.gallery.Function;
import com.example.week1.ui.main.SectionsPagerAdapter;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
//    private static final int PERMISSIONS_REQUEST_CODE = 10;
//    private static final int PERMISSIONS_REQUEST_CODE_2 = 11;
    APIClient apiClient;

    ContactDBHelper dbHelper = null ;

    String user_name;
    String user_id;
    String user_number;
    String user_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sf = getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = sf.getString("currentUser_email", "");
        user_name = sf.getString("currentUser_name", "");
        user_number = sf.getString("currentUser_number", "");
        user_profile = sf.getString("currentuser_profile",null);

        apiClient = APIClient.getInstance(this, "143.248.39.49",4500).createBaseApi();
        ContactDBAdapter db = new ContactDBAdapter(this);


        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        Toolbar tb = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(tb);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.NavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        ImageView current_image = headerLayout.findViewById(R.id.profile_image);
        TextView current_name = headerLayout.findViewById(R.id.current_name);
        TextView current_email = headerLayout.findViewById(R.id.current_id);
        TextView current_number = headerLayout.findViewById(R.id.current_number);
        current_name.setText(user_id);
        current_email.setText(user_name);
        current_number.setText(user_number);
        if(user_profile != null){
            try {
                //TODO : PUT SERVER URL
                String url =  String.format("http://%s:%d/%s", "143.248.39.49",4500, user_profile);
                Glide.with(this)
                        .load( url ) // Url of the picture
                        .into(current_image);

            } catch (Exception e) {}
        }


        //---logout manager-----//
        LoginButton logoutButton = findViewById(R.id.facebook_log_button);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LoginManager.getInstance().logOut();

                SharedPreferences.Editor editor = sf.edit();
                editor.clear();
                editor.commit();

                if(db.drop_contact()){
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

        if (id == R.id.TEST) {
            User user = new User();
            user.setLogin_id("idididid");
            user.setName("namenamename");
            user.setNumber("000-0000-0000");
            Log.d("user", String.format(" user %s %s %s", user.getLogin_id(), user.getName(), user.getNumber()));
            System.out.println(String.format(" user %s %s %s", user.getLogin_id(), user.getName(), user.getNumber()));

            apiClient.post_User(user, new APICallback() {
                @Override
                public void onError(Throwable t) {
                    Log.e("LOG", t.toString());
                }

                @Override
                public void onSuccess(int code, Object receivedData) {
                    User data = (User) receivedData;
                    Log.d("user", String.format(" data %s %s %s", data.getLogin_id(), data.getName(), data.getNumber()));
                }

                @Override
                public void onFailure(int code) {
                    Log.e("FAIL", String.format("code : %d", code));
                }
            });

        }
        return false;
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





}