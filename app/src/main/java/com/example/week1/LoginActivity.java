package com.example.week1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
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
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener{
    private static final int PERMISSIONS_REQUEST_CODE = 10;
    private static final int PERMISSIONS_REQUEST_CODE_2 = 11;
    APIClient apiClient;

    CallbackManager callbackManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiClient = APIClient.getInstance(this, "143.248.38.203",4500).createBaseApi();
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
    public Boolean check;
    public void inital_setting(){
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        //  to handle login responses by calling CallbackManager.Factory.create.
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("userFile",MODE_PRIVATE);


        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sf.edit();




        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(!isLoggedIn){
            loginButton.registerCallback(callbackManager,
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
                                    //-----email이랑 이름 받아오는 부분--------
                                    Log.i("TAG", "user: " + user.toString());
                                    setResult(RESULT_OK);

                                    try{
                                        String Login_Id = user.getString("email");
                                        String Name = user.getString("name");

                                        new AsyncTask<Void, Void, Boolean>() {
                                            @Override
                                            protected Boolean doInBackground(Void... params) {
                                                apiClient.getUserfrom_Name_LoginId(Name, Login_Id, new APICallback() {

                                                    @Override
                                                    public void onError(Throwable t) { }
                                                    @Override
                                                    public void onSuccess(int code, Object receivedData) {
                                                        User data = (User) receivedData;
                                                        Log.i("check", "ssssssssssssss");
                                                        Log.i("user_name", data.getName());
                                                        check = true;
                                                    }
                                                    @Override
                                                    public void onFailure(int code) {
                                                        Log.e("FAIL", String.format("code : %d", code));
                                                        check = false;
                                                    }
                                                });
                                                return check;
                                            }
                                            @Override
                                            protected void onPostExecute(Boolean s) {
                                                super.onPostExecute(s);
                                                if (check){
                                                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                                    editor.putString("currentUser_email",Login_Id); // key, value를 이용하여 저장하는 형태
                                                    editor.putString("currentUser_name", Name); // key, value를 이용하여 저장하는 형태
                                                    editor.commit();
                                                    startActivity(mainIntent);

                                                    finish();
                                                } else {
                                                    Intent insertIntent = new Intent(getApplicationContext(), InsertNumberActivity.class);
                                                    insertIntent.putExtra("user_email", Login_Id);
                                                    insertIntent.putExtra("user_name", Name);
                                                    editor.putString("currentUser_email",Login_Id); // key, value를 이용하여 저장하는 형태
                                                    editor.putString("currentUser_name", Name); // key, value를 이용하여 저장하는 형태
                                                    editor.commit();
                                                    startActivity(insertIntent);
                                                    finish();
                                                }
                                            }
                                        }.execute();



                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
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
//                        // App code
//                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(i);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        }
        else{
            Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(loginIntent);
            finish();
        }





        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button://페이스북 로그인 버튼
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email","user_friends"));
                break;//페이스북 로그인 버튼
        }


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
