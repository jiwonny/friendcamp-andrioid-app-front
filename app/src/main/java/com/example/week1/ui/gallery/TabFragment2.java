package com.example.week1.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.week1.R;
import com.example.week1.ui.main.PageViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TabFragment2 extends Fragment {

    private PageViewModel pageViewModel;

    public TabFragment2 () { }


    LoadAlbum loadAlbumTask;
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 2;
        pageViewModel.setIndex(index);
    }


    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tabfragment2, container, false);
        galleryGridView = (GridView) root.findViewById(R.id.galleryGridView);

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

        return root;
    }

    class LoadAlbum extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        //Generate image data
        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;
            String countPhoto = null;
            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;


            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };
            Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});

            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                countPhoto = Function.getCount(getActivity().getApplicationContext(), album);

                albumList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), countPhoto));
            }
            cursor.close();
            Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        // Set Adapter
        @Override
        protected void onPostExecute(String xml) {

            AlbumAdapter adapter = new AlbumAdapter(getActivity(), albumList);
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
    }


    class AlbumViewHolder {
        ImageView galleryImage;
        TextView gallery_count, gallery_title;
    }

}