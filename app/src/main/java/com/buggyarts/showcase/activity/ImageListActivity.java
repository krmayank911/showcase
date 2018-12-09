package com.buggyarts.showcase.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.buggyarts.showcase.R;
import com.buggyarts.showcase.adapters.ImagesAdapter;
import com.buggyarts.showcase.customClasses.AppUtils;

import java.util.ArrayList;


public class ImageListActivity extends AppCompatActivity implements ImagesAdapter.Callback, View.OnClickListener {

    ArrayList<String> showcaseImages = new ArrayList<>();

    RecyclerView imagesRV;
    RecyclerView.LayoutManager layoutManager;
    ImagesAdapter adapter;
    ImageView backButton;

    String TAG = ImageListActivity.class.getSimpleName();

    private static int PREVIEW_REQUEST = 203;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        imagesRV = findViewById(R.id.images_rv);
        layoutManager = new LinearLayoutManager(this);
        imagesRV.setLayoutManager(layoutManager);
        showcaseImages = AppUtils.getList(this);
        adapter = new ImagesAdapter(this,showcaseImages);
        adapter.setCallback(this);
        imagesRV.setAdapter(adapter);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(this);

    }

    @Override
    public void onImageClick(String imageUrl) {
        Intent intent = new Intent(ImageListActivity.this, PreviewActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.back_button){
            this.finish();
        }
    }
}
