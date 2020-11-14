package com.example.mytips.post;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mytips.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class PostImageAdapter extends SliderViewAdapter<PostImageAdapter.PostImageViewHolder> {
    private Context context;
    private ArrayList<String> postImageList;

    public PostImageAdapter(Context context, ArrayList<String> postImageList) {
        this.context=context;
        this.postImageList=postImageList;
    }
    @Override
    public int getCount() {
        return postImageList.size();
    }
    @Override
    public PostImageViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.post_image_item, parent, false);
        return new PostImageViewHolder(view);
    }
    @Override
    public void onBindViewHolder(PostImageViewHolder viewHolder, int position) {
        Glide.with(context)
                .load(postImageList.get(position))
                .into(viewHolder.imageView);

    }
    static class PostImageViewHolder extends SliderViewAdapter.ViewHolder{
        private ImageView imageView;
        public PostImageViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.post_image_item);
        }
    }
}
