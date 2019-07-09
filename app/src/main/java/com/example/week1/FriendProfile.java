package com.example.week1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.IPInfo;
import com.example.week1.network.Image_f;
import com.example.week1.persistence.GalleryDBAdapter;
import com.example.week1.ui.gallery.CameraAction;
import com.example.week1.ui.gallery.Function;
import com.example.week1.ui.gallery.GalleryPreview;
import com.example.week1.ui.gallery.TabFragment2;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendProfile extends AppCompatActivity {

    String user_id;
    String user_name;
    String user_number;
    String user_profile;
    GridView galleryGridView;
    LoadAlbum loadAlbumTask;
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    CameraAction cameraAction;
    AlbumAdapter adapter;
    APIClient apiClient;
    Activity activity  =this;
    IPInfo ipInfo = new IPInfo();
    String address = ipInfo.IPAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

//        SharedPreferences sf = getSharedPreferences("userFile", MODE_PRIVATE);
////        user_id = sf.getString("currentUser_email", "");

        Intent show_profile = getIntent();
        user_id = show_profile.getStringExtra("showUser_LoginId");
        user_name = show_profile.getStringExtra("showUser_Name");
        user_number = show_profile.getStringExtra("showUser_Number");
        user_profile = show_profile.getStringExtra("showUser_Profile");

        apiClient = APIClient.getInstance(this, address,4500).createBaseApi();


        TextView profile_name = findViewById(R.id.Profile_name);
        TextView profile_id = findViewById(R.id.Profile_id);
        TextView profile_number = findViewById(R.id.Profile_number);
        profile_name.setText(user_name);
        profile_id.setText(user_id);
        profile_number.setText(user_number);

        ImageView profile_image = findViewById(R.id.Profile_image);
        if ( user_profile != null){
            Glide.with(this).load(user_profile).dontAnimate().into(profile_image);
        }

        galleryGridView = (GridView) findViewById(R.id.galleryGridView);

        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360)
        {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        loadAlbumTask = new LoadAlbum();
        loadAlbumTask.execute();

    }

    // Load Photos from DB and set adapter
    class LoadAlbum extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        //Generate image data
        protected String doInBackground(String... args) {
            String xml = "";
            Log.d("User_id", user_id + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            apiClient.getImageList(user_id, new APICallback() {
                @Override
                public void onError(Throwable t) { }

                @Override
                public void onSuccess(int code, Object receivedData) {
                    List<Image_f> data = (List<Image_f>) receivedData;
                    Log.d("Load", "Start-------------------------------------------"+data);
                    for( Image_f image_f : data){

                        String login_id = image_f.getLogin_id();
                        String url = String.format("http://%s:%d/%s", address ,4500, image_f.getUrl());
                        String file = image_f.getUrl();
                        String timestamp = image_f.getTimestamp();

                        Log.d("Load", String.format("id : %s, url : %s , timestamp : %s", login_id,url, timestamp));

                        albumList.add(Function.mappingInbox(login_id,url,file,timestamp));
                    }
                }
                @Override
                public void onFailure(int code) {
                    Log.e("FAIL", String.format("code : %d", code));
                }
            });

            return xml;
        }

        // Set Adapter
        @Override
        protected void onPostExecute(String xml) {
            adapter = new AlbumAdapter(activity, albumList);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {

                    //TODO : send back result

                }
            });
        }
    }

    class AlbumAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<HashMap< String, String >> data;
        public AlbumAdapter(Activity a, ArrayList < HashMap < String, String >> d) {
            activity = a;
            data = d;
        }
        public int getCount() {
            return data.size();
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            AlbumViewHolder holder = null;
            if (convertView == null) {
                holder = new AlbumViewHolder();
                convertView = LayoutInflater.from(activity).inflate(
                        R.layout.album_row, parent, false);

                holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);

                convertView.setTag(holder);
            } else {
                holder = (AlbumViewHolder) convertView.getTag();
            }

            holder.galleryImage.setId(position);

            HashMap < String, String > song = new HashMap < String, String > ();
            song = data.get(position);
            try {

                Glide.with(activity)
                        .load( song.get("url") ) // Url of the picture
                        .into(holder.galleryImage);

            } catch (Exception e) {}
            return convertView;
        }

        public void onActivityResult(int requestCode, int resultCode) {
            this.notifyDataSetChanged();
        }

    }

    class AlbumViewHolder {
        ImageView galleryImage;
    }


}
