package com.buggyarts.showcase.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buggyarts.showcase.R;
import com.buggyarts.showcase.cameraWork.Camera2Activity;
import com.buggyarts.showcase.customClasses.AppUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static com.buggyarts.showcase.customClasses.AppUtils.IMAGE_PLACE_HOLDER;
import static com.buggyarts.showcase.customClasses.AppUtils.STORAGE_PATH_UPLOADS;

public class ImageUploadAcitivty extends AppCompatActivity implements View.OnClickListener {

    ImageView capturedImage;
    private Uri mImageUri;
    TextView buttonCamera;
    TextView buttonGallery;
    TextView buttonUpload;

    String TAG = ImageUploadAcitivty.class.getSimpleName();

    private static int CAMERA_REQUEST = 200;
    private static int GALLERY_REQUEST = 202;
    private static int PREVIEW_REQUEST = 203;

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        capturedImage = findViewById(R.id.capturedImage);
        buttonCamera = findViewById(R.id.camera);
        buttonGallery = findViewById(R.id.gallery);
        buttonUpload = findViewById(R.id.upload);
        buttonUpload.setVisibility(View.GONE);

        buttonCamera.setOnClickListener(this);
        buttonGallery.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

        Glide.with(this).load(IMAGE_PLACE_HOLDER).into(capturedImage);

        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.camera){

            Intent intent = new Intent(ImageUploadAcitivty.this, Camera2Activity.class);
            startActivityForResult(intent,CAMERA_REQUEST);

        }else if(view.getId() == R.id.gallery){

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent,"Select ShowcaseImage From Gallery"),GALLERY_REQUEST);

        }else if(view.getId() == R.id.upload){
            upload();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == CAMERA_REQUEST){
                if(data != null){
                    if(data.hasExtra("filePath")){
                        String filePath = data.getStringExtra("filePath");
                        Glide.with(this).load(filePath).into(capturedImage);

                        File file = new File(filePath);
                        Uri uri = Uri.fromFile(file);
                        mImageUri = uri;
                        buttonUpload.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(ImageUploadAcitivty.this, PreviewActivity.class);
                        intent.putExtra("imageUri", filePath);
                        startActivityForResult(intent,PREVIEW_REQUEST);

                    }
                }
            }else if(requestCode == GALLERY_REQUEST){

                if(data!= null){
                    Uri uri  = data.getData();
                    mImageUri = uri;
                    buttonUpload.setVisibility(View.VISIBLE);
                    Glide.with(this).load(mImageUri).into(capturedImage);

                    Intent intent = new Intent(ImageUploadAcitivty.this, PreviewActivity.class);
                    intent.putExtra("imageUri2", mImageUri.toString());
                    startActivityForResult(intent,PREVIEW_REQUEST);
                }

            }else if(requestCode == PREVIEW_REQUEST){
                if(data!= null){
                    if(data.hasExtra("filePath")){
                        String filePath = data.getStringExtra("filePath");
                        File file = new File(filePath);
                        Uri uri = Uri.fromFile(file);
                        mImageUri = uri;
                        buttonUpload.setVisibility(View.VISIBLE);
                        Glide.with(this).load(mImageUri).into(capturedImage);
                    }
                }
            }
        }
    }

    private void upload(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        StorageReference riversRef = mStorageRef.child(STORAGE_PATH_UPLOADS).child(mImageUri.getLastPathSegment());

        riversRef.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        AppUtils.addToList(downloadUrl.toString(), ImageUploadAcitivty.this);

                        Toast.makeText(ImageUploadAcitivty.this,"Upload Complete", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: " + exception);
                        // Handle unsuccessful uploads
                        // ...
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //displaying the upload progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }

}
