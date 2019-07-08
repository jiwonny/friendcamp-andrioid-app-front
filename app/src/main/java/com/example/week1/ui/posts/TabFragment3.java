package com.example.week1.ui.posts;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.R;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.Image_f;
import com.example.week1.network.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class TabFragment3 extends Fragment {

    Context mContext;

    APIClient apiClient;

    String user_name;
    String user_id;
    String user_number;
    String user_profile;
    IPInfo ip = new IPInfo();
    String address = ip.IPAddress;
    int port = ip.Port;

    static final String KEY_LOGIN_ID = "login_id";
    static final String KEY_PROFILE = "profile";

    RecyclerView recyclerView;
    PostAdapter adapter;

    ArrayList<HashMap<String,String>> friends_ID_Profile = new ArrayList<>();
    ArrayList<PostItem> Posts = new ArrayList<PostItem>();


    public TabFragment3() {  }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this.getActivity();
        apiClient = APIClient.getInstance(getActivity(), address,port).createBaseApi();

        SharedPreferences sf = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = sf.getString("currentUser_email", "");
        user_name = sf.getString("currentUser_name", "");
        user_number = sf.getString("currentUser_number", "");
        user_profile = sf.getString("currentUser_profile","");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.tabfragment3, container, false);

        recyclerView = root.findViewById(R.id.post_recycler);

        load_All_Friends load_all_friends = new load_All_Friends();
        load_all_friends.execute();

        return root;
    }

    public class load_All_Friends extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //Retreive All Friends
        protected String doInBackground(String... args) {
            String xml = "";

            // Put My Posts
            HashMap<String, String> map = new HashMap<String,String>();
            map.put(KEY_LOGIN_ID, user_id);
            map.put(KEY_PROFILE, user_profile);
            friends_ID_Profile.add(map);

            apiClient.getUserfrom_Name_LoginId(user_name, user_id, new APICallback() {
                @Override
                public void onError(Throwable t) { }

                @Override
                public void onSuccess(int code, Object receivedData) {
                    User data = (User) receivedData;
                    ArrayList<User> friends = data.getFriends();
                    if(friends != null){
                        for( User friend : friends){
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(KEY_LOGIN_ID, friend.getLogin_id());
                            map.put(KEY_PROFILE, friend.getProfile_image_id());
                            friends_ID_Profile.add(map);
                        }
                    }

                }
                @Override
                public void onFailure(int code) {
                    Log.d("T3error", " failed to retreive friends ");
                }
            });
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {
            super.onPostExecute(xml);

            load_All_Posts load_all_posts = new load_All_Posts();
            load_all_posts.execute();
        }
    }

    public class load_All_Posts extends AsyncTask<HashMap<String,String>, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        // load all Posts from Friends
        protected String doInBackground(HashMap<String,String>... args) {
            for (HashMap<String,String> friend : args) {
                apiClient.getImageList(friend.get(KEY_LOGIN_ID), new APICallback() {
                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onSuccess(int code, Object receivedData) {
                        List<Image_f> data = (List<Image_f>) receivedData;
                        if (data.size() > 0) {
                            for( Image_f image : data){
                                PostItem post = new PostItem();
                                post.setLogin_id(friend.get(KEY_LOGIN_ID));
                                post.setProfile(friend.get(KEY_PROFILE));
                                post.setPost(image);

                                Posts.add(post);
                            }
                        }
                    }
                    @Override
                    public void onFailure(int code) {
                        Log.e("Tab3err", "failed to load image_list");
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            adapter = new PostAdapter(Posts);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

        }
    }

}