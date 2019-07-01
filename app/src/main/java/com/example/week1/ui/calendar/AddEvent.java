package com.example.week1.ui.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.week1.R;

public class AddEvent extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        Button buttonAdd = (Button) findViewById(R.id.addbutton);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                int pos = intent.getIntExtra("position",-1);

                Intent intent_s = new Intent();
                intent_s.putExtra("position", pos);

                // Date edit and deliver in text
                EditText editTextDate = (EditText) findViewById(R.id.date_edit);
                intent_s.putExtra("event_date", editTextDate.getText().toString());

                // Time edit and deliver in text
                EditText editTextTime = (EditText) findViewById(R.id.time_edit);
                intent_s.putExtra("event_time", editTextTime.getText().toString());

                // Location edit and deliver in text
                EditText editTextLocation = (EditText) findViewById(R.id.location_edit);
                intent_s.putExtra("event_location", editTextLocation.getText().toString());

                // Event edit and deliver in text
                EditText editTextEvent = (EditText) findViewById(R.id.event_edit);
                intent_s.putExtra("event_event", editTextEvent.getText().toString());

                setResult(RESULT_OK, intent_s);
                finish();
            }
        });
    }
}
