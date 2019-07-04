package com.example.week1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.User;
import com.example.week1.ui.contact.ContactSearchAdapter;
import com.example.week1.ui.gallery.Function;
import com.example.week1.ui.main.SectionsPagerAdapter;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final int PERMISSIONS_REQUEST_CODE = 10;
    private static final int PERMISSIONS_REQUEST_CODE_2 = 11;

    CallbackManager callbackManager;
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
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn){
            Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(loginIntent);
            return;
        }

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        GraphRequest request;
                        request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject user, GraphResponse response) {
                                if (response.getError() != null) {

                                } else {
                                    Log.i("TAG", "user: " + user.toString());
                                    Log.i("TAG", "AccessToken: " + loginResult.getAccessToken().getToken());
                                    setResult(RESULT_OK);

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code

                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });



        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
