package com.example.week1.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.week1.MainActivity;
import com.example.week1.R;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.User;
import com.example.week1.persistence.ContactDBAdapter;
import com.example.week1.ui.gallery.Function;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener{
    private static final int PERMISSIONS_REQUEST_CODE = 10;
    private static final int PERMISSIONS_REQUEST_CODE_2 = 11;
    APIClient apiClient;

    CallbackManager callbackManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContactDBAdapter db = new ContactDBAdapter(this);

        IPInfo ip = new IPInfo();
        String address = ip.IPAddress;

        apiClient = APIClient.getInstance(this, address,4500).createBaseApi();
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

    public Boolean check = true;
    public String c_login_Id;
    public String c_name;
    public String c_phNumber;
    public String c_profile;
    public User data = new User();



    public void inital_setting(){
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);
        Button reg_btn = findViewById(R.id.register_button);
        Button o_login_btn = (Button) findViewById(R.id.login_origin_button);
        SharedPreferences sf = getSharedPreferences("userFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("register", "register");
                Intent regIntent = new Intent(getApplicationContext(), InsertNumberActivity.class);
                startActivity(regIntent);
                editor.putBoolean("Facebook", false);
                editor.commit();
            }
        });

        // 로그인한 User 가 db 에 있는지 확인
        o_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText Login_id_edit = findViewById(R.id.Login_id_edit);
                String submit_loginId = Login_id_edit.getText().toString();

                try{
                    new AsyncTask<Void, Void, Boolean>(){
                          @Override
                          protected Boolean doInBackground(Void... params) {
                              apiClient.getUserfrom_LoginId(submit_loginId, new APICallback() {
                                  @Override
                                  public void onError(Throwable t) {
                                  }

                                  @Override
                                  public void onSuccess(int code, Object receivedData) {
                                      // 만약 존재하는 경우 바로 main activity 로 이동.
                                      data = (User) receivedData;
                                      check = true;
                                  }

                                  @Override
                                  public void onFailure(int code) {
                                      check = false;
                                  }
                              });
                              return check;
                          }

                          @Override
                          protected void onPostExecute(Boolean s){
                             if(check){
                                 Gson currentGson = new GsonBuilder().create();
                                 String userJson = currentGson.toJson(data, User.class);

                                 editor.putString("currentUser", userJson);
                                 editor.putString("currentUser_email", data.getLogin_id());
                                 editor.putString("currentUser_name", data.getName());
                                 editor.putString("currentUser_number", data.getNumber());
                                 editor.putString("currentUser_profile", data.getProfile_image_id());
                                 editor.putBoolean("Facebook", false);
                                 editor.commit();

                                 Toast.makeText(getApplicationContext(), "안녕하세요! " +data.getName() +"님", Toast.LENGTH_SHORT).show();

                                 Intent mainIntent2 = new Intent(getApplicationContext(), MainActivity.class);
                                 startActivity(mainIntent2);
                             }
                             else{
                                 Toast.makeText(getApplicationContext(), "해당 사용자가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                             }
                          }
                      }.execute();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //  to handle login responses by calling CallbackManager.Factory.create.
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);


        loginButton.setReadPermissions("email");

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
                                        c_login_Id = user.getString("email");
                                        c_name = user.getString("name");

                                        new AsyncTask<Void, Void, Boolean>() {
                                            @Override
                                            protected Boolean doInBackground(Void... params) {
                                                apiClient.getUserfrom_Name_LoginId(c_name, c_login_Id, new APICallback() {

                                                    @Override
                                                    public void onError(Throwable t) { }
                                                    @Override
                                                    public void onSuccess(int code, Object receivedData) {
                                                       data = (User) receivedData;
                                                        c_phNumber = data.getNumber();
                                                        c_profile = data.getProfile_image_id();
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
                                                    // 만약 존재하는 경우 바로 main activity 로 이동.
                                                    Gson currentGson = new GsonBuilder().create();
                                                    String userJson = currentGson.toJson(data, User.class);

                                                    editor.putString("currentUser", userJson);
                                                    editor.putString("currentUser_email",c_login_Id);
                                                    editor.putString("currentUser_name", c_name);
                                                    editor.putString("currentUser_number", c_phNumber);
                                                    editor.putString("currentUser_profile", c_profile);
                                                    editor.putBoolean("Facebook", true);
                                                    editor.commit();
                                                    Toast.makeText(getApplicationContext(), "안녕하세요! " +c_name +"님", Toast.LENGTH_SHORT).show();
                                                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(mainIntent);

                                                } else {
                                                    Intent insertIntent = new Intent(getApplicationContext(), InsertNumberActivity.class);
                                                    insertIntent.putExtra("user_email", c_login_Id);
                                                    insertIntent.putExtra("user_name", c_name);
                                                    Gson currentGson = new GsonBuilder().create();
                                                    String userJson = currentGson.toJson(data, User.class);

                                                    editor.putString("currentUser", userJson);

                                                    editor.putString("currentUser_email",c_login_Id); // key, value를 이용하여 저장하는 형태
                                                    editor.putString("currentUser_name", c_name); // key, value를 이용하여 저장하는 형태
                                                    editor.putBoolean("Facebook", true);
                                                    editor.commit();
                                                    startActivity(insertIntent);

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
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button://페이스북 로그인 버튼
                Log.d("facebook", "login with facebook");
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
