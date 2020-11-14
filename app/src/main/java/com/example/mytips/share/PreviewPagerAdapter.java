package com.example.mytips.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.mytips.R;
import com.example.mytips.model.Image;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class PreviewPagerAdapter extends PagerAdapter implements View.OnClickListener {
    private Context context;
    private final ArrayList<Image> imageList;


    public PreviewPagerAdapter(Context context, ArrayList<Image> imageList) {
        this.context=context;
        this.imageList=imageList;
    }
    @Override
    public int getCount() {
        return imageList.size();
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView photoView=new PhotoView(context);
        Glide.with(context)
                .load(imageList.get(position).getImagePath())
                .into(photoView);
        photoView.setOnClickListener(this);
        container.addView(photoView);
        return photoView;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((PhotoView)object);
    }

    @Override
    public void onClick(View v) {
        ((OnPhotoClickListener)context).onPhotoClick();
    }

    public interface OnPhotoClickListener{
        void onPhotoClick();
    }

}
