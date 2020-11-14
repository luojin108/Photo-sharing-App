package com.example.mytips.homefragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mytips.R;
import com.example.mytips.RecyclerViewGridAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragmentChild3 extends Fragment {
    private RecyclerView mRecyclerView;
    public HomeFragmentChild3() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home_child3, container, false);
        mRecyclerView=view.findViewById(R.id.home_child3_recycler_view);
        return  view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerView();
    }

    private void initRecyclerView(){
        String[] imageUrlList = new String[]{
                "https://yt3.ggpht.com/a/AGF-l792rbMETCGJrbO724KRtSHTgtRjYmZ04HBbqw=s900-c-k-c0xffffffff-no-rj-mo",
                "https://i.picsum.photos/id/237/200/300.jpg",
                "https://i.picsum.photos/id/0/5616/3744.jpg",
                "https://i.picsum.photos/id/10/2500/1667.jpg",
                "https://i.picsum.photos/id/1015/6000/4000.jpg",
                "https://i.picsum.photos/id/103/2592/1936.jpg",
                "https://i.picsum.photos/id/1039/6945/4635.jpg",
                "https://i.picsum.photos/id/1059/7360/4912.jpg",
                "https://i.picsum.photos/id/110/5616/3744.jpg",
                "https://i.picsum.photos/id/13/2500/1667.jpg",
                "https://i.picsum.photos/id/134/4928/3264.jpg",
                "https://i.picsum.photos/id/155/3264/2176.jpg"};
        ArrayList<String> imageUriList2= new ArrayList<>(Arrays.asList(imageUrlList));
        String[] titleList= new String[]{
                "I am HaoGuaGua, GuaGua",
                "This is a dog",
                "This is a computer",
                "This is a forest",
                "This is a gorge",
                "A person sitting down",
                "beautiful valley",
                "home",
                "what",
                "beautiful",
                "wow",
                "Who am I? What am I going to do?"};
        ArrayList<String> titleList2= new ArrayList<>(Arrays.asList(titleList));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //mRecyclerView.setAdapter(new RecyclerViewGridAdapter(getContext(), imageUriList2,titleList2));
    }
}
