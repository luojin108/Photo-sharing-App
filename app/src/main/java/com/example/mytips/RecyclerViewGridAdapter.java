package com.example.mytips;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.mytips.model.ImagePosts;
import com.example.mytips.widgets.SquareCircleImageView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewGridAdapter extends RecyclerView.Adapter {
    private String TAG=" RecyclerViewGridAdapter";
    private Context context;
    private ArrayList<ImagePosts> userImagePostsList;
    private String userID;
    private DatabaseReference databaseReference;
    private OnPostClickListener callBack;
    private final int POST=1;
    private final int LOADING_ICON=2;


    public RecyclerViewGridAdapter(Context context, ArrayList<ImagePosts> userImagePostsList, String userID, DatabaseReference databaseReference, OnPostClickListener callBack) {
        this.context = context;
        this.userImagePostsList = userImagePostsList;
        this.userID=userID;
        this.databaseReference=databaseReference;
        this.callBack=callBack;
    }
    @Override
    public int getItemCount() {
        return userImagePostsList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        //check if it is the last item in the list
        if(position==userImagePostsList.size()){
            return LOADING_ICON;
        } else return POST;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        if(viewType==POST){
            View view=layoutInflater.inflate(R.layout.recycler_view_card_item_layout, parent, false);
            return new PostViewHolder(view, context, userImagePostsList, userID, databaseReference,callBack);
        } else {
            View view=layoutInflater.inflate(R.layout.recycler_view_bottom_loading_icon, parent, false);
            return new BottomLoadingIconHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==POST){
            ((PostViewHolder)holder).likeButton.setVisibility(View.INVISIBLE);
            ((PostViewHolder)holder).getPostDataBasedOnPostId(position);
            ((PostViewHolder)holder).setLinearLayoutToOnPostClickListener();
        } else {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
    }

     private static class PostViewHolder extends RecyclerView.ViewHolder {
        OnPostClickListener callBack;
        LinearLayout linearLayout;
        ImageView imageView;
        TextView titleText,authorNameText,numberOfLikeText;
        SquareCircleImageView squareCircleImageView;
        LikeButton likeButton;
        Context context;
        ArrayList<ImagePosts> userImagePostsList;
        String userID,coverImageHttp,profilePhotoHttp,title,authorName,authorID;
        int numberOflikes;
        DatabaseReference databaseReference;
        PostViewHolder(@NonNull View itemView, Context context, ArrayList<ImagePosts> userImagePostsList, String userID, DatabaseReference databaseReference,OnPostClickListener callBack) {
            super(itemView);
            this.callBack=callBack;
            this.linearLayout=itemView.findViewById(R.id.cv_linear_layout);
            this.imageView=itemView.findViewById(R.id.cv_image_view);
            this.squareCircleImageView=itemView.findViewById(R.id.card_view_circle_image);
            this.authorNameText=itemView.findViewById(R.id.card_view_author_name);
            this.titleText=itemView.findViewById(R.id.cv_text_view);
            this.numberOfLikeText=itemView.findViewById(R.id.card_view_like_number);
            this.likeButton=itemView.findViewById(R.id.card_view_like_button);
            this.userImagePostsList=userImagePostsList;
            this.context=context;
            this.userID=userID;
            this.databaseReference=databaseReference;
        }
        private void setLinearLayoutToOnPostClickListener(){
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OnPostClickListener)callBack).onPostClick(getAdapterPosition());
                }
            });
        }
        private void getPostDataBasedOnPostId( int position)          {
                databaseReference.child(context.getString(R.string.image_posts_node)).child(userImagePostsList.get(position).getPost_id())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("TAG", "onDataChange: "+dataSnapshot.toString());
                                coverImageHttp=dataSnapshot.getValue(ImagePosts.class).getHttp_uri().get(0);
                                profilePhotoHttp=dataSnapshot.getValue(ImagePosts.class).getProfile_photo();
                                title=dataSnapshot.getValue(ImagePosts.class).getTitle();
                                authorName=dataSnapshot.getValue(ImagePosts.class).getUser_name();
                                authorID=dataSnapshot.getValue(ImagePosts.class).getUser_id();
                                if(dataSnapshot.child(context.getResources().getString(R.string.image_posts_node_users_liked)).exists()){
                                    numberOflikes=(int)dataSnapshot.child(context.getResources().getString(R.string.image_posts_node_users_liked)).getChildrenCount();
                                } else {
                                    numberOflikes=0;
                                }
                                setImageAndText(coverImageHttp,profilePhotoHttp,title,authorName,numberOflikes);
                                if(userID!=null){
                                    initLikeButton(numberOflikes,dataSnapshot);
                                    likeButton.setVisibility(View.VISIBLE);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

       }
        private void setImageAndText(String coverImageHttp,String profilePhotoHttp,String title,String authorName, int numberOflikes){
               Glide.with(context)
                       .load(coverImageHttp).centerCrop()
                       .into(imageView);
               titleText.setText(title);
               Glide.with(context)
                       .load(profilePhotoHttp).centerCrop()
                       .into(squareCircleImageView);
               authorNameText.setText(authorName);
           if (numberOflikes>0){
               if(numberOfLikeText.getText()==""){
                   numberOfLikeText.setText(Integer.toString(numberOflikes));
               }
           } else {
               numberOfLikeText.setText("");
           }
       }
       //..............................................................
        private void initLikeButton(final int numberOfLikesInt, DataSnapshot dataSnapshot){
            if(dataSnapshot.child(context.getResources().getString(R.string.image_posts_node_users_liked)).exists()){
                if(dataSnapshot.child(context.getResources().getString(R.string.image_posts_node_users_liked)).child(userID).exists()){
                    likeButton.setLiked(true);
                } else {likeButton.setLiked(false);}
            } else {likeButton.setLiked(false);}
            likeButton.setOnLikeListener(new OnLikeListener() {
                //note: these object attributes are needed, in case that, user clicks the like button, the like number can change
                //in real time before the database is updated.
                private int newNumberOfLikeInt;
                private int liked=1;
                private int unliked=2;
                @Override
                public void liked(LikeButton likeButton) {
                    if(liked==1 && unliked==2){
                        newNumberOfLikeInt=numberOfLikesInt+1;
                        //set liked=11, such that we know that the like button has been pressed, no matter whether the database is updated
                        liked=11;
                    }else {
                        newNumberOfLikeInt=newNumberOfLikeInt+1;
                    }
                    numberOfLikeText.setText(Integer.toString(newNumberOfLikeInt));
                    addToUserLikedForThePost(databaseReference);
                    addToLikedPostOfDatabase(databaseReference);
                    addToTotalUsersLikedForTheAuthor(databaseReference);
                    updateLikeNumberInDatabase(databaseReference,newNumberOfLikeInt);
                }
                @Override
                public void unLiked(LikeButton likeButton) {
                    if(liked==1 && unliked==2){
                        newNumberOfLikeInt=numberOfLikesInt-1;
                        //set unliked=22, such that we know that the like button has been pressed, no matter whether the database is updated
                        unliked=22;
                    } else {
                        newNumberOfLikeInt=newNumberOfLikeInt-1;
                    }
                    if(newNumberOfLikeInt==0){
                        numberOfLikeText.setText("");
                    } else numberOfLikeText.setText(Integer.toString(newNumberOfLikeInt));
                    removeFromUserLikedForThePost(databaseReference);
                    removeFromLikedPostOfDatabase(databaseReference);
                    removeFromTotalUsersLikedForTheAuthor(databaseReference);
                    updateLikeNumberInDatabase(databaseReference,newNumberOfLikeInt);
                }
            });
        }
        private void updateLikeNumberInDatabase(DatabaseReference databaseReference, int newNumberOfLikeInt ){
            //increase like number for the post in the public image post node in the database
            databaseReference.child(context.getString(R.string.image_posts_node)).child(userImagePostsList.get(getAdapterPosition()).getPost_id())
                    .child("number_of_like").setValue(newNumberOfLikeInt);
        }
        private void addToUserLikedForThePost(DatabaseReference databaseReference){
            //add the user id (as new node with value 1) to the post's "users_liked" node in the public image post node in the database
            databaseReference.child(context.getString(R.string.image_posts_node)).child(userImagePostsList.get(getAdapterPosition()).getPost_id())
                    .child("users_liked").child(userID).setValue(1);
        }
        private void removeFromUserLikedForThePost(DatabaseReference databaseReference){
            //remove the user id (as new node with value 1) to the post's "users_liked" node in the public image post node
            databaseReference.child(context.getString(R.string.image_posts_node)).child(userImagePostsList.get(getAdapterPosition()).getPost_id())
                    .child("users_liked").child(userID).removeValue();
        }
        private void addToTotalUsersLikedForTheAuthor(DatabaseReference databaseReference){
            //add the user id (as new node with value 1) to user_account_setting -> userID -> total_users_liked -> post_id
            databaseReference.child(context.getString(R.string.child_user_account_setting)).child(authorID)
                    .child("total_users_liked").child(userImagePostsList.get(getAdapterPosition()).getPost_id()+"_"+userID).setValue(1);

        }
        private void removeFromTotalUsersLikedForTheAuthor(DatabaseReference databaseReference){
            //remove the user id (as new node with value 1) to user_account_setting -> userID -> total_users_liked -> post_id
            databaseReference.child(context.getString(R.string.child_user_account_setting)).child(authorID)
                    .child("total_users_liked").child(userImagePostsList.get(getAdapterPosition()).getPost_id()+"_"+userID).removeValue();

        }
        private void addToLikedPostOfDatabase(DatabaseReference databaseReference){
            //add the post id (with child post id and time stamp) to the node user_post -> user ID -> user_liked posts
            databaseReference.child(context.getString(R.string.user_posts_node)).child(userID).child("user_liked_posts")
                    .child(userImagePostsList.get(getAdapterPosition()).getPost_id()).child("post_id")
                    .setValue(userImagePostsList.get(getAdapterPosition()).getPost_id());
            databaseReference.child(context.getString(R.string.user_posts_node)).child(userID).child("user_liked_posts")
                    .child(userImagePostsList.get(getAdapterPosition()).getPost_id()).child("time_stamp")
                    .setValue(-1*new Date().getTime());
        }
        private void removeFromLikedPostOfDatabase(DatabaseReference databaseReference){
            //remove the post id (with value 1) in the node user_post -> user ID -> user_liked posts
            databaseReference.child(context.getString(R.string.user_posts_node)).child(userID).child("user_liked_posts")
                    .child(userImagePostsList.get(getAdapterPosition()).getPost_id()).removeValue();
        }

         //..............................................................^^^

    }
    public static class BottomLoadingIconHolder extends RecyclerView.ViewHolder {
        private SpinKitView bottomLoadingIcon;

        BottomLoadingIconHolder(@NonNull View itemView) {
            super(itemView);
            this.bottomLoadingIcon = itemView.findViewById(R.id.bottom_loading_icon);
        }
        public void enableLoadingIcon(){
                bottomLoadingIcon.setVisibility(View.VISIBLE);
        }
        public void disableLoadingIcon(){
            bottomLoadingIcon.setVisibility(View.INVISIBLE);
        }
    }
    public interface OnPostClickListener{
        void onPostClick(int position);

    }



}
