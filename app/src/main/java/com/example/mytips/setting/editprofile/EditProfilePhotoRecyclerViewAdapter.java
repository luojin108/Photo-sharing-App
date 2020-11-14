package com.example.mytips.setting.editprofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mytips.R;
import com.example.mytips.model.ImageFolder;
import com.example.mytips.utils.OnItemClickListener;
import com.theophrast.ui.widget.SquareImageView;

import java.util.ArrayList;

public class EditProfilePhotoRecyclerViewAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;
    private ArrayList<ImageFolder> data;

    public EditProfilePhotoRecyclerViewAdapter(Context context, ArrayList<ImageFolder> data, OnItemClickListener onItemClickListener) {
        this.mContext=context;
        this.data=data;
        this.onItemClickListener = onItemClickListener;
        this.layoutInflater=LayoutInflater.from(mContext);
    }
    @Override
    public int getItemCount() {
        return data.get(0).getImageList().size();
    }
    @Override
    public int getItemViewType(int position) {
        return 0;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new EditProfilePhotoRecyclerViewAdapter.ImageHolder(layoutInflater.inflate(R.layout.activity_edit_profile_photo_single_item,parent,false),
                    mContext, data,onItemClickListener);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((EditProfilePhotoRecyclerViewAdapter.ImageHolder)holder).setImage(position);

    }
    private static class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SquareImageView imageView;
        private OnItemClickListener onItemClickListener;
        private Context mContext;
        private ArrayList<ImageFolder> data;

        public ImageHolder(@NonNull View itemView, Context context, ArrayList<ImageFolder> data, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.mContext = context;
            this.data = data;
            imageView = itemView.findViewById(R.id.activity_edit_profile_photo_single_image);
            this.onItemClickListener = onItemClickListener;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == imageView) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
        public void setImage ( int imagePosition){
            Glide.with(mContext)
                    .load(data.get(0).getImageList().get(imagePosition).getImagePath())
                    .into(imageView);
        }

    }
}
