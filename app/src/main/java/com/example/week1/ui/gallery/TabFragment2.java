package com.example.week1.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.week1.R;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;
import com.example.week1.network.Image_f;
import com.example.week1.network.User;
import com.example.week1.persistence.GalleryDBAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.content.Context.MODE_PRIVATE;

public class TabFragment2 extends Fragment  implements ActivityCompat.OnRequestPermissionsResultCallback {


    private static final int PERMISSIONS_REQUEST_CODE_2 = 11;
    static final int REQ_TAKE_CAMARA = 1 ;
    static final int REQ_PICK_IMAGE = 2 ;
    static final int REQ_DELETE =3 ;
    View root;
    LoadAlbum loadAlbumTask;
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    CameraAction cameraAction;
    AlbumAdapter adapter;
    APIClient apiClient;

    String user_name;
    String user_id;
    String user_number;
    String user_profile;

    public TabFragment2(){  }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = APIClient.getInstance(getActivity(), "143.248.39.49",4500).createBaseApi();

        SharedPreferences sf = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = sf.getString("currentUser_email", "");
        user_name = sf.getString("currentUser_name", "");
        user_number = sf.getString("currentUser_number", "");
        user_profile = sf.getString("currentUser_profile","");
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.tabfragment2, container, false);

        TextView Profile_name = root.findViewById(R.id.Profile_name);
        TextView Profile_id = root.findViewById(R.id.Profile_id);
        TextView Profile_number = root.findViewById(R.id.Profile_number);
        Profile_name.setText(user_name);
        Profile_id.setText(user_id);
        Profile_number.setText(user_number);

        Button upload_image = root.findViewById(R.id.button_upload);
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQ_PICK_IMAGE);
            }
        });


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


        galleryGridView = (GridView) root.findViewById(R.id.galleryGridView);

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

        init_photos();

        return root;
    }

    public void init_photos(){

        loadAlbumTask = new TabFragment2.LoadAlbum();
        loadAlbumTask.execute();

        ImageView Profile_image = root.findViewById(R.id.Profile_image);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                apiClient.getUserfrom_Name_LoginId(user_name, user_id, new APICallback() {
                    @Override
                    public void onError(Throwable t) { }

                    @Override
                    public void onSuccess(int code, Object receivedData) {
                        User data = (User) receivedData;
                        user_profile = data.getProfile_image_id();
                        if(user_profile != null){
                            try {
                                //TODO : PUT SERVER URL
                                String url =  String.format("http://%s:%d/%s", "143.248.39.49",4500, user_profile);
                                Glide.with(getActivity())
                                        .load( url ) // Url of the picture
                                        .dontAnimate()
                                        .into(Profile_image);
                            } catch (Exception e) {}
                        }
                    }
                    @Override
                    public void onFailure(int code) {

                    }
                });
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();
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
            getPhotosfromServer();

            return xml;
        }
        // Set Adapter
        @Override
        protected void onPostExecute(String xml) {
            super.onPostExecute(xml);
            GalleryDBAdapter db = new GalleryDBAdapter(getActivity());
            albumList = load_photos();
            adapter = new AlbumAdapter(getActivity(), albumList);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Intent intent = new Intent(getActivity(), GalleryPreview.class);
                    intent.putExtra("url", albumList.get(+position).get(Function.KEY_URL));
                    startActivityForResult(intent, REQ_DELETE);
                }
            });

            galleryGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                    String url = albumList.get(+position).get(Function.KEY_URL);
                    String file = albumList.get(+position).get(Function.KEY_FILE);

                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
                    alt_bld.setMessage("Do you want to delete the photo?").setCancelable(
                            false).setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    apiClient.deleteImage(user_id, file, new APICallback() {
                                        @Override
                                        public void onError(Throwable t) {

                                            Toast.makeText(view.getContext(), "NETWORK NOT CONNECTED", Toast.LENGTH_SHORT).show();
                                            Log.e("LOG", t.toString());
                                        }

                                        @Override
                                        public void onSuccess(int code, Object receivedData) {

                                            Toast.makeText(view.getContext(), "DELETE SUCCESS", Toast.LENGTH_SHORT).show();

                                            db.delete_photo(user_id, url);
                                            int i =0;
                                            for(HashMap<String, String> album : albumList){
                                                if( album.get(Function.KEY_FILE).equals(file)){
                                                    break;
                                                }
                                                i++;
                                            }

                                            albumList.remove(i);
                                            adapter.onActivityResult(1,1);
                                        }

                                        @Override
                                        public void onFailure(int code) {

                                            Toast.makeText(view.getContext(), "DELETE FAIL", Toast.LENGTH_SHORT).show();
                                            Log.e("FAIL", String.format("code : %d", code));
                                        }
                                    });
                                }
                            }).setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    // Title for AlertDialog
                    alert.setTitle("DELETE");
                    // Icon for AlertDialog
                    alert.show();
                    return true;
                }
            });

        }
    }

    // get Photos from Server
    public void getPhotosfromServer(){

        GalleryDBAdapter db = new GalleryDBAdapter(getActivity());
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                apiClient.getImageList(user_id, new APICallback() {
                    @Override
                    public void onError(Throwable t) { }

                    @Override
                    public void onSuccess(int code, Object receivedData) {
                        List<Image_f> data = (List<Image_f>) receivedData;
                        Log.d("Load", "Start-------------------------------------------"+data);
                        for( Image_f image_f : data){


                            String login_id = image_f.getLogin_id();
                            String url = String.format("http://%s:%d/%s", "143.248.39.49",4500, image_f.getUrl());
                            String file = image_f.getUrl();
                            String timestamp = image_f.getTimestamp();

                            Log.d("Load", String.format("id : %s, url : %s , timestamp : %s", login_id,url, timestamp));

                            db.insert_photo(login_id,url, file, timestamp);
                        }
                    }
                    @Override
                    public void onFailure(int code) {
                        Log.e("FAIL", String.format("code : %d", code));
                    }
                });
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();
    }

    // generate Albumlist for adpater
    private ArrayList<HashMap<String, String>> load_photos(){
        GalleryDBAdapter db = new GalleryDBAdapter(getActivity());
        return db.sellect_all();
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
                        .load( song.get(Function.KEY_URL) ) // Url of the picture
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

    // get result from Camera Action
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQ_TAKE_CAMARA: {
                if (resultCode == Activity.RESULT_OK) {
                    String path = cameraAction.get_Path();
                    cameraAction.galleryAddPic(getActivity(), path);
                    uploadImageToServer(path, user_id);

                }
                break;
            }
            case REQ_PICK_IMAGE: {
                if (resultCode == Activity.RESULT_OK){
                    Uri photoUri = intent.getData();
                    Cursor cursor = null;
                    try {
                        String[] proj = { MediaStore.Images.Media.DATA };
                        assert photoUri != null;
                        cursor = getActivity().getContentResolver().query(photoUri, proj, null, null, null);
                        assert cursor != null;
                        cursor.moveToNext();
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        cursor.close();
                        Log.d("path",path);

                        uploadImageToServer(path, user_id);

                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                break;
            }
            case REQ_DELETE: {
                if (resultCode == Activity.RESULT_OK){
                    String url = intent.getStringExtra("url");
                    String login_id = intent.getStringExtra("login_id");

                    int i =0;
                    for(HashMap<String, String> album : albumList){
                        if( album.get(Function.KEY_URL).equals(url)){
                            break;
                        }
                        i++;
                    }

                    albumList.remove(i);
                    adapter.onActivityResult(1,1);
                }
            }
        }
    }

    public void uploadImageToServer(String filePath, String login_id){
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("Gallery", file.getName(), fileReqBody);
        Log.d("filename", file.getName());


        apiClient.uploadImage(part, login_id, new APICallback() {
            @Override
            public void onError(Throwable t) {
                Log.e("LOG", t.toString());
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                //TODO : PUT SERVER URL
                String url =  String.format("http://%s:%d/%s", "143.248.39.49",4500,  user_id+'_'+file.getName());
                albumList.add(Function.mappingInbox(login_id, url, file.getName(), null));

                Log.d("ImageUpload", String.format("id: %s , url: %s -----------------",login_id, url));
                adapter.onActivityResult(1,1);
            }
            @Override
            public void onFailure(int code) {
                Log.e("FAIL", String.format("code : %d", code));
            }
        });
    }
}