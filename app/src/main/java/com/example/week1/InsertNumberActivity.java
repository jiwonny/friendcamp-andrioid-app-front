package com.example.week1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.User;

public class InsertNumberActivity extends AppCompatActivity{

    APIClient apiClient;
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

        Intent userIntent = getIntent();
        String user_name = userIntent.getStringExtra("user_name");
        String user_email = userIntent.getStringExtra("user_email");

        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(user_name);

        TextView tvEmail = findViewById(R.id.tv_email);
        tvEmail.setText(user_email);

        EditText etNumber = findViewById(R.id.tv_number);

        Button saveBtn = findViewById(R.id.user_save);



        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(etNumber.getText().toString().length() != 0) {
                    Log.d("savebtn", "savebtn click");
                    User user = new User();
                    user.setName(user_name);
                    user.setLogin_id(user_email);
                    String user_phNumber = etNumber.getText().toString().replace("-", "");

                    user.setNumber(user_phNumber);

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
