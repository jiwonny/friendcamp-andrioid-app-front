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
import com.example.week1.R;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.User;

import java.util.ArrayList;

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

    ArrayList<String> friends_ID;


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







        return root;
    }

    //TODO : Request all friend list
    public class load_All_Friends extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //Retreive All Friends
        protected String doInBackground(String... args) {
            String xml = "";
            apiClient.getUserfrom_Name_LoginId(user_name, user_id, new APICallback() {
                @Override
                public void onError(Throwable t) { }

                @Override
                public void onSuccess(int code, Object receivedData) {
                    User data = (User) receivedData;
                    ArrayList<User> friends = data.getFriends();
                    for( User friend : friends){
                        friends_ID.add( friend.getLogin_id() );
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
        }
    }

    public class load_All_Posts{}
}