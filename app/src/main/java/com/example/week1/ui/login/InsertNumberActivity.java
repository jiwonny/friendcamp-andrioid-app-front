package com.example.week1.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.week1.MainActivity;
import com.example.week1.R;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InsertNumberActivity extends AppCompatActivity{

    APIClient apiClient;
    User currentUser = new User();
    String user_name;
    String user_email;
    Gson gson = new GsonBuilder().create();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertnum);
        IPInfo ip = new IPInfo();
        String address = ip.IPAddress;

        apiClient = APIClient.getInstance(this, address,4500).createBaseApi();

        SharedPreferences sf = getSharedPreferences("userFile",MODE_PRIVATE);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sf.edit();

        // 원래 user 에 대한 gson get
        String user_instance = sf.getString("currentUser", "");
        Boolean login_facebook = sf.getBoolean("Facebook", true);

        EditText tvName = findViewById(R.id.tv_name);
        EditText tvEmail = findViewById(R.id.tv_email);
        TextView tvPw_title = findViewById(R.id.tv_pw_title);
        EditText tvPw = findViewById(R.id.tv_pw);

        // facebook 으로 가입할 시.
        if(login_facebook == true){
            tvName.setEnabled(false);
            tvEmail.setEnabled(false);
            tvPw_title.setVisibility(View.GONE);
            tvPw.setVisibility(View.GONE);
          // 변환
            currentUser = gson.fromJson(user_instance, User.class);
        }else{
            tvName.setEnabled(true);
            tvEmail.setEnabled(true);
        }



        Intent userIntent = getIntent();
        user_name = userIntent.getStringExtra("user_name");
        user_email = userIntent.getStringExtra("user_email");

        tvName.setText(user_name);
        tvEmail.setText(user_email);

        EditText etNumber = findViewById(R.id.tv_number);

        Button saveBtn = findViewById(R.id.user_save);



        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(etNumber.getText().toString().length() != 0) {
                    Log.d("savebtn", "savebtn click");
                    // FACEBOOK 으로 가입하지 않을 경우.
                    if(user_instance.equals("")){
                        user_name = tvName.getText().toString();
                        user_email = tvEmail.getText().toString();
                    }

                    User user = new User();
                    user.setName(user_name);
                    user.setLogin_id(user_email);
                    String user_phNumber = etNumber.getText().toString().replace("-", "");
                    user.setNumber(user_phNumber);

                    // 어차피 처음 가입하는 자는 친구가 없음.
                    currentUser.setName(user_name);
                    currentUser.setLogin_id(user_email);
                    currentUser.setNumber(user_phNumber);



//                    Gson currentGson = new GsonBuilder().create();
                    String userJson = gson.toJson(currentUser, User.class);

                    editor.putString("currentUser", userJson);
                    editor.putString("currentUser_name", user_name);
                    editor.putString("currentUser_email", user_email);

                    editor.putString("currentUser_number", user_phNumber);
                    editor.commit();

                    apiClient.post_User(user, new APICallback() {
                        @Override
                        public void onError(Throwable t) {
                        }

                        @Override
                        public void onSuccess(int code, Object receivedData) {
                            User data = (User) receivedData;
                            Log.d("post_user", data.getName());
                            Toast.makeText(getApplicationContext(), "안녕하세요! " +data.getName() +"님", Toast.LENGTH_SHORT).show();

                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }

                        @Override
                        public void onFailure(int code) {
                            Log.e("FAIL", String.format("code : %d", code));
                        }
                    });
                }
            }
        });

    }
}
