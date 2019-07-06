package com.example.week1.ui.contact;


import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.R;
import com.example.week1.network.User;

import java.util.ArrayList;

public class ContactSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<User> mDataset;


    public static class ContactSearchViewHolder extends RecyclerView.ViewHolder{
        TextView number_search;
        TextView name_search;
        ImageView image_search;


        public ContactSearchViewHolder(View view){
            super(view);

            number_search = view.findViewById(R.id.contact_number_search);
            name_search =  view.findViewById(R.id.contact_name_search);
            image_search = view.findViewById(R.id.contact_image_search);
        }

    }

    // Set Dataset
    public ContactSearchAdapter(ArrayList<User> list){
        mDataset= list;
    }


    @Override
    public ContactSearchAdapter.ContactSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_search, parent, false);
        ContactSearchViewHolder vh = new ContactSearchViewHolder(v);

        return vh;
    }

    //Bind View Holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = mDataset.get(position);

        ContactSearchViewHolder ViewHolder = (ContactSearchViewHolder) holder;
        ViewHolder.name_search.setText(user.getName());
        ViewHolder.number_search.setText(user.getNumber());


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
