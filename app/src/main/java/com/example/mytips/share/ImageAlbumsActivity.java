package com.example.mytips.share;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mytips.R;
import com.example.mytips.model.Image;
import com.example.mytips.model.ImageFolder;
import com.example.mytips.utils.DirectorySelectorBottomSheet;
import com.example.mytips.utils.FolderContainerBuilder;
import com.example.mytips.utils.OnItemClickListener;
import com.example.mytips.utils.Permissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;


public class ImageAlbumsActivity extends AppCompatActivity implements View.OnClickListener, DirectorySelectorBottomSheet.OnDirectorySelectListener, AlbumsAdapter.CheckBoxNotifier {
    private static final int CAMERA_REQUEST_CODE=0;
    private static final int PreviewImagesActivity_REQUEST_CODE=1;
    private static final int PreviewSelectedImagesActivity_REQUEST_CODE=2;
    private Toolbar mToolBar;
    private RecyclerView albumsRecyclerView;
    private AlbumsAdapter mAlbumsAdapter;
    private AppCompatButton directorySelectorButton, previewButton;
    private DirectorySelectorBottomSheet directorySelectorBottomSheet;
    private static ArrayList<ImageFolder> folderContainer;
    private ArrayList<ImageFolder> data=new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private File cameraImageFile;
    private Uri cameraImageUri;
    private ArrayList<Image> selectedImages=new ArrayList<>();
    private int MAX_IMAGE_NUMBER=9;


