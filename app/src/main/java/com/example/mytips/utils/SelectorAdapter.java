package com.example.mytips.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mytips.R;
import com.example.mytips.model.ImageFolder;

import java.util.ArrayList;

public class SelectorAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private Context mContext;
    private ArrayList<ImageFolder> folderContainer;
    private LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;
    private static AppCompatRadioButton lastCheckedButton=null;
    private boolean alreadyExecuted=false;



    public SelectorAdapter(Context context, ArrayList<ImageFolder> folderContainer, OnItemClickListener onItemClickListener) {
        this.onItemClickListener=onItemClickListener;
        this.mContext=context;
        this.layoutInflater=LayoutInflater.from(mContext);
        this.folderContainer=folderContainer;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OptionViewHolder(layoutInflater.inflate(R.layout.directory_seletor_single_item,parent,false)
                , mContext,folderContainer);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((OptionViewHolder)holder).setData(position);
        AppCompatRadioButton checkButton=((OptionViewHolder) holder).checkButton;
        checkButton.setTag(position);

                if(!alreadyExecuted){
                    if(position==0) {
                        checkButton.setChecked(true);
                        lastCheckedButton=checkButton;
                    }
                    alreadyExecuted=true;
                }
        ((OptionViewHolder) holder).wrapper.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        AppCompatRadioButton checkButton=v.findViewById(R.id.directory_selector_preview_check);
        checkButton.setChecked(true);
        if(lastCheckedButton.getTag()!=checkButton.getTag()){
            lastCheckedButton.setChecked(false);
        }
        lastCheckedButton=checkButton;
        onItemClickListener.onItemClick(v,(int)checkButton.getTag());
    }
    @Override
    public int getItemCount() {
        return folderContainer.size();
    }

    private static class OptionViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        private ImageView preViewImage;
        private TextView directoryTitleView;
        private Context mContext;
        private AppCompatRadioButton checkButton;
        private ArrayList<ImageFolder> folderContainer;
        private RelativeLayout wrapper;

     public OptionViewHolder(@NonNull View itemView, Context context, ArrayList<ImageFolder> folderContainer) {
         super(itemView);
         this.mContext=context;
         this.folderContainer=folderContainer;
         this.preViewImage=itemView.findViewById(R.id.directory_selector_preview_image_view);
         this.wrapper=itemView.findViewById(R.id.directory_selector_single_item_rl);
         this.directoryTitleView=itemView.findViewById(R.id.directory_selector_preview_title);
         this.checkButton=itemView.findViewById(R.id.directory_selector_preview_check);
     }

     private void setData(int position){
         String directoryTitle="("+folderContainer.get(position).getImageList().size()+")"+
                 " "+folderContainer.get(position).getFolderName();
         directoryTitleView.setText(directoryTitle);
         Glide.with(mContext)
                 .load(folderContainer.get(position).getAlbumCover())
                 .into(preViewImage);
     }
 }

}
