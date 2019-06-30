package com.example.week1.ui.contact;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.R;

import java.util.ArrayList;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<ContactItem> mDataset;
    private Context context;

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView textView1;
        public TextView textView2;
        public ImageView imageView;

        public ContactViewHolder(View itemView){
            super(itemView);

            textView1 = itemView.findViewById(R.id.contact_name);
            textView2 = itemView.findViewById(R.id.contact_number);
            imageView = itemView.findViewById(R.id.contact_image);
        }
    }

    public ContactAdapter(ArrayList<ContactItem> list){
        mDataset= list;
    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.contact_item, parent, false);
        ContactViewHolder vh = new ContactViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position){
        ContactItem contactItem = mDataset.get(position);
        holder.textView1.setText(contactItem.getUser_Name());
        holder.textView2.setText(contactItem.getUser_phNumber());
        /*
        if (contactItem.getUser_photo() != null) {
            holder.imageView.setImageBitmap(contactItem.getUser_photo());
        } else{
            holder.imageView.setImageResource(R.drawable.icons_user);
        }*/
        holder.imageView.setImageBitmap(contactItem.getUser_photo());

    }
    @Override
    public int getItemCount(){
        return mDataset.size();
    }

    public void onActivityResult(int requestCode, int resultCode) {
        this.notifyDataSetChanged();
    }

}
