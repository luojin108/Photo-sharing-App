package com.example.mytips.myfragment;

import android.os.Bundle;
import android.util.Log;
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
import com.example.mytips.model.ImagePosts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFragmentChild2 extends Fragment implements RecyclerViewGridAdapter.OnPostClickListener {
    private String TAG="MyfragmentChild2";
    private RecyclerView mRecyclerView;
    private RecyclerViewGridAdapter recyclerViewGridAdapter;
    private RecyclerViewGridAdapter.BottomLoadingIconHolder bottomLoadingIconHolder;

    //Regarding posts
    private int previousUserImagePostListSize=0;
    private Long theLatestPostTimeStamp;
    private Boolean isLoading=false;
    private ArrayList<ImagePosts> userImagePostsList=new ArrayList<>();
    //fibreBase
    private String userID;
    private DatabaseReference databaseReference;
    private Query userLikedPostsNodeQuery;
    public MyFragmentChild2(String userID, DatabaseReference databaseReference) {
        this.userID=userID;
        this.databaseReference=databaseReference;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_child2, container, false);
        initWidgets(view);
        return  view;
    }
    private void initWidgets(View view){
        mRecyclerView=view.findViewById(R.id.my_child2_recycler_view);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDatabaseRefToSingleValueEventListener();
    }
    //............The followings are the components needed to populate the imagePostList of user
    private void setDatabaseRefToSingleValueEventListener(){
        userLikedPostsNodeQuery=databaseReference.child(getActivity().getString(R.string.user_posts_node)).child(userID)
                .child("user_liked_posts").orderByChild("time_stamp");

        userLikedPostsNodeQuery.limitToFirst(12).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                populateUserImagePostsList(dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    //non UI thread
    private void populateUserImagePostsList(DataSnapshot dataSnapshot){
        if(dataSnapshot.exists()){
            for(DataSnapshot ds:dataSnapshot.getChildren()){
                try{
                    ImagePosts imagePosts=new ImagePosts();
                    imagePosts.setPost_id(ds.getValue(ImagePosts.class).getPost_id());
                    imagePosts.setTime_stamp(ds.getValue(ImagePosts.class).getTime_stamp());
                    imagePosts.setUser_id(ds.getValue(ImagePosts.class).getUser_id());
                    userImagePostsList.add(imagePosts);
                } catch (NullPointerException e){
                    //
                }
            }
        }
        if(userImagePostsList.size()!=0){
            theLatestPostTimeStamp=userImagePostsList.get(userImagePostsList.size()-1).getTime_stamp();
        }
        previousUserImagePostListSize=userImagePostsList.size();
        initRecyclerView(userImagePostsList);
    }
    //............
    private void initRecyclerView(final ArrayList<ImagePosts> imagePostsList){
        final StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerViewGridAdapter=new RecyclerViewGridAdapter(getContext(), imagePostsList,userID,databaseReference, this);
        mRecyclerView.setAdapter(recyclerViewGridAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager staggeredGridLayoutManager1=(StaggeredGridLayoutManager)mRecyclerView.getLayoutManager();
                if(!isLoading){
                    int[] positions=new int[staggeredGridLayoutManager1.getSpanCount()];
                    int[] lastCompletelyVisibleItemPositions=staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(positions);
                    if(lastCompletelyVisibleItemPositions[1] == imagePostsList.size()){
                        isLoading=true;
                        bottomLoadingIconHolder=(RecyclerViewGridAdapter.BottomLoadingIconHolder)
                                mRecyclerView.findViewHolderForAdapterPosition(imagePostsList.size());
                        if(bottomLoadingIconHolder!=null){
                            bottomLoadingIconHolder.enableLoadingIcon();
                        }
                        loadMorePosts();
                    }

                }
            }
        });
    }
    //............load more posts when scrolling down
    private void loadMorePosts(){
        if(theLatestPostTimeStamp!=null){
            userLikedPostsNodeQuery.startAt(theLatestPostTimeStamp).limitToFirst(13).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    addMorePostsIntoTheList(dataSnapshot);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    private void addMorePostsIntoTheList(DataSnapshot dataSnapshot){
        if(dataSnapshot.exists()){
            for(DataSnapshot ds:dataSnapshot.getChildren()){
                if(ds.getValue(ImagePosts.class).getTime_stamp()!=theLatestPostTimeStamp){
                    try{
                        ImagePosts imagePosts=new ImagePosts();
                        imagePosts.setPost_id(ds.getValue(ImagePosts.class).getPost_id());
                        imagePosts.setTime_stamp(ds.getValue(ImagePosts.class).getTime_stamp());
                        imagePosts.setUser_id(ds.getValue(ImagePosts.class).getUser_id());
                        userImagePostsList.add(imagePosts);
                    } catch (NullPointerException e){
                        //
                    }
                }
            }
            theLatestPostTimeStamp=userImagePostsList.get(userImagePostsList.size()-1).getTime_stamp();
        }
        if(previousUserImagePostListSize<userImagePostsList.size()){
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerViewGridAdapter.notifyDataSetChanged();
                    isLoading=false;
                    bottomLoadingIconHolder.disableLoadingIcon();
                }
            },1000);
            previousUserImagePostListSize=userImagePostsList.size();
        } else {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomLoadingIconHolder.disableLoadingIcon();
                }
            },1000);


        }

    }
    //........................................
    @Override
    public void onPostClick(int position) {

    }

}