    public ImageAlbumsActivity() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!checkPermissions()){
            ActivityCompat.requestPermissions(this, Permissions.PERMISSIONS, 1);
        }
        setContentView(R.layout.activity_image_albums);
        initWidgets();
        initToolBar();
        initFolderContainer();
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=this.getIntent();
        ArrayList<Image> selectedImagesShareActivity=intent.getParcelableArrayListExtra("selectedImagesShareActivity");
        if(selectedImagesShareActivity!=null){
            int IndexOfi;
            Image image;
            for(Image i:selectedImagesShareActivity){
                IndexOfi=data.get(0).getImageList().indexOf(i);
                image=data.get(0).getImageList().get(IndexOfi);
                image.setSelectionState(true);
                selectedImages.add(image);
            }
            String previewButtonText="("+selectedImages.size()+"/"+MAX_IMAGE_NUMBER+")";
            previewButton.setText(previewButtonText);
            intent.removeExtra("selectedImagesShareActivity");
        }
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
        mToolBar=this.findViewById(R.id.activity_image_albums_tool_bar);
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
        directorySelectorButton=this.findViewById(R.id.switch_dir_button);
        previewButton=this.findViewById(R.id.preview_button);
        previewButton.setOnClickListener(this);
        String previewButtonText="("+selectedImages.size()+"/"+MAX_IMAGE_NUMBER+")";
        previewButton.setText(previewButtonText);
        directorySelectorButton.setOnClickListener(this);
    }
    private void initFolderContainer(){
        FolderContainerBuilder folderContainerBuilder=new FolderContainerBuilder(this);
        folderContainer=folderContainerBuilder.buildImageFolderContainer();
    }
    private void initRecyclerView(){
        albumsRecyclerView=this.findViewById(R.id.albums_recycler_view);
        albumsRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        directorySelectorButton.setText(folderContainer.get(0).getFolderName());
        data.add(folderContainer.get(0));
        onItemClickListener=new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if(position==0){
                        takePhoto();
                    } else {
                        Intent intent=new Intent(getApplicationContext(),PreviewImagesActivity.class);
                        intent.putParcelableArrayListExtra("selectedImages", selectedImages);
                        intent.putParcelableArrayListExtra("imageList",  data.get(0).getImageList());
                        intent.putExtra("imagePosition", position);
                        startActivityForResult(intent,PreviewImagesActivity_REQUEST_CODE);
                    }
                }
        };
        mAlbumsAdapter=new AlbumsAdapter(this,data,selectedImages,onItemClickListener);
        albumsRecyclerView.setAdapter(mAlbumsAdapter);

    }
    @Override
    public void onCheckBoxChanged(CheckBox checkBox, int imagePosition) {

        if(!checkBox.isChecked()){
            if(selectedImages.size()<MAX_IMAGE_NUMBER){
                //position is the position of the grid recyclerView, position minus 1 is the position for the
                //corresponding image in the image folder (data)
                checkBox.setChecked(true);
                selectedImages.add(data.get(0).getImageList().get(imagePosition));
                data.get(0).getImageList().get(imagePosition).setSelectionState(true);
                mAlbumsAdapter.notifyDataSetChanged();
            } else {Toast.makeText(this,R.string.maximum_number_of_image_reminder,Toast.LENGTH_SHORT).show();}
        } else {
            //position is the position of the grid recyclerView, position minus 1 is the position for the
            //corresponding image in the image folder (data)
            checkBox.setChecked(false);
            selectedImages.remove(data.get(0).getImageList().get(imagePosition));
            data.get(0).getImageList().get(imagePosition).setSelectionState(false);
            mAlbumsAdapter.notifyDataSetChanged();
        }
        String previewButtonText="("+selectedImages.size()+"/9)";
        previewButton.setText(previewButtonText);
    }
    @Override
    public void onClick(View v) {
            if(v.getId()==R.id.switch_dir_button){

                if(directorySelectorBottomSheet==null) {
                directorySelectorBottomSheet = new DirectorySelectorBottomSheet(this,folderContainer);
                }
                if(!directorySelectorBottomSheet.isShowing()){
                    directorySelectorBottomSheet.show();
                }
            } else if (v.getId()==R.id.preview_button){
                if(selectedImages.size()>0){
                    Intent intent=new Intent(getApplicationContext(),PreviewSelectedImagesActivity.class);
                    intent.putParcelableArrayListExtra("selectedImages", selectedImages);
                    startActivityForResult(intent,PreviewSelectedImagesActivity_REQUEST_CODE);
                }
            }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.activity_preview_tool_bar_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
    public void onDirectorySelect(ImageFolder imageFolder) {
        directorySelectorButton.setText(imageFolder.getFolderName());
        data.clear();
        data.add(imageFolder);
        mAlbumsAdapter.notifyDataSetChanged();
    }
    private void takePhoto(){
        Intent CameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
        if (CameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraImageFile = createImageFile();
            if (cameraImageFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ///7.0以上要通过FileProvider将File转化为Uri
                    cameraImageUri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", cameraImageFile);
                } else {
                    //7.0以下则直接使用Uri的fromFile方法将File转化为Uri
                    cameraImageUri = Uri.fromFile(cameraImageFile);
                }
                //将用于输出的文件Uri传递给相机
                if(cameraImageFile!=null){
                CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                //启动相机
                startActivityForResult(CameraIntent, CAMERA_REQUEST_CODE);
                }
            }
        }
    }
    private File createImageFile() {
        Locale current = getResources().getConfiguration().locale;
        SimpleDateFormat timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        timeStamp.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));
        String time=timeStamp.format(new Date());
        String imageFileName = "IMG_" + time + "_";
        File storageDir =new File( Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");

        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    galleryAddPictures();
                }
                break;
            case PreviewImagesActivity_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    if (data != null) {
                        ArrayList<Integer> selectedImageIndex=data.getIntegerArrayListExtra("selectedImageIndex");
                        ArrayList<Integer> unselectedImageIndex=data.getIntegerArrayListExtra("unselectedImageIndex");
                        if(!selectedImageIndex.isEmpty()){
                            for(int i:selectedImageIndex){
                                Image image=this.data.get(0).getImageList().get(i);
                                if(!image.getSelectionState()){
                                    image.setSelectionState(true);
                                }
                                if(!selectedImages.contains(image)){
                                    selectedImages.add(image);
                                }
                            }
                        }
                        if(!unselectedImageIndex.isEmpty()){
                            for(int i:unselectedImageIndex){
                                Image image=this.data.get(0).getImageList().get(i);
                                if(image.getSelectionState()){
                                    image.setSelectionState(false);
                                }
                                selectedImages.remove(image);
                            }
                        }
                        mAlbumsAdapter.notifyDataSetChanged();
                        String previewButtonText = "(" + selectedImages.size() + "/9)";
                        previewButton.setText(previewButtonText);
                    }
                }
                break;
            case PreviewSelectedImagesActivity_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        ArrayList<Image> selectedImagesFromPreview = data.getParcelableArrayListExtra("selectedImages");
                        ArrayList<Image> mirrorOfSelectedImages = new ArrayList<>(selectedImages);
                        for(Image i:mirrorOfSelectedImages){
                            if(selectedImagesFromPreview.isEmpty()||!selectedImagesFromPreview.contains(i)){
                                selectedImages.get(selectedImages.indexOf(i)).setSelectionState(false);
                                selectedImages.remove(i);
                            }
                        }
                        mAlbumsAdapter.notifyDataSetChanged();
                        String previewButtonText = "(" + selectedImages.size() + "/9)";
                        previewButton.setText(previewButtonText);
                    }
                }
                break;
        }

    }
    /**
     * 将拍的照片添加到相册
     */
    private void galleryAddPictures() {
        try{
            Log.d("123456", "galleryAddPictures:1 ");
            Uri uri=Uri.fromFile(cameraImageFile);
            Log.d("123456", "galleryAddPictures:2 ");
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            sendBroadcast(mediaScanIntent);
        }
        catch (NullPointerException e){
            Log.d("123456", "galleryAddPictures: nullPointer "+e);
        }
    }
}