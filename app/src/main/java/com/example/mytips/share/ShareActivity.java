package com.example.mytips.share;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytips.MainActivity;
import com.example.mytips.R;
import com.example.mytips.model.Image;
import com.example.mytips.utils.FireBaseMethods;
import com.example.mytips.utils.MyOnProgressListener;
import com.example.mytips.utils.Permissions;
import com.example.mytips.utils.SoftKeyBoardHidingHelper;

import java.io.File;
import java.util.ArrayList;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener,ShareItemTouchListener, MyOnProgressListener.OnUploadProgressListener {
    private Toolbar mToolBar;
    private RecyclerView mRecyclerView;
    private ShareImageAdapter shareImageAdapter;
    private Button mPostButton;
    private ImageButton addImageButton;
    private EditText titleEditText,descriptionEditText;
    private ProgressBar progressBar;
    private AppCompatTextView progressText;
    private ArrayList<Image> selectedImages=new ArrayList<>();
    public ShareActivity() {
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initWidgets();
        initToolBar();
        initRecyclerView();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=this.getIntent();
        ArrayList<Image> selectedImagesFromAlbum=intent.getParcelableArrayListExtra("selectedImages");
        if(selectedImagesFromAlbum!=null){
            selectedImages.clear();
            selectedImages.addAll(selectedImagesFromAlbum);
        }
        shareImageAdapter.notifyDataSetChanged();
    }
    private void initToolBar(){
        mToolBar=this.findViewById(R.id.activity_share_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initWidgets(){
        progressBar=this.findViewById(R.id.activity_share_progress_bar);
        progressText=this.findViewById(R.id.activity_share__text_uploading);
        mPostButton=this.findViewById(R.id.activity_share_post_button);
        mRecyclerView=this.findViewById(R.id.activity_share_rv);
        addImageButton=this.findViewById(R.id.activity_share_add_button);
        titleEditText=this.findViewById(R.id.activity_share_title_edit_text);
        descriptionEditText=this.findViewById(R.id.activity_share_description_edit_text);
        addImageButton.setOnClickListener(this);
        mPostButton.setOnClickListener(this);
    }
    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        shareImageAdapter=new ShareImageAdapter(selectedImages,this);
        mRecyclerView.setAdapter(shareImageAdapter);
        ItemTouchHelper.Callback callback=new ShareTouchHelperCallback(this);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
    @Override
    public void onClick(View v) {
        if(checkPermissions()){
            switch (v.getId()){
                case R.id.activity_share_add_button:
                    Intent mIntent=new Intent(this, ImageAlbumsActivity.class);
                    if(selectedImages!=null&&!selectedImages.isEmpty()){
                        mIntent.putParcelableArrayListExtra("selectedImagesShareActivity",selectedImages);
                    }
                    startActivity(mIntent);
                    break;
                case R.id.activity_share_post_button:
                    if(selectedImages.size()>0){
                        disableWidgetsInteraction();
                        progressBar.setVisibility(View.VISIBLE);
                        ArrayList<String> imageUriList=new ArrayList<>();
                        for(int i=0;i<=selectedImages.size()-1;i++){
                            String imagePath=selectedImages.get(i).getImagePath();
                            // Uri imageUri=Uri.fromFile(new File(imagePath));
                            imageUriList.add(imagePath);
                        }
                        String title=titleEditText.getText().toString();
                        String description=descriptionEditText.getText().toString();
                        FireBaseMethods fireBaseMethods=new FireBaseMethods(this);
                        fireBaseMethods.uploadPublicImagePost(title,"",description,imageUriList);
                    }else (Toast.makeText(this,R.string.share_activity_reminder,Toast.LENGTH_SHORT)).show();
            }
        } else { ActivityCompat.requestPermissions(this, Permissions.PERMISSIONS, 1);}
    }
    private void disableWidgetsInteraction(){
        mPostButton.setClickable(false);
        mPostButton.setFocusable(false);
        titleEditText.setClickable(false);
        titleEditText.setFocusable(false);
        descriptionEditText.setClickable(false);
        descriptionEditText.setFocusable(false);
        addImageButton.setClickable(false);
        addImageButton.setFocusable(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.activity_share_tool_bar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_info){
        }
        return true;
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
    //......ShareItemTouchListener methods
    @Override
    public void onItemSwiped(int position) {
        selectedImages.remove(position);
        shareImageAdapter.notifyItemRemoved(position);
    }
    @Override
    public void onItemMoved(int positionFrom, int positionTo) {
        Image removed = selectedImages.remove(positionFrom);
        if(positionFrom<positionTo){
            selectedImages.add(positionTo-1,removed);
        } else if (positionFrom>positionTo){
            selectedImages.add(positionTo,removed);
        }
        shareImageAdapter.notifyItemMoved(positionFrom,positionTo);
    }
    //......
    //update progress bar
    @Override
    public void onUploadImagePosts(double progress) {
        if(progress>=98){
            Intent intent=new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        }
        progressText.setText(getText(R.string.share_activity_uploading_reminder).toString()+(int)progress+"%");
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (titleEditText.isFocused()) {
                Rect outRect = new Rect();
                titleEditText.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(),(int)event.getRawY()))   {
                    SoftKeyBoardHidingHelper.hideSoftKeyBoard(this);
                    titleEditText.clearFocus();
                }
            }
            if (descriptionEditText.isFocused()) {
                Rect outRect = new Rect();
                descriptionEditText.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(),(int)event.getRawY()))   {
                    SoftKeyBoardHidingHelper.hideSoftKeyBoard(this);
                    descriptionEditText.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
