package com.example.week1.ui.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.week1.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AlbumActivity_checkable extends AppCompatActivity {
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    String album_name = "";
    LoadAlbumImages loadAlbumTask;
    SingleAlbumAdapter_checkable adapter;
    static final int REQ_DELETE_IMAGE =2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_checkable);

        Intent intent = getIntent();
        album_name = intent.getStringExtra("name");

        TextView title = findViewById(R.id.title_album_);
        title.setText(album_name);

        Button button = findViewById(R.id.button_delete);
        button.setVisibility(View.VISIBLE);

        galleryGridView = (GridView) findViewById(R.id.galleryGridView);
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();

        // DELETE BUTTON
        button.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                System.out.println("click click clickclickclickclickclickclickclickclickclickclickclickvv click");
                SparseBooleanArray checkedItems = galleryGridView.getCheckedItemPositions();

                if(checkedItems != null){

                    int count = adapter.getCount();
                    for (int i =count-1; i>=0;i--){
                        if (checkedItems.get(i)){

                            String path = imageList.get(i).get(Function.KEY_PATH);
                            String album = imageList.get(i).get(Function.KEY_ALBUM);
                            String timestamp = imageList.get(i).get(Function.KEY_TIMESTAMP);

                            GalleryDBAdapter db = new GalleryDBAdapter(v.getContext());
                            db.delete_photo(path, album, timestamp);

                            File f = new File(path);
                            f.delete();  //TODO:

                            imageList.remove(i);
                        }
                    }
                    galleryGridView.clearChoices();
                    adapter.onActivityResult(REQ_DELETE_IMAGE, 1);
                }
            }

        });

    }
    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }


    class LoadAlbumImages extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            imageList = load_photos(album_name);

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            adapter = new SingleAlbumAdapter_checkable(AlbumActivity_checkable.this, imageList);
            galleryGridView.setAdapter(adapter);

        }
    }


    private ArrayList<HashMap<String, String>> load_photos(String album_name) {
        GalleryDBAdapter db = new GalleryDBAdapter(this);
        return db.retreive_photos_inAlbum(album_name);
    }

}

class SingleAlbumAdapter_checkable extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap< String, String >> data;
    public SingleAlbumAdapter_checkable(Activity a, ArrayList < HashMap < String, String >> d) {
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
        SingleAlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new SingleAlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.single_album_row, parent, false);

            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.checkBox.setVisibility(View.VISIBLE);

            holder.checkBox.setChecked(((GridView)parent).isItemChecked(position));



            convertView.setTag(holder);
        } else {
            holder = (SingleAlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);
        try {

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

