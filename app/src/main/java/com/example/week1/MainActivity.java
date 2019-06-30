package com.example.week1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.week1.ui.contact.ContactDBHelper;
import com.example.week1.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int PERMISSIONS_REQUEST_CODE = 10;

    ContactDBHelper dbHelper = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Contacts permission request
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_CODE);
        } else{
            inital_setting();
        }

    }

    public void inital_setting(){

        //init_contact_tables();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults){
        View mainLayout = findViewById(R.id.main_layout);
        switch(requestCode){
            case PERMISSIONS_REQUEST_CODE: {
                if (grandResults.length > 0 && grandResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)){

                        Snackbar.make(mainLayout, "Permission to Contacts was denied.\nRestart the App", Snackbar.LENGTH_INDEFINITE).setAction("Confirm", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
                    } else {
                        Snackbar.make(mainLayout, "Permission to Contacts was denied.\nYou should get Permission in Setting",
                                Snackbar.LENGTH_INDEFINITE).setAction("Confirm", new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                finish();
                            }
                        }).show();
                    }
                } else {
                    inital_setting();
                }
            }
        }
    }
/*
    private void init_contact_tables() {
        dbHelper = new ContactDBHelper(this) ;
    }


    private SQLiteDatabase init_database() {

        SQLiteDatabase db = null ;

        File file = new File(getFilesDir(), "Database.db") ;

        System.out.println("PATH : " + file.toString()) ;
        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null) ;
        } catch (SQLiteException e) {
            e.printStackTrace() ;
        }

        if (db == null) {
            System.out.println("DB creation failed. " + file.getAbsolutePath()) ;
        }

        return db ;
    }

*/


}