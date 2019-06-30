package com.example.week1.ui.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.week1.R;

public class Edit_Contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__contact);

        Button buttonSave = (Button) findViewById(R.id.buttonsave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                // Phot0 edit and deliver in ??
                //TODO : edit photo in contact (get from Gallery??)

                // Name edit and deliver in text
                EditText editTextName = (EditText) findViewById(R.id.name_edit);
                intent.putExtra("contact_name", editTextName.getText().toString());

                // Number edit and deliver in text
                EditText editTextNumber = (EditText) findViewById(R.id.number_edit);
                intent.putExtra("contact_number", editTextNumber.getText().toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
