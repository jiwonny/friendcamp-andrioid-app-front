package com.example.week1.ui.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.week1.R;

public class Edit_Contact extends AppCompatActivity {
    Intent intent;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__contact);

        // get intent from tabfragment1
        intent = getIntent();

        String User_Name = intent.getStringExtra("name");
        String User_phNumber = intent.getStringExtra("number");
        //TODO : Input Photo

        // Input NAME
        final EditText editTextName = (EditText) findViewById(R.id.name_edit);
        editTextName.setText(User_Name, TextView.BufferType.EDITABLE);
        editTextName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editTextName.setSelection(0,editTextName.length());
            }
        });

        //Input NUMBER
        final EditText editTextNumber = (EditText) findViewById(R.id.number_edit);
        editTextNumber.setText(User_phNumber, TextView.BufferType.EDITABLE);
        editTextNumber.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editTextNumber.setSelection(0,editTextNumber.length());
            }
        });

        // SAVE BUTTON
        Button buttonSave = (Button) findViewById(R.id.buttonsave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = intent.getIntExtra("position",-1);

                Intent intent_r = new Intent();
                intent_r.putExtra("position", pos);

                // Phot0 edit and deliver in ??
                //TODO : edit photo in contact (get from Gallery??)

                // Name edit and deliver in text
                intent_r.putExtra("contact_name", editTextName.getText().toString());

                // Number edit and deliver in text
                intent_r.putExtra("contact_number", editTextNumber.getText().toString());

                setResult(RESULT_OK, intent_r);
                finish();
            }
        });
    }
}
