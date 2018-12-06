package com.buggyarts.showcase.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.buggyarts.showcase.R;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener {

    // variables
    String TAG = LauncherActivity.class.getSimpleName();

    // views
    TextView buttonCaptureImage;
    TextView buttonShowImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        buttonCaptureImage = findViewById(R.id.button_capture_image);
        buttonShowImages = findViewById(R.id.button_show_images);

        buttonShowImages.setOnClickListener(this);
        buttonCaptureImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_capture_image){

            Intent intent = new Intent(LauncherActivity.this, ImageUploadAcitivty.class);
            startActivity(intent);

        }else if(view.getId() == R.id.button_show_images){

        }
    }

}
