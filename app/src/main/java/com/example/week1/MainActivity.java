package com.example.week1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.Image;
import com.example.week1.network.User;
import com.example.week1.persistence.ContactDBHelper;
import com.example.week1.ui.gallery.Function;
import com.example.week1.ui.main.SectionsPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSIONS_REQUEST_CODE = 10;
    private static final int PERMISSIONS_REQUEST_CODE_2 = 11;
    APIClient apiClient;

    ContactDBHelper dbHelper = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // PERMISSIONS CHECK

        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Function.hasPermissions(this, PERMISSIONS)){
            ArrayList<String> remainingPermissions = new ArrayList<>();
            for (String permission : PERMISSIONS){
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    remainingPermissions.add(permission);
                }
            }
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        } else {
            inital_setting();
        }

    }

    public void inital_setting(){
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        Toolbar tb = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(tb);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.NavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        apiClient = APIClient.getInstance(this, "143.248.39.49",4500).createBaseApi();

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.TEST: {
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
                break;
            }
            case R.id.Image_TEST: {
                Log.d("test", "clickclickclick");
                apiClient.getImage("http://143.248.39.49:4500/Images", new APICallback() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e("LOG", t.toString());
                    }

                    @Override
                    public void onSuccess(int code, Object receivedData) {
                        Image data = (Image) receivedData;
                        Log.d("data"," aaaaaaaaaaaaaaaaa");
                        String image = data.getImg();
                        Log.d("data",image);
                        Log.d("data",String.format("%d",image.length()));
                        Log.d("data",String.format("%s",image.substring(image.length()-12)));
                        image.replace(" ","+");
                        image.replace("_","/");
                        image.replace("-","+");
                        image.split("=");
                        byte[] decodedString = Base64.decode(image, Base64.URL_SAFE );
                        Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        showImage(bmp);
                    }

                    @Override
                    public void onFailure(int code) {
                        Log.e("erroe", "image load fail");
                    }
                });
            }
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




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grandResults.length > 0) {
                    for (int i = 0; i < grandResults.length; i++) {

                        if (grandResults[i] != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                                Toast.makeText(this, "Permissions were denied.\nRestart the App", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Permissions were denied.\nYou should get Permissions in Setting", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    }
                }
                if(Function.hasPermissions(this, permissions)){
                    inital_setting();
                }
                break;
            }
            case PERMISSIONS_REQUEST_CODE_2: {
                if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //
                } else {
                    Toast.makeText(this, "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

