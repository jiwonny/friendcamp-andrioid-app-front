package com.example.week1.ui.posts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.week1.R;
import com.example.week1.network.IPInfo;
import com.example.week1.network.Image_f;


import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private static ArrayList<PostItem> mDataset;
    Context mcontext;
    static Activity activity;

    IPInfo ip = new IPInfo();
    String address = ip.IPAddress;
    int port = ip.Port;

    // Set Dataset
    public PostAdapter(Activity a, ArrayList<PostItem> list){
        activity = a;
        mDataset = list;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        public ImageView Post_image;
        public ImageView Post_profile;
        public TextView Post_id;
        public TextView Post_name;
        public TextView Post_date;
        public CardView Comments;

        public PostViewHolder(final View itemView){
            super(itemView);



            Post_image = itemView.findViewById(R.id.Post_image);
            Post_profile = itemView.findViewById(R.id.Post_profile);
            Post_id = itemView.findViewById(R.id.Post_id);
            Post_name = itemView.findViewById(R.id.Post_name);
            Post_date = itemView.findViewById(R.id.Post_date);

            Comments = itemView.findViewById(R.id.Post_comments);
            Comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition() ;
                    PostItem postitem = mDataset.get(pos);
                    Image_f image_f = postitem.getPost();

                    Intent intent = new Intent(activity, Comments.class);
                    intent.putExtra("Login_id", image_f.getLogin_id());
                    intent.putExtra("url", image_f.getUrl());
                    intent.putExtra("profile", postitem.getProfile());
                    activity.startActivity(intent);
                }
            });
        }
    }



    // Create View Holder
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        mcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.post_item, parent, false);
        PostAdapter.PostViewHolder vh = new PostAdapter.PostViewHolder(view);
        return vh;
    }

    // Bind View Holder
    @Override
    public void onBindViewHolder(PostAdapter.PostViewHolder holder, int position){
        PostItem post = mDataset.get(position);
        Image_f image = post.getPost();

        String url =  String.format("http://%s:%d/%s", address ,port, image.getUrl());
        String profile = post.getProfile();

        holder.Post_id.setText(image.getLogin_id());
        holder.Post_name.setText(image.getName());
        holder.Post_date.setText(image.getTimestamp());

        Glide.with(mcontext)
                .load( profile ).dontAnimate() // Url of the picture
                .into(holder.Post_profile);

        Glide.with(mcontext)
                .load(url).into(holder.Post_image);

    }

    // Get Item Count
    @Override
    public int getItemCount(){ return mDataset.size();}

    public void onActivityResult(int requestCode, int resultCode) {
        this.notifyDataSetChanged();
    }

}
