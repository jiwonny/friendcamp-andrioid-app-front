package com.example.week1.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.week1.R;
import com.example.week1.ui.main.PageViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TabFragment2 extends Fragment  implements ActivityCompat.OnRequestPermissionsResultCallback {

    private PageViewModel pageViewModel;
    private static final int PERMISSIONS_REQUEST_CODE_2 = 11;
    static final int REQ_TAKE_CAMARA = 1 ;
    View root;
    LoadAlbum loadAlbumTask;
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    CameraAction cameraAction;
    AlbumAdapter adapter;

    public TabFragment2(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 2;
        pageViewModel.setIndex(index);
    }


    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.tabfragment2, container, false);
        galleryGridView = (GridView) root.findViewById(R.id.galleryGridView);

        GalleryDBAdapter db = new GalleryDBAdapter(getActivity());

        loadAlbumTask = new TabFragment2.LoadAlbum();
        loadAlbumTask.execute();

        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getActivity().getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360)
        {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getActivity().getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        FloatingActionButton Start_Camera = root.findViewById(R.id.camera_button);
        Start_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] PERMISSIONS = {Manifest.permission.CAMERA};
                if(!Function.hasPermissions(getActivity(), PERMISSIONS)) {
                    ArrayList<String> remainingPermissions = new ArrayList<>();
                    for (String permission : PERMISSIONS) {
                        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                            remainingPermissions.add(permission);
                        }
                    }
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSIONS_REQUEST_CODE_2);
                } else{
                    CameraActivity();
                }
            }
        });
        return root;
    }

    // Camera Action
    public void CameraActivity(){
        try {
            cameraAction = new CameraAction();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = cameraAction.createImageFile(getActivity());
            Uri uri = FileProvider.getUriForFile(getContext(), "com.example.week1", f);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(intent, REQ_TAKE_CAMARA);
        }catch( IOException e){
            e.printStackTrace();
        }
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

            if(isFirstTime()){
                getPhotos();
            }
            albumList = load_photos();

            return xml;
        }

        // Set Adapter
        @Override
        protected void onPostExecute(String xml) {

            adapter = new AlbumAdapter(getActivity(), albumList);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Intent intent = new Intent(getActivity(), AlbumActivity.class);
                    intent.putExtra("name", albumList.get(+position).get(Function.KEY_ALBUM));
                    startActivity(intent);
                }
            });
        }
    }

    // check firstTime
    private Boolean firstTime2 = null;
    private boolean isFirstTime(){
        if (firstTime2 == null) {
            SharedPreferences mPreferences = getActivity().getSharedPreferences("first_time2", Context.MODE_PRIVATE);
            firstTime2 = mPreferences.getBoolean("firstTime2", true);
            if (firstTime2) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime2", false);
                editor.commit();
            }
        }
        return firstTime2;
    }

    // get Photos from Gallery(very first time)
    public void getPhotos(){
        String path = null;
        String album = null;
        String timestamp = null;
        Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };
        Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, null,
                null, null);
        Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection, null,
                null, null);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});

        while (cursor.moveToNext()) {

            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
            //put in Database
            GalleryDBAdapter db = new GalleryDBAdapter(getActivity());
            db.insert_photo(path,album,timestamp);
        }
    }


    // generate Albumlist for adpater
    private ArrayList<HashMap<String, String>> load_photos(){
        GalleryDBAdapter db = new GalleryDBAdapter(getActivity());
        return db.retreive_photos_byAlbums();
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
                holder.gallery_count = (TextView) convertView.findViewById(R.id.gallery_count);
                holder.gallery_title = (TextView) convertView.findViewById(R.id.gallery_title);

                convertView.setTag(holder);
            } else {
                holder = (AlbumViewHolder) convertView.getTag();
            }
            holder.galleryImage.setId(position);
            holder.gallery_count.setId(position);
            holder.gallery_title.setId(position);

            HashMap < String, String > song = new HashMap < String, String > ();
            song = data.get(position);
            try {
                holder.gallery_title.setText(song.get(Function.KEY_ALBUM));
                holder.gallery_count.setText(song.get(Function.KEY_COUNT));

                Glide.with(activity)
                        .load(new File(song.get(Function.KEY_PATH))) // Uri of the picture
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
        TextView gallery_count, gallery_title;
    }

    // get result from Camera Action
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQ_TAKE_CAMARA: {
                if (resultCode == Activity.RESULT_OK) {
                    GalleryDBAdapter db = new GalleryDBAdapter(getActivity());

                    String path = cameraAction.get_Path();
                    String album = cameraAction.get_album();
                    String time = cameraAction.get_timestamp();
                    String timestamp = Function.converToTimeStamp(time).toString();

                    db.insert_photo(path, album, timestamp);
                    String countPhoto = db.getCount(album);
                    int i=-1;

                    for(HashMap<String, String> album_i :albumList){
                        if (album_i.get(Function.KEY_ALBUM).equals(album)){
                            i = albumList.indexOf(album_i);
                        }
                    }
                    if(i != -1) {
                        albumList.remove(i);
                    }
                    albumList.add(i,Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), countPhoto));
                    adapter.onActivityResult(REQ_TAKE_CAMARA, 1);
                    break;
                }
            }
        }
    }
}