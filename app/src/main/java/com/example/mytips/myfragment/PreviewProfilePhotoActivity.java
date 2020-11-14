package com.example.mytips.myfragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mytips.R;
import com.example.mytips.share.ImageAlbumsActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;

public class PreviewProfilePhotoActivity extends AppCompatActivity {
    private PhotoView photoView;
    private Toolbar toolBar;
    private AppBarLayout appBarLayout;
    public PreviewProfilePhotoActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_preview_profile_photo);
        initToolBar();
        photoView=this.findViewById(R.id.activity_preview_profile_photo);
        appBarLayout=this.findViewById(R.id.activity_preview_profile_photo_appbar);
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
        String profilePhotoUri=intent.getStringExtra("profilePhotoUri");
        Glide.with(this)
                .load(profilePhotoUri)
                .into(photoView);

    }
    private void initToolBar(){
        toolBar=this.findViewById(R.id.activity_preview_profile_photo_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
