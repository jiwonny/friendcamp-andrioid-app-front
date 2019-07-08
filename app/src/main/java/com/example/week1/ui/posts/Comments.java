package com.example.week1.ui.posts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.week1.R;
import com.example.week1.network.Comment;
import com.example.week1.network.IPInfo;

import java.util.ArrayList;

public class Comments extends AppCompatActivity {

    IPInfo ip = new IPInfo();
    String address = ip.IPAddress;
    int port = ip.Port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ImageView back_button = findViewById(R.id.comment_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


}

//public class load_comments

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private ArrayList<CommentItem> mDataset;
    Context mcontext;
    static Activity activity;

    IPInfo ip = new IPInfo();
    String address = ip.IPAddress;
    int port = ip.Port;

    // Set Dataset
    public CommentAdapter(Activity a, ArrayList<CommentItem> list){
        activity = a;
        mDataset = list;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        public ImageView Comment_profile;
        public TextView Comment_id;
        public TextView Comment_context;

        public CommentViewHolder(final View itemView){
            super(itemView);

            Comment_profile = itemView.findViewById(R.id.comment_profile);
            Comment_id = itemView.findViewById(R.id.comment_id);
            Comment_context= itemView.findViewById(R.id.comment_context);

        }
    }

    // Create View Holder
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        mcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.comment_item, parent, false);
        CommentAdapter.CommentViewHolder vh = new CommentAdapter.CommentViewHolder(view);
        return vh;
    }

    // Bind View Holder
    @Override
    public void onBindViewHolder(CommentAdapter.CommentViewHolder holder, int position){
        CommentItem commentitem = mDataset.get(position);
        Comment comment = commentitem.getComment();

        holder.Comment_id.setText(comment.getLogin_id());
        holder.Comment_context.setText(comment.getContext());

        if ( commentitem.getProfile() != null){
            Glide.with(mcontext)
                    .load( commentitem.getProfile() ).dontAnimate() // Url of the picture
                    .into(holder.Comment_profile);
        }
    }

    // Get Item Count
    @Override
    public int getItemCount(){ return mDataset.size();}

    public void onActivityResult(int requestCode, int resultCode) {
        this.notifyDataSetChanged();
    }

}





