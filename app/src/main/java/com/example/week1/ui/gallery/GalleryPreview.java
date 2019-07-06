package com.example.week1.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.week1.R;
import com.example.week1.network.APICallback;
import com.example.week1.network.APIClient;


/**
 * Created by SHAJIB on 25/12/2015.
 */
public class GalleryPreview extends AppCompatActivity {

    ImageView GalleryPreviewImg;
    String url;
    String login_id;
    Intent intent;
    APIClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gallery_preview);

        apiClient = APIClient.getInstance(this, "143.248.39.49",4500).createBaseApi();

        // Get intent from TabFragment2
        intent = getIntent();
        //TODO : PUT CURRENT LOGIN ID
        login_id = "login_id";
        url = intent.getStringExtra("url");

        GalleryPreviewImg = (ImageView) findViewById(R.id.GalleryPreviewImg);
        Glide.with(GalleryPreview.this)
                .load(url) // Uri of the picture
                .into(GalleryPreviewImg);

        registerForContextMenu(GalleryPreviewImg);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d("test", "onCreateContextMenu");
        //getMenuInflater().inflate(R.menu.main, menu);
        menu.setHeaderTitle("Menu");
        menu.add(0,1,100,"DELETE");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case 1 :// DELETE
                apiClient.deleteImage(login_id, url, new APICallback() {
                    @Override
                    public void onError(Throwable t) {

                        Toast.makeText(GalleryPreview.this, "NETWORK NOT CONNECTED", Toast.LENGTH_SHORT).show();
                        Log.e("LOG", t.toString());
                    }

                    @Override
                    public void onSuccess(int code, Object receivedData) {

                        Toast.makeText(GalleryPreview.this, "DELETE SUCCESS", Toast.LENGTH_SHORT).show();
                        Intent intent_r = new Intent();
                        intent_r.putExtra("login_id", login_id);
                        intent_r.putExtra("url", url);
                        setResult(RESULT_OK, intent_r);
                        finish();
                    }

                    @Override
                    public void onFailure(int code) {

                        Toast.makeText(GalleryPreview.this, "DELETE FAIL", Toast.LENGTH_SHORT).show();
                        Log.e("FAIL", String.format("code : %d", code));
                    }
                });

                return true;
        }

        return super.onContextItemSelected(item);
    }
}
