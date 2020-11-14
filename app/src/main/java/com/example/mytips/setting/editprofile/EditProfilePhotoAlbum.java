package com.example.mytips.setting.editprofile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mytips.R;
import com.example.mytips.model.ImageFolder;
import com.example.mytips.utils.DirectorySelectorBottomSheet;
import com.example.mytips.utils.FolderContainerBuilder;
import com.example.mytips.utils.OnItemClickListener;
import com.example.mytips.utils.Permissions;

import java.util.ArrayList;

public class EditProfilePhotoAlbum extends AppCompatActivity implements View.OnClickListener,DirectorySelectorBottomSheet.OnDirectorySelectListener {
    private RecyclerView recyclerView;
    private OnItemClickListener onItemClickListener;
    private EditProfilePhotoRecyclerViewAdapter editProfilePhotoRecyclerViewAdapter;
    private AppCompatButton directorySelectorButton;
    private Toolbar toolBar;
    private DirectorySelectorBottomSheet directorySelectorBottomSheet;
    private static ArrayList<ImageFolder> folderContainer;
    private ArrayList<ImageFolder> data=new ArrayList<>();
    public EditProfilePhotoAlbum() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!checkPermissions()){
            ActivityCompat.requestPermissions(this, Permissions.PERMISSIONS, 1);
        }
        setContentView(R.layout.activity_edit_profile_photo);
        initWidgets();
        initToolBar();
        initFolderContainer();
        initRecyclerView();
    }
    private Boolean checkPermissions(){
        for(int i = 0; i< Permissions.PERMISSIONS.length; i++){
            if(!checkSinglePermission(Permissions.PERMISSIONS[i])){
                return false;
            }
        } return true;
    }
    private Boolean checkSinglePermission(String s){
        int permissionRequest= ActivityCompat.checkSelfPermission(this,s);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(!checkPermissions()){
                finish();
            }
        }
    }
    private void initToolBar(){
        toolBar=this.findViewById(R.id.activity_edit_profile_photo_tool_bar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initWidgets(){
        directorySelectorButton=this.findViewById(R.id.edit_profile_photo_switch_dir_button);
        directorySelectorButton.setOnClickListener(this);

    }
    private void initFolderContainer(){
        FolderContainerBuilder folderContainerBuilder=new FolderContainerBuilder(this);
        folderContainer=folderContainerBuilder.buildImageFolderContainer();
    }
    private void initRecyclerView(){
        recyclerView=this.findViewById(R.id.edit_profile_photo_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        directorySelectorButton.setText(folderContainer.get(0).getFolderName());
        data.add(folderContainer.get(0));
        onItemClickListener=new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getApplicationContext(), EditProfilePhotoPreview.class);
                String imageUri=data.get(0).getImageList().get(position).getImagePath();
                intent.putExtra("imageUri", imageUri);
                startActivity(intent);
            }
        };
        editProfilePhotoRecyclerViewAdapter=new EditProfilePhotoRecyclerViewAdapter(this,data,onItemClickListener);
        recyclerView.setAdapter(editProfilePhotoRecyclerViewAdapter);
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.edit_profile_photo_switch_dir_button){

            if(directorySelectorBottomSheet==null) {
                directorySelectorBottomSheet = new DirectorySelectorBottomSheet(this,folderContainer);
            }
            if(!directorySelectorBottomSheet.isShowing()){
                directorySelectorBottomSheet.show();
            }
        }
    }
    @Override
    public void onDirectorySelect(ImageFolder imageFolder) {
        directorySelectorButton.setText(imageFolder.getFolderName());
        data.clear();
        data.add(imageFolder);
        editProfilePhotoRecyclerViewAdapter.notifyDataSetChanged();
    }
}
