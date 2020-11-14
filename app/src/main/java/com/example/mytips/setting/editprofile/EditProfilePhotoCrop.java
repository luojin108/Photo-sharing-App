package com.example.mytips.setting.editprofile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mytips.R;
import com.example.mytips.utils.FireBaseMethods;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class EditProfilePhotoCrop extends AppCompatActivity {
    private final String TAG="EditProfilePhotoCrop";
    private Toolbar toolBar;
    private CropImageView cropImageView;
    private String imageUri;
    private Uri uri;
    public EditProfilePhotoCrop() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_photo_crop);
        initToolBar();
        initCropImage();
    }

    private void initCropImage(){
        cropImageView=this.findViewById(R.id.activity_edit_profile_photo_crop_photo);
        Intent intent=getIntent();
        imageUri=intent.getStringExtra("imageUri");
        uri=Uri.fromFile(new File(imageUri));
        cropImageView.setImageUriAsync(uri);
//        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
//            @Override
//            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
//                Uri croppedPhotoUri=result.getUri();
//                FireBaseMethods fireBaseMethods=new FireBaseMethods(getApplicationContext());
//                Log.d(TAG, "onCropImageComplete: Uri"+ croppedPhotoUri);
//                //fireBaseMethods.uploadProfilePhoto(croppedPhotoUri);
//                //finish();
//
//            }
//        });
    }
    private void initToolBar(){
        toolBar=this.findViewById(R.id.activity_edit_profile_photo_crop_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.activity_edit_profile_photo_crop_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.activity_edit_profile_photo_crop_check){
           // cropImageView.saveCroppedImageAsync(uri);
            //without saving the cropped image
            item.setCheckable(false);
            Bitmap cropped=cropImageView.getCroppedImage();
            FireBaseMethods fireBaseMethods=new FireBaseMethods(this);
            fireBaseMethods.uploadProfilePhoto(cropped);
            Intent intent=new Intent(this,EditProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

}
