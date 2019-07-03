package com.example.week1.ui.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.week1.R;

public class Contactitem_sub extends LinearLayout {

    public Contactitem_sub(Context context){
        super(context);
        init(context);
    }

    // Generate 3 Buttons View(Call, Edit, Delete)
    private View init(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.contact_item_sub, this, true);
        return root;
    }
}
