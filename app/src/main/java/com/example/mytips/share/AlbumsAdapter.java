package com.example.mytips.share;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mytips.R;
import com.example.mytips.model.Image;
import com.example.mytips.model.ImageFolder;
import com.example.mytips.utils.OnItemClickListener;
import com.theophrast.ui.widget.SquareImageView;

import java.util.ArrayList;

public class AlbumsAdapter extends RecyclerView.Adapter{
    private int ADD_BUTTON=0;
    private int PHOTO=1;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;
    private ArrayList<ImageFolder> data;
    private ArrayList<Image> selectedImages;


    public AlbumsAdapter(Context context, ArrayList<ImageFolder> data, ArrayList<Image> selectedImages, OnItemClickListener onItemClickListener) {
        this.mContext=context;
        this.data=data;
        this.selectedImages=selectedImages;
        this.onItemClickListener = onItemClickListener;
        this.layoutInflater=LayoutInflater.from(mContext);
    }
    @Override
    public int getItemCount() {
        return data.get(0).getImageList().size()+1;
    }
    @Override
    public int getItemViewType(int position) {
        if(position==0){return ADD_BUTTON;}
        else {return PHOTO;}
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ADD_BUTTON){
            return new AddPhotoButtonImageHolder(layoutInflater.inflate(R.layout.albums_add_photo_item,parent,false),
                    onItemClickListener);
        } else {
            return new ImageHolder(layoutInflater.inflate(R.layout.activity_image_albums_single_item,parent,false),
                    mContext, data,selectedImages, onItemClickListener);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==PHOTO){
        ((ImageHolder)holder).setImageAndCheckBox(position-1);
        }
    }
    private static class AddPhotoButtonImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView addPhotoButton;
        private OnItemClickListener onItemClickListener;
        
        public AddPhotoButtonImageHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            addPhotoButton=itemView.findViewById(R.id.albums_add_photo_button);
            this.onItemClickListener = onItemClickListener;
            addPhotoButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v,getAdapterPosition());
        }
    }
    private static class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SquareImageView imageView;
        private AppCompatCheckBox checkBox;
        private View checkBoxMask;
        private OnItemClickListener onItemClickListener;
        private Context mContext;
        private ArrayList<ImageFolder> data;
        private ArrayList<Image> selectedImages;


        public ImageHolder(@NonNull View itemView, Context context, ArrayList<ImageFolder> data,ArrayList<Image> selectedImages, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.mContext=context;
            this.data=data;
            this.selectedImages=selectedImages;
            imageView=itemView.findViewById(R.id.activity_image_albums_single_image);
            checkBox=itemView.findViewById(R.id.check_box);
            checkBoxMask=itemView.findViewById(R.id.check_box_mask);
            this.onItemClickListener = onItemClickListener;
            imageView.setOnClickListener(this);
            checkBoxMask.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if(v==imageView){
                onItemClickListener.onItemClick(v,getAdapterPosition());
            } else if (v==checkBoxMask){
                ((CheckBoxNotifier)mContext).onCheckBoxChanged(checkBox, getAdapterPosition()-1);
            }
        }
        public void setImageAndCheckBox(int imagePosition){
            if(data.get(0).getImageList().get(imagePosition).getSelectionState()){
                if(!checkBox.isChecked()){
                    checkBox.setChecked(true);
                }
            }else {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                }
            }
            Glide.with(mContext)
                    .load(data.get(0).getImageList().get(imagePosition).getImagePath())
                    .into(imageView);
        }
    }
public interface CheckBoxNotifier{
        void onCheckBoxChanged(CheckBox checkBox, int imagePosition);
}

}
