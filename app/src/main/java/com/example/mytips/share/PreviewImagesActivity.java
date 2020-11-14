package com.example.mytips.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mytips.R;
import com.example.mytips.model.Image;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class PreviewImagesActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, PreviewPagerAdapter.OnPhotoClickListener{
    private int currentImagePosition;
    private static final int MAX_IMAGES_NUMBER=9;
    private ArrayList<Image> imageList,selectedImages;
    private PreviewPagerAdapter previewPagerAdapter;
    private ViewPager viewPager;
    private AppBarLayout appBarLayout;
    private Toolbar toolBar;
    private RelativeLayout bottomRelativeLayout;
    private CheckBox selectCheckBox;
    private String sendButtonTitle, toolBarTitle;
    private MenuItem sendButton;
    private View checkBoxMask;
    private ArrayList<Integer> selectedImageIndex=new ArrayList<>();
    private ArrayList<Integer> unselectedImageIndex=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_images);
        initWidgets();
        Intent intent=getIntent();
        selectedImages=intent.getParcelableArrayListExtra("selectedImages");
        imageList=intent.getParcelableArrayListExtra("imageList");
        currentImagePosition=intent.getIntExtra("imagePosition",0);
        previewPagerAdapter=new PreviewPagerAdapter(this,imageList);
        viewPager.setAdapter(previewPagerAdapter);
        viewPager.setCurrentItem(currentImagePosition-1);
        viewPager.addOnPageChangeListener(this);
        initCheckBoxSelectState();
        initToolBar();
    }

    private void initToolBar(){
        toolBar=this.findViewById(R.id.activity_preview_image_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ImageAlbumsActivity.class);
                intent.putIntegerArrayListExtra("selectedImageIndex",selectedImageIndex);
                intent.putIntegerArrayListExtra("unselectedImageIndex",unselectedImageIndex);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
        toolBarTitle=currentImagePosition+"/"+imageList.size();
        toolBar.setTitle(toolBarTitle);
        toolBar.setTitleTextColor(getResources().getColor(R.color.orange));
    }
    private void initWidgets(){
        viewPager=this.findViewById(R.id.activity_preview_image_vp);
        appBarLayout=this.findViewById(R.id.activity_preview_image_appbar);
        bottomRelativeLayout=this.findViewById(R.id.activity_preview_image_rl);
        selectCheckBox=this.findViewById(R.id.activity_preview_image_cb);
        checkBoxMask=this.findViewById(R.id.activity_preview_image_check_box_mask);
    }
    private void initCheckBoxSelectState(){
        if(imageList.get(currentImagePosition-1).getSelectionState()){
            selectCheckBox.setChecked(true);
        }else{
            selectCheckBox.setChecked(false);
        }
        checkBoxMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition=viewPager.getCurrentItem();
                if(selectCheckBox.isChecked()){
                    selectCheckBox.setChecked(false);
                    if(selectedImages.contains(imageList.get(currentPosition))){
                       imageList.get(currentPosition).setSelectionState(false);
                       selectedImages.remove(imageList.get(currentPosition));
                       upDateSendButtonText();
                        if(selectedImageIndex.contains(currentPosition)){
                            selectedImageIndex.remove(Integer.valueOf(currentPosition));
                        }
                        unselectedImageIndex.add(currentPosition);
                    }
                } else{
                    if(selectedImages.size()<MAX_IMAGES_NUMBER){
                        selectCheckBox.setChecked(true);
                        if(!selectedImages.contains(imageList.get(currentPosition))){
                            imageList.get(currentPosition).setSelectionState(true);
                            selectedImages.add(imageList.get(currentPosition));
                            upDateSendButtonText();
                            if(unselectedImageIndex.contains(currentPosition)){
                                unselectedImageIndex.remove(Integer.valueOf(currentPosition));
                            }
                            selectedImageIndex.add(currentPosition);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),R.string.maximum_number_of_image_reminder,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void upDateSendButtonText(){
        sendButtonTitle=getResources().getString(R.string.toolbar_send_button_default_title)+"("+selectedImages.size()+" / "+MAX_IMAGES_NUMBER+")";
        sendButton.setTitle(sendButtonTitle);

    }
    private void upDateToolbarTitle(int position){
        toolBarTitle=position+"/"+imageList.size();
        toolBar.setTitle(toolBarTitle);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.activity_preview_tool_bar_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        sendButton=menu.getItem(0);
        sendButtonTitle=getResources().getString(R.string.toolbar_send_button_default_title)+"("+selectedImages.size()+" / "+MAX_IMAGES_NUMBER+")";
        sendButton.setTitle(sendButtonTitle);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.preview_toolbar_menu_send){
            if(!selectedImages.isEmpty()){
                Intent intent=new Intent(this,ShareActivity.class);
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.putParcelableArrayListExtra("selectedImages",selectedImages);
                startActivity(intent);
            } else {
                Toast.makeText(this,R.string.media_not_selected_reminder,Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(imageList.get(position).getSelectionState()){
            selectCheckBox.setChecked(true);
        }else {selectCheckBox.setChecked(false);}
        upDateToolbarTitle(position+1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPhotoClick() {
        if(appBarLayout.getVisibility()==View.VISIBLE && bottomRelativeLayout.getVisibility()==View.VISIBLE){
            appBarLayout.setVisibility(View.GONE);
            bottomRelativeLayout.setVisibility(View.GONE);
        } else{
            appBarLayout.setVisibility(View.VISIBLE);
            bottomRelativeLayout.setVisibility(View.VISIBLE);
        }
    }
}
