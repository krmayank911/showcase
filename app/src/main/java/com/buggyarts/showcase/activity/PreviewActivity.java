package com.buggyarts.showcase.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.showcase.R;
import com.buggyarts.showcase.cameraWork.Camera2Activity;
import com.buggyarts.showcase.customClasses.ImagePreviewView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView cropImage;
    private TextView buttonDone;
    private ImagePreviewView mPinchZoomImageView;
    private Uri mImageUri;
    private Bitmap mImageBitmap;
    private String filePath;
    private Boolean hasBitmap = false;
    private static int CROP_REQUEST = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mPinchZoomImageView = findViewById(R.id.imagePreview);
        buttonDone = findViewById(R.id.done);
        cropImage = findViewById(R.id.button_crop);
        cropImage.setOnClickListener(this);
        buttonDone.setOnClickListener(this);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra("imageUri")){
                String imageUri = intent.getStringExtra("imageUri");
                filePath = imageUri;
                File file = new File(filePath);
                Uri uri = Uri.fromFile(file);
                mImageUri = uri;
                pinchZoomPan();
            }else if(intent.hasExtra("imageUri2")){
                String imageUri = intent.getStringExtra("imageUri2");
                mImageUri = Uri.parse(imageUri);
                pinchZoomPan();
            }else if(intent.hasExtra("imageUrl")){
                String imageUrl = intent.getStringExtra("imageUrl");
                loadWithGlide(imageUrl);
            }

        }

    }

    private void pinchZoomPan() {

        if(hasBitmap){
            mPinchZoomImageView.setImageBitmap(mImageBitmap);
        }else {
            mPinchZoomImageView.setImageUri(mImageUri);
        }
        mPinchZoomImageView.setVisibility(View.VISIBLE);
    }

    private void performCrop(Uri picUri){

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_REQUEST);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == CROP_REQUEST){

                if(data != null) {
                    //get the returned data
                    Bundle extras = data.getExtras();
                    //get the cropped bitmap
                    if(extras != null) {
                        Bitmap thePic = extras.getParcelable("data");
                        saveImage(thePic);
                    }else {
                        Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT);
                    }
                }
            }
        }
    }

    public void closeActivityWithResult(){
        Intent resultIntent = new Intent();
        if(filePath != null){
            resultIntent.putExtra("filePath",filePath);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        PreviewActivity.this.finish();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_crop){
            performCrop(mImageUri);
        }else if(view.getId() == R.id.done){
            closeActivityWithResult();
        }
    }

    private void saveImage(Bitmap bitmap){
        String fileName = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()).toString();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + ".jpg";

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File file = new File(filePath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            this.filePath = filePath;
            mPinchZoomImageView.setImageUri(Uri.fromFile(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        closeActivityWithResult();
        return super.onKeyDown(keyCode, event);
    }

    private void loadWithGlide(String image){
        Glide.with(this).asBitmap().load(image)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mImageBitmap = resource;
                        hasBitmap = true;
                        pinchZoomPan();
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

}
