package com.example.week1.ui.contact;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.R;
import com.example.week1.SearchActivity;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.User;

import java.util.ArrayList;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

public class ContactSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<User> mDataset;


    public static class ContactSearchViewHolder extends RecyclerView.ViewHolder{
        TextView number_search;
        TextView name_search;
        ImageView image_search;
        Button btn_add;


        public ContactSearchViewHolder(View view){
            super(view);

            number_search = view.findViewById(R.id.contact_number_search);
            name_search =  view.findViewById(R.id.contact_name_search);
            image_search = view.findViewById(R.id.contact_image_search);
            btn_add = view.findViewById(R.id.btn_friend_add);


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
        User user = mDataset.get(position); // 추가할 user 객체


        ContactSearchViewHolder ViewHolder = (ContactSearchViewHolder) holder;
        ViewHolder.name_search.setText(user.getName());
        ViewHolder.number_search.setText(user.getNumber());


        // 친구추가 버튼을 누름 -> 내부 db 목록에도 넣고. 서버디비에 넣도록.

        ViewHolder.btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences sf = v.getContext().getSharedPreferences("add_user_file", MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();

                editor.putString("add_user_name", user.getName());
                editor.putString("add_user_number", user.getNumber());
                editor.commit();
//                Intent searchIntent = new Intent(v.getContext(), SearchActivity.class);
//                searchIntent.putExtra("add_user_name", user.getName());
//                searchIntent.putExtra("add_user_number", user.getNumber());
//                v.getContext().startActivity(searchIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
