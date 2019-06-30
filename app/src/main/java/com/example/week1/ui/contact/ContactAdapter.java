package com.example.week1.ui.contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.R;

import java.util.ArrayList;

import static com.example.week1.ui.contact.TabFragment1.REQ_CALL_CONTACT;
import static com.example.week1.ui.contact.TabFragment1.REQ_DELETE_CONTACT;
import static com.example.week1.ui.contact.TabFragment1.REQ_EDIT_CONTACT;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<ContactItem> mDataset;

    // Set Listener
    private static OnItemClickListener mListener = null ;

    public interface OnItemClickListener{
        void onItemClick(View v,int position, int request_code);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView textView1;
        public TextView textView2;
        public ImageView imageView;

        public ContactViewHolder(final View itemView){
            super(itemView);



            textView1 = itemView.findViewById(R.id.contact_name);
            textView2 = itemView.findViewById(R.id.contact_number);
            imageView = itemView.findViewById(R.id.contact_image);
            final LinearLayout addView = (LinearLayout) itemView.findViewById(R.id.add_sub);


            itemView.setOnClickListener(new View.OnClickListener() {

                Boolean onclick =false;
                Contactitem_sub add_layout = new Contactitem_sub(itemView.getContext());

                @Override
                public void onClick(View view) {
                    if (!onclick) {
                        addView.addView(add_layout);
                        onclick = true;

                        // call button

                        Button call_contact = (Button) add_layout.findViewById(R.id.call_contact);
                        call_contact.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int pos = getAdapterPosition() ;
                                if(mListener != null){
                                    mListener.onItemClick(view, pos, REQ_CALL_CONTACT);
                                }
                            }
                        });


                        // edit button
                        Button edit_contact = (Button) add_layout.findViewById(R.id.edit_contact);
                        edit_contact.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int pos = getAdapterPosition() ;
                                if(mListener != null){
                                    mListener.onItemClick(view, pos, REQ_EDIT_CONTACT);
                                }
                            }
                        });

                        // delete button
                        Button delete_contact = (Button) add_layout.findViewById(R.id.delete_contact);
                        delete_contact.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int pos = getAdapterPosition() ;
                                if(mListener != null){
                                    mListener.onItemClick(view, pos, REQ_DELETE_CONTACT);
                                    onclick= false;
                                }
                            }
                        });

                    } else{
                        addView.removeView(add_layout);
                        onclick = false;
                    }
                }
            });
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
