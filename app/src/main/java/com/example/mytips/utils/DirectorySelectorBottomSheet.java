package com.example.mytips.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytips.R;
import com.example.mytips.model.ImageFolder;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class DirectorySelectorBottomSheet extends BottomSheetDialog {
    private RecyclerView directoryOptions;
    private ArrayList<ImageFolder> folderContainer;
    private SelectorAdapter mSelectorAdapter;
    private Context mContext;
    public DirectorySelectorBottomSheet(@NonNull Context context, ArrayList<ImageFolder> folderContainer) {
        super(context);
        this.mContext=context;
        this.folderContainer=folderContainer;
        setContentView(R.layout.dialog_dir_selector);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        directoryOptions=this.findViewById(R.id.dir_selector_rv);
        initRecyclerView();

    }
    private void initRecyclerView(){
        directoryOptions=this.findViewById(R.id.dir_selector_rv);
        directoryOptions.setLayoutManager(new LinearLayoutManager(getContext()));
        mSelectorAdapter=new SelectorAdapter(mContext, folderContainer, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //View view is not used
                ((DirectorySelectorBottomSheet.OnDirectorySelectListener)mContext)
                        .onDirectorySelect(folderContainer.get(position));
                dismiss();
            }
        });
        directoryOptions.setAdapter(mSelectorAdapter);
    }
    public interface OnDirectorySelectListener{
        void onDirectorySelect(ImageFolder imageFolder );
    }
}
