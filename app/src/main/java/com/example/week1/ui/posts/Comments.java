package com.example.week1.ui.posts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.week1.R;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.Comment;
import com.example.week1.network.IPInfo;
import com.example.week1.network.Image_f;
import com.example.week1.network.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Comments extends AppCompatActivity {

    APIClient apiClient;

    IPInfo ip = new IPInfo();
    String address = ip.IPAddress;
    int port = ip.Port;

    String login_id;
    String Image_login_id;
    String filename;
    String profile;
    RecyclerView recyclerView;
    CommentAdapter adapter;

    Intent intent;

    ArrayList<Comment> new_comments = new ArrayList<Comment>();
    ArrayList<CommentItem> Comments = new ArrayList<CommentItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        apiClient = APIClient.getInstance(this, address,port).createBaseApi();

        intent = getIntent();
        filename = intent.getStringExtra("url");
        Image_login_id = intent.getStringExtra("Login_id");

        SharedPreferences sf = getSharedPreferences("userFile", MODE_PRIVATE);
        login_id =sf.getString("currentUser_email", "");
        profile = sf.getString("currentUser_profile","");

        ImageView back_button = findViewById(R.id.comment_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final EditText editText = (EditText) findViewById(R.id.comment);

        Button confirm = (Button) findViewById(R.id.comment_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String context =  editText.getText().toString();
                Comment c = new Comment();
                c.setLogin_id(login_id);
                c.setContext(context);
                new_comments.add(c);

                CommentItem ci = new CommentItem();
                ci.setProfile(profile);
                ci.setComment(c);

                InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);


                apiClient.update_Comment(Image_login_id, filename, new_comments, new APICallback() {
                    @Override
                    public void onError(Throwable t) {
                        Log.d("commentErr","erooorroororororo");
                    }

                    @Override
                    public void onSuccess(int code, Object receivedData) {
                        Toast.makeText(Comments.this, "Comment is Uploaded", Toast.LENGTH_SHORT).show();
                        editText.getText().clear();
                        Comments.add(ci);
                        adapter.onActivityResult(1,1);
                    }

                    @Override
                    public void onFailure(int code) {
                        Toast.makeText(Comments.this, "Network Failed", Toast.LENGTH_SHORT).show();
                        Log.d("comment fail", "fffffffffffffffffffffffff");
                    }
                });
            }
        });

        recyclerView = findViewById(R.id.comment_recycler);
        adapter = new CommentAdapter(Comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        load_comments load = new load_comments();
        load.execute();

    }

    String c_login_id;
    String c_profile;
    class load_comments extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            new_comments.clear();
            Comments.clear();
        }

        @Override
        // load all Posts from Friends
        protected String doInBackground(String... args) {
            Log.d("commentaaaaaa","ssssssssssssssssssss"+login_id +"sssssssssss"+ filename);
            apiClient.getImage(Image_login_id, filename, new APICallback() {
                @Override
                public void onError(Throwable t) { }

                @Override
                public void onSuccess(int code, Object receivedData) {
                    Image_f image = (Image_f) receivedData;
                    Log.d("commmmntaaaa", receivedData.toString());
                    ArrayList<Comment> com = image.getComments();
                    if (com != null) {
                        for (Comment c : com) {
                            Log.d("commentaaaaaa", c.getContext());
                            new_comments.add(c);
                            c_login_id = c.getLogin_id();

                            apiClient.getUserfrom_LoginId(c_login_id, new APICallback() {
                                @Override
                                public void onError(Throwable t) { }

                                @Override
                                public void onSuccess(int code, Object receivedData) {
                                    User data = (User) receivedData;
                                    c_profile = data.getProfile_image_id();
                                }
                                @Override
                                public void onFailure(int code) {
                                    Log.d("COmmment", " failed to get User ");
                                }
                            });


                            CommentItem ci = new CommentItem();
                            ci.setComment(c);
                            ci.setProfile(c_profile);
                            Comments.add(ci);
                        }
                    }
                }

                @Override
                public void onFailure(int code) {
                    Log.d("COmmment", " failed to get Comments ");
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            adapter.onActivityResult(1,1);
        }
    }
}


class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private ArrayList<CommentItem> mDataset;
    Context mcontext;


    IPInfo ip = new IPInfo();
    String address = ip.IPAddress;
    int port = ip.Port;

    // Set Dataset
    public CommentAdapter( ArrayList<CommentItem> list){
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
    public int getItemCount(){
        return mDataset.size();
    }

    public void onActivityResult(int requestCode, int resultCode) {
        this.notifyDataSetChanged();
    }

}





