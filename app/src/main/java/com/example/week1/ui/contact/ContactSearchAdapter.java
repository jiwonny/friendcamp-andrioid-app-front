package com.example.week1.ui.contact;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.week1.persistence.ContactDBAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

public class ContactSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<User> mDataset;
    APIClient apiClient;
    IPInfo ipInfo = new IPInfo();
    String address = ipInfo.IPAddress;
    Boolean check = true;
    User addUser = new User();
    User currentUser;
    ContactDBAdapter db;
    SharedPreferences sf;
    Gson gson = new GsonBuilder().create();

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
        apiClient = APIClient.getInstance(parent.getContext(), address,4500).createBaseApi();
        db = new ContactDBAdapter(parent.getContext());
        sf = parent.getContext().getSharedPreferences("userFile", MODE_PRIVATE);

        return vh;
    }

    //Bind View Holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = mDataset.get(position); // 추가할 user 객체


        ContactSearchViewHolder ViewHolder = (ContactSearchViewHolder) holder;
        ViewHolder.name_search.setText(user.getName());
        ViewHolder.number_search.setText(user.getNumber());


        //###### 현재 로그인한 user 정보 불러오기.########
        String user_instance = sf.getString("currentUser", "");
        Log.d("user_instance check", user_instance);
        // 변환
        User currentUser = gson.fromJson(user_instance, User.class);
        //currentUser 란 현재 로그인한 user 정보.
        Log.d("gsonUser", currentUser.getNumber());
        ArrayList<User> current_user_friends = new ArrayList<User>();
        if(currentUser.getFriends() == null){
            Log.d("current user info", "-----------------");
        }
        else {
            Log.d("current user info", "--" + currentUser.getName());
            Iterator iterator = currentUser.getFriends().iterator();

            while (iterator.hasNext()) {
                current_user_friends.add((User) iterator.next());
            }
        }
        //#########현재 user 정보 불러오기 끝########

        // 친구추가 버튼을 누름 -> 내부 db 목록에도 넣고. 서버디비에 넣도록.

        ViewHolder.btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences sf = v.getContext().getSharedPreferences("add_user_file", MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();

                Log.d("add_user_btn", "btn pressed");
                editor.putString("add_user_name", user.getName());
                editor.putString("add_user_number", user.getNumber());
//                editor.putBoolean("add_user_check", true);
                editor.commit();

                int checkDuplicate = -1;
                for(User friend : current_user_friends){
                    if(friend.getName().equals(user.getName()) && friend.getNumber().equals(user.getNumber())){
                        checkDuplicate ++;
                        break;
                    }else continue;
                }

                if(checkDuplicate != -1){
                    Toast.makeText(v.getContext(), "이미 추가된 친구입니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        new AsyncTask<Void, Void, Boolean>(){
                            @Override
                            protected Boolean doInBackground(Void... params){
                                apiClient.getUserfrom_Name_Number(user.getName(), user.getNumber(), new APICallback() {
                                    @Override
                                    public void onError(Throwable t) { }

                                    @Override
                                    public void onSuccess(int code, Object receivedData) {
                                        // 추가할 user 에 대한 user 객체
                                        check = true;
                                        addUser = (User) receivedData;
                                        Log.d("who are you", "addUser"+addUser.getName());
                                        current_user_friends.add(addUser);

                                    }

                                    @Override
                                    public void onFailure(int code) { check = false;}
                                });

                                return check;
                            }

                            @Override
                            protected void onPostExecute(Boolean s){
                                super.onPostExecute(s);
                                if(check){
                                    //db 에 연락처를 추가
                                    db.insert_contact(addUser.getLogin_id(), addUser.getName(), addUser.getNumber());
                                    currentUser.setFriends(current_user_friends);

                                    Toast.makeText(v.getContext(), addUser.getName()+"님이 친구로 추가되었습니다.", Toast.LENGTH_SHORT).show();

                                    ((Activity)v.getContext()).finish();
                                }else{
                                    Toast.makeText(v.getContext(), "친구 추가에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.execute();
                    }catch(Exception e){
                        e.printStackTrace();
                    }



                    // 서버 디비에 친구목록 추가
                    apiClient.update_User(currentUser.getLogin_id(), currentUser, new APICallback() {
                        @Override
                        public void onError(Throwable t) { }

                        @Override
                        public void onSuccess(int code, Object receivedData) {
                            User data = (User) receivedData;
                            Log.d("Success_add", data.getName());
                        }

                        @Override
                        public void onFailure(int code) {
                            Log.e("FRIEND ADD FAIL", String.format("code : %d", code));
                        }
                    });

                }



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
