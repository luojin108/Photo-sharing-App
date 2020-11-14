package com.example.mytips.share;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytips.R;
import com.example.mytips.model.Image;

import java.util.ArrayList;

public class ShareImageAdapter extends RecyclerView.Adapter {
    private ArrayList<Image> imageArrayList;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public ShareImageAdapter(ArrayList<Image> imageArrayList,Context context) {
        this.imageArrayList=imageArrayList;
        this.mContext=context;
        this.layoutInflater=LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.share_image_display_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(20));
        Glide.with(mContext)
                .load(imageArrayList.get(position).getImagePath())
                .apply(requestOptions)
                .into(((ViewHolder)holder).imageView);

    }

    @Override
    public int getItemCount() {

        return imageArrayList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{
         private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView=itemView.findViewById(R.id.recycler_view_image_display_layout);
        }
    }
}
