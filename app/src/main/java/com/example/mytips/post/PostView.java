package com.example.mytips.post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.example.mytips.R;
import com.example.mytips.model.Comment;
import com.example.mytips.model.Reply;
import com.example.mytips.utils.DisplayDimensionGetter;
import com.example.mytips.utils.FireBaseMethods;
import com.example.mytips.widgets.SquareCircleImageView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.smarteist.autoimageslider.SliderView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class PostView extends AppCompatActivity implements View.OnClickListener, CommentExpandAdapter.OnCommentClickListener {
    private static final String TAG="PostView";
    //for sending reply
    private String commentPosition;// this is the key for inputContentHashMap to get the previously input text back.
    private String commentIdForReply;
    private String parentCommentID; // this is to indicate whether the reply is to the main comment or another reply.
    private static String ADD_A_COMMENT="add a comment";
    private static int FROM_ADD_A_COMMENT=1;
    private static int FROM_REPLY=2;
    private int originForCommentWindow=0;
    private HashMap<String,String> inputContentHashMap=new HashMap<>();
    //.......
    private SpinKitView progressBar;
    private String postID,origin,authorID;
    private ArrayList<String> postImageHttpUrlList=new ArrayList<>();
    private ArrayList<Float> postImageHeightToWidthRatioList=new ArrayList<>();
    private ArrayList<Comment> commentList=new ArrayList<>();
    private int sliderMaxHeight,sliderMinHeight,sliderWidth,sliderHeight,numberOflikes;
    //widgets
    private Toolbar mToolBar;
    private NestedScrollView nestedScrollView;
    private FrameLayout sliderContainer;
    private TextView description,title,datePosted,authorName,commentTextView,numberOfLikeText;
    private SquareCircleImageView authorProfilePhoto;
    private SliderView postImageSlider;
    private RelativeLayout parent;
    private PopupWindow popupWindow;
    private LikeButton likeButton;
    private MyExpandableListView expandableListView;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseRef;
    private String userID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        Intent intent=getIntent();
        origin=intent.getStringExtra("origin");
        postID=intent.getStringExtra("postID");
        authorID=intent.getStringExtra("AuthorID");
        initSliderMaxAndMinHeight();
        initWidgets();
        initToolBar();
        initFireBaseComponents();
        setDatabaseRefToValueEventListener();
    }
    private void initSliderMaxAndMinHeight(){
        DisplayDimensionGetter displayDimensionGetter=new DisplayDimensionGetter();
        sliderMaxHeight=(int)((displayDimensionGetter.getDisplayLengthInDp(this)-100)/10*9);
        sliderMinHeight=(int)((displayDimensionGetter.getDisplayLengthInDp(this)-100)/5*2);
        sliderWidth=(int)((displayDimensionGetter.getDisplayWidthInDp(this)));
    }
    private void initWidgets(){
        progressBar=findViewById(R.id.activity_post_view_progress_bar);
        title=this.findViewById(R.id.activity_post_view__title_text);
        description=this.findViewById(R.id.activity_post_view__description_text);
        datePosted=this.findViewById(R.id.activity_post_view__time_posted);
        authorProfilePhoto=this.findViewById(R.id.activity_post_view_author_profile_photo);
        authorName=this.findViewById(R.id.activity_post_view_author_name);
        postImageSlider=this.findViewById(R.id.activity_post_view_imageSlider);
        sliderContainer=this.findViewById(R.id.activity_post_view_frame_layout);
        nestedScrollView=this.findViewById(R.id.activity_post_view_nested_scroll_view);
        nestedScrollView.setVisibility(View.INVISIBLE);
        commentTextView=this.findViewById(R.id.activity_post_view_comment_box);
        commentTextView.setOnClickListener(this);
        parent=this.findViewById(R.id.activity_post_view_parent);
        numberOfLikeText=this.findViewById(R.id.activity_post_view_like_number);
        likeButton=this.findViewById(R.id.activity_post_view_like_button);
        likeButton.setVisibility(View.INVISIBLE);
        expandableListView=this.findViewById(R.id.activity_post_view_comment_expandable_list_view);
    }
    private void initToolBar() {
        mToolBar=this.findViewById(R.id.activity_post_view_tool_bar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //initialize firebase components
    private void initFireBaseComponents(){
        mAuth = FirebaseAuth.getInstance();
        userID=mAuth.getUid();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseRef=mFirebaseDatabase.getReference();
    }
    // ............The followings are the components needed for populating widgets with data from database
    private void setDatabaseRefToValueEventListener(){
        mDatabaseRef.child(getString(R.string.image_posts_node)).child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                populatePostContent(dataSnapshot);
                determineSliderHeightAndInitSliderAdapter(postImageHeightToWidthRatioList,postImageHttpUrlList);
                nestedScrollView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabaseRef.child(getString(R.string.child_user_account_setting)).child(authorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                populateAuthorInfo(dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabaseRef.child(getString(R.string.image_posts_node)).child(postID)
                .child(getString(R.string.comments_node)).orderByChild(getString(R.string.time_stamp))
                .limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                populateCommentList(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void populateCommentList(DataSnapshot dataSnapshot){
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            Comment comment=new Comment();

            comment.setUser_id(ds.child(getString(R.string.user_id)).getValue().toString());
            comment.setComment_id(ds.child(getString(R.string.comments_node_comment_id)).getValue().toString());
            comment.setComment_text(ds.child(getString(R.string.comments_node_comment_text)).getValue().toString());
            comment.setTime_stamp(Long.parseLong(ds.child(getString(R.string.comments_node_time_stamp)).getValue().toString()));
            comment.setPost_id((ds.child(getString(R.string.comments_node_post_id)).getValue()).toString());

            ArrayList<Reply> replies=new ArrayList<>();
            ArrayList<Reply> hiddenReplies=new ArrayList<>();
            int counter = 0;
             for (DataSnapshot ds2:ds.child(getString(R.string.replies_node)).getChildren()){
                 Reply reply=new Reply();
                 reply.setUser_id(ds2.getValue(Reply.class).getUser_id());
                 reply.setTime_stamp(ds2.getValue(Reply.class).getTime_stamp());
                 reply.setReplied_id(ds2.getValue(Reply.class).getReplied_id());
                 reply.setReply_text(ds2.getValue(Reply.class).getReply_text());
                 reply.setReply_id(ds2.getValue(Reply.class).getReply_id());
                 reply.setParent_id(ds2.getValue(Reply.class).getParent_id());

                 counter ++;
                 if (counter > 1 ){
                     hiddenReplies.add(reply);
                     continue;
                 }
                 replies.add(reply);
             }
           /*  if(!replies.isEmpty()){
                 replies.add(0,new Reply());
             }*/
             comment.setReplies(replies);
             comment.setHiddenReplies(hiddenReplies);
             commentList.add(comment);
        }
        CommentExpandAdapter commentExpandAdapter=new CommentExpandAdapter(this,commentList,userID,mDatabaseRef);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                boolean isExpanded = expandableListView.isGroupExpanded(groupPosition);
                if(isExpanded){
                    expandableListView.collapseGroup(groupPosition);
                }else {
                    expandableListView.expandGroup(groupPosition, true);
                }
                return true;
            }
        });
        expandableListView.setAdapter(commentExpandAdapter);
        for(int i = 0; i<commentList.size(); i++){
            expandableListView.expandGroup(i);
        }
        expandableListView.setGroupIndicator(null);

    }
    private void populatePostContent(DataSnapshot dataSnapshot){
        ArrayList<Long> heightList=new ArrayList<>();
        ArrayList<Long> widthList=new ArrayList<>();
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            if(ds.getKey().equals(getResources().getString(R.string.image_posts_node_title))){
                title.setText(ds.getValue().toString());
            }
            else if (ds.getKey().equals(getResources().getString(R.string.image_posts_node_description))){
                description.setText(ds.getValue().toString());
            }
            else if(ds.getKey().equals(getResources().getString(R.string.image_posts_node_date_posted))){
                long timeDifference=getTimestampDifferenceInMinutes(String.valueOf(ds.getValue()));
                if(timeDifference<60){
                    String string= timeDifference +" "+"min";
                    datePosted.setText(string);
                } else if (1<=(timeDifference/60) && (timeDifference/60)<24){
                    String string=Math.round(timeDifference/60)+" "+"h";
                    datePosted.setText(string);
                } else if(1<=(timeDifference/60/24) && (timeDifference/60/24)<6){
                    String string=Math.round(timeDifference/60/24)+" "+"d";
                    datePosted.setText(string);
                } else if(6<(timeDifference/60/24)){
                    String date=ds.getValue().toString().substring(0,ds.getValue().toString().indexOf("T"));
                    datePosted.setText(date);
                }
            }
            else if(ds.getKey().equals(getResources().getString(R.string.image_posts_node_http_uri))){
                postImageHttpUrlList.addAll((ArrayList)(ds.getValue()));
            }
            else if(ds.getKey().equals(getResources().getString(R.string.image_posts_node_imageHeights))){
                heightList.addAll((ArrayList)(ds.getValue()));
            }
            else if(ds.getKey().equals(getResources().getString(R.string.image_posts_node_imageWidths))){
                widthList.addAll((ArrayList)(ds.getValue()));
            }
        }
        if(heightList.size()==widthList.size()){
            float ratio;
            for(int i=0; i<=heightList.size()-1;i++){
                ratio=(float)heightList.get(i)/widthList.get(i);
                postImageHeightToWidthRatioList.add(ratio);
            }
        }

        //..............................................................
        if(dataSnapshot.child(getResources().getString(R.string.image_posts_node_users_liked)).exists()){
            numberOflikes=(int)dataSnapshot.child(getResources().getString(R.string.image_posts_node_users_liked)).getChildrenCount();
        } else {
            numberOflikes=0;
        }
        if (numberOflikes>0){
            if(numberOfLikeText.getText()==""){
                numberOfLikeText.setText(Integer.toString(numberOflikes));
            }
        } else {
            numberOfLikeText.setText("");
        }
        if(userID!=null){
            initLikeButton(numberOflikes,dataSnapshot);
            likeButton.setVisibility(View.VISIBLE);
        }
        //..............................................................^^^

    }
    //..............................................................^^^

    //likeButton related...............................................
    private void initLikeButton(final int numberOfLikesInt, DataSnapshot dataSnapshot){
        if(dataSnapshot.child(getResources().getString(R.string.image_posts_node_users_liked)).exists()){
            if(dataSnapshot.child(getResources().getString(R.string.image_posts_node_users_liked)).child(userID).exists()){
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
                addToUserLikedForThePost(mDatabaseRef);
                addToLikedPostOfDatabase(mDatabaseRef);
                addToTotalUsersLikedForTheAuthor(mDatabaseRef);
                updateLikeNumberInDatabase(mDatabaseRef,newNumberOfLikeInt);
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
                removeFromUserLikedForThePost(mDatabaseRef);
                removeFromLikedPostOfDatabase(mDatabaseRef);
                removeFromTotalUsersLikedForTheAuthor(mDatabaseRef);
                updateLikeNumberInDatabase(mDatabaseRef,newNumberOfLikeInt);
            }
        });
    }
    private void updateLikeNumberInDatabase(DatabaseReference databaseReference, int newNumberOfLikeInt ){
        //increase like number for the post in the public image post node in the database
        databaseReference.child(getString(R.string.image_posts_node)).child(postID)
                .child("number_of_like").setValue(newNumberOfLikeInt);
    }
    private void addToUserLikedForThePost(DatabaseReference databaseReference){
        //add the user id (as new node with value 1) to the post's "users_liked" node in the public image post node in the database
        databaseReference.child(getString(R.string.image_posts_node)).child(postID)
                .child("users_liked").child(userID).setValue(1);
    }
    private void removeFromUserLikedForThePost(DatabaseReference databaseReference){
        //remove the user id (as new node with value 1) to the post's "users_liked" node in the public image post node
        databaseReference.child(getString(R.string.image_posts_node)).child(postID)
                .child("users_liked").child(userID).removeValue();
    }
    private void addToTotalUsersLikedForTheAuthor(DatabaseReference databaseReference){
        //add the user id (as new node with value 1) to user_account_setting -> userID -> total_users_liked -> post_id
        databaseReference.child(getString(R.string.child_user_account_setting)).child(authorID)
                .child("total_users_liked").child(postID+"_"+userID).setValue(1);

    }
    private void removeFromTotalUsersLikedForTheAuthor(DatabaseReference databaseReference){
        //remove the user id (as new node with value 1) to user_account_setting -> userID -> total_users_liked -> post_id
        databaseReference.child(getString(R.string.child_user_account_setting)).child(authorID)
                .child("total_users_liked").child(postID+"_"+userID).removeValue();

    }
    private void addToLikedPostOfDatabase(DatabaseReference databaseReference){
        //add the post id (with child post id and time stamp) to the node user_post -> user ID -> user_liked posts
        databaseReference.child(getString(R.string.user_posts_node)).child(userID).child("user_liked_posts")
                .child(postID).child("post_id")
                .setValue(postID);
        databaseReference.child(getString(R.string.user_posts_node)).child(userID).child("user_liked_posts")
                .child(postID).child("time_stamp")
                .setValue(-1*new Date().getTime());
    }
    private void removeFromLikedPostOfDatabase(DatabaseReference databaseReference){
        //remove the post id (with value 1) in the node user_post -> user ID -> user_liked posts
        databaseReference.child(getString(R.string.user_posts_node)).child(userID).child("user_liked_posts")
                .child(postID).removeValue();
    }
    //..............................................................^^^
    private void determineSliderHeightAndInitSliderAdapter(ArrayList<Float> postImageHeightToWidthRatioList, ArrayList<String> postImageHttpUrlList){
        float highestHeightToWidthRatioInTheList= Collections.max(postImageHeightToWidthRatioList);
        if((int)(highestHeightToWidthRatioInTheList*sliderWidth)>sliderMaxHeight){
            sliderHeight=sliderMaxHeight;
        }
        else sliderHeight = Math.max((int)(highestHeightToWidthRatioInTheList * sliderWidth), sliderMinHeight);
        RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) sliderContainer.getLayoutParams();
        layoutParams.height=(int)(sliderHeight*this.getResources().getDisplayMetrics().density);
        layoutParams.width=(int)(sliderWidth*this.getResources().getDisplayMetrics().density);
        sliderContainer.setLayoutParams(layoutParams);
        PostImageAdapter postImageAdapter=new PostImageAdapter(this,postImageHttpUrlList);
        postImageSlider.setSliderAdapter(postImageAdapter);
    }
    private void populateAuthorInfo(DataSnapshot dataSnapshot){
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            if(ds.getKey().equals(getResources().getString(R.string.name))){
                authorName.setText(ds.getValue().toString());
            }
            else if(ds.getKey().equals(getResources().getString(R.string.account_setting_node_profile_photo))){
                Glide.with(getApplicationContext())
                        .load(ds.getValue()).centerCrop()
                        .into(authorProfilePhoto);
            }
        }
    }
    //................................................................

    /***
     * Note that the input is formatted date in string
     */
    private long getTimestampDifferenceInMinutes(String datePosted){

        long difference = 0;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyy-MM-dd'T'HH:mm:ss:SSS", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));//google 'android list of timezones'
        Date today = calendar.getTime();

        Date date;
        try{
            date = simpleDateFormat.parse(datePosted);
            Log.d(TAG, "getTimestampDifferenceInMinutes: "+date);
            difference = Math.round(((today.getTime() - date.getTime()) / 1000 / 60));
        }catch (ParseException e){
          //
        }
        return difference;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_post_view_comment_box:
               initCommentWindow(parent,ADD_A_COMMENT,null);
                break;
            case R.id.comment_popup_window_send_button:
                if(originForCommentWindow==FROM_ADD_A_COMMENT){
                    sendComment(ADD_A_COMMENT);
                }else if(originForCommentWindow==FROM_REPLY){
                    sendReply(commentPosition,commentIdForReply,parentCommentID);
                }

                break;
        }
    }

    /***
     *
     * @param parent
     * @param origin origin can be "AddComment" or comment position (for reply)
     */
    private  void initCommentWindow(View parent, final String origin, String commentAuthorName){
        if(origin.equals(ADD_A_COMMENT)){
            originForCommentWindow=FROM_ADD_A_COMMENT;
        } else {
            originForCommentWindow=FROM_REPLY;
        }
        Log.d(TAG, "initCommentWindow: "+originForCommentWindow);
        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.comment_popup_window,null);
        popupWindow=new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM,0,0);
        final EditText editText=view.findViewById(R.id.comment_popup_window_edit_text);
        if (commentAuthorName!=null){
            String hint="reply "+commentAuthorName;
            editText.setHint(hint);
        }
        final Button sendCommentButton=view.findViewById(R.id.comment_popup_window_send_button);

        if(inputContentHashMap.get(origin)!=null&&!inputContentHashMap.get(origin).isEmpty()){
            sendCommentButton.setBackground(getResources().getDrawable(R.drawable.button_shape3));
            sendCommentButton.setEnabled(true);

        } else {
            sendCommentButton.setBackground(getResources().getDrawable(R.drawable.button_shape4));
            sendCommentButton.setEnabled(false);

        }
        sendCommentButton.setOnClickListener(this);
        if(inputContentHashMap.get(origin)!=null&&!inputContentHashMap.get(origin).isEmpty()){
            editText.setText(inputContentHashMap.get(origin));
        }
        editText.requestFocus();
        final InputMethodManager inputMethodManager= (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.post(new Runnable() {
            @Override
            public void run() {
                if(inputMethodManager!=null){
                    //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    inputMethodManager.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                inputContentHashMap.put(origin,s.toString());
                if(inputContentHashMap.get(origin)!=null&&!inputContentHashMap.get(origin).isEmpty()){
                    sendCommentButton.setBackground(getResources().getDrawable(R.drawable.button_shape3));
                    sendCommentButton.setEnabled(true);
                } else {
                    sendCommentButton.setBackground(getResources().getDrawable(R.drawable.button_shape4));
                    sendCommentButton.setEnabled(false);
                }
            }
        });
    }
    @Override
    public void onCommentClick(String commentID, String commentAuthorName, String commentAuthorID
            ,Boolean reply,String position,String parentCommentID) {
        if(reply){
            this.commentPosition=position;
            this.commentIdForReply=commentID;
            this.parentCommentID=parentCommentID;
            initCommentWindow(parent,position,commentAuthorName);
        }else {
            //to do
        }
    }
    private void sendComment(String origin){
        FireBaseMethods fireBaseMethods=new FireBaseMethods(this);
        fireBaseMethods.postComment(inputContentHashMap.get(origin),postID);
        if (popupWindow.isShowing()){
            popupWindow.dismiss();
            inputContentHashMap.remove(origin);
        }
    }
    private void sendReply(String origin,String commentID,String parentCommentID){
        FireBaseMethods fireBaseMethods=new FireBaseMethods(this);
        fireBaseMethods.postReply(inputContentHashMap.get(origin),postID,commentID,parentCommentID);
        if (popupWindow.isShowing()){
            popupWindow.dismiss();
            inputContentHashMap.remove(origin);
        }

    }
}
