package com.example.mytips.setting.editprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.TaskStackBuilder;

import com.bumptech.glide.Glide;
import com.example.mytips.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;

public class EditProfilePhotoPreview extends AppCompatActivity {
    private PhotoView photoView;
    private AppBarLayout appBarLayout;
    private Toolbar toolBar;
    private String imageUri;
    public EditProfilePhotoPreview() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_edit_profile_photo_preview);
        initPhotoView();
        initToolBar();
    }
    private void initPhotoView(){
        photoView=this.findViewById(R.id.activity_edit_profile_photo_preview_photo);
        appBarLayout=this.findViewById(R.id.activity_edit_profile_photo_preview_appbar);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appBarLayout.getVisibility()==View.VISIBLE ){
                    appBarLayout.setVisibility(View.GONE);
                } else{
                    appBarLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        Intent intent=getIntent();
        imageUri=intent.getStringExtra("imageUri");
        Glide.with(this)
                .load(imageUri)
                .into(photoView);
    }
    private void initToolBar(){
        toolBar=this.findViewById(R.id.activity_edit_profile_photo_preview_toolbar);
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
        menuInflater.inflate(R.menu.activity_edit_profile_photo_preview_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.activity_edit_profile_photo_check){
            if(imageUri!=null){
                Intent intent=new Intent(this,EditProfilePhotoCrop.class);
                intent.putExtra("imageUri",imageUri);
                startActivity(intent);
            }
        }
        return true;
    }
}
