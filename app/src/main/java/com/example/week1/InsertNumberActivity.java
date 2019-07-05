package com.example.week1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InsertNumberActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertnum);

        Intent userIntent = getIntent();
        String user_name = userIntent.getStringExtra("user_name");
        String user_email = userIntent.getStringExtra("user_email");

        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(user_name);

        TextView tvEmail = findViewById(R.id.tv_email);
        tvEmail.setText(user_email);

        EditText etNumber = findViewById(R.id.tv_number);

    }
}
