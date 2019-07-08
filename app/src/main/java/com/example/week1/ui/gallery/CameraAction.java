package com.example.week1.ui.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.example.week1.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraAction {
    Context c;
    String mCurrentPhotoPath;
    String timeStamp;
    String appName;

    public static void CameraAction(){
    }

    public File getAlbumdir(Context c){
        appName = c.getString(R.string.app_name);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"/"+appName);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir;
    }

    final static String JPEG_FILE_PREFIX = "IMG_";

    final static String JPEG_FILE_SUFFIX = ".jpg";



    public File createImageFile(Context c) throws IOException {

        timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss").format( new Date());

        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";

        File image = File.createTempFile(
                imageFileName,			// prefix
                JPEG_FILE_SUFFIX,		// suffix
                getAlbumdir(c)				// directory
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected void galleryAddPic(Context c, String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        c.sendBroadcast(mediaScanIntent);
    }




    public String get_timestamp(){
        return timeStamp;
    }
    public String get_Path(){
        return mCurrentPhotoPath;
    }
    public String get_album(){
        return appName;
    }

}
