package com.example.mytips.post;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.mytips.R;
import com.example.mytips.model.Comment;
import com.example.mytips.model.Reply;
import com.example.mytips.widgets.SquareCircleImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommentExpandAdapter extends BaseExpandableListAdapter {
    private static final String TAG="CommentExpandAdapter";
    private ArrayList<Comment> commentArrayList;
    private Context context;
    private String userID;
    private DatabaseReference databaseReference;

    public CommentExpandAdapter(Context context, ArrayList<Comment> commentArrayList,String userID,DatabaseReference databaseReference) {
        this.commentArrayList = commentArrayList;
        this.context=context;
        this.userID=userID;
        this.databaseReference=databaseReference;
    }

    @Override
    public int getGroupCount() {
        return commentArrayList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (commentArrayList.get(groupPosition).getReplies().isEmpty()){
            return 0;
        }else {
            return commentArrayList.get(groupPosition).getReplies().size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return commentArrayList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return commentArrayList.get(groupPosition).getReplies().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition,childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupHolder groupHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.comment_item, parent,false);
            groupHolder= new GroupHolder(context, convertView, commentArrayList, databaseReference);
            convertView.setTag(groupHolder);
        } else {
            groupHolder=(GroupHolder)convertView.getTag();
        }
        groupHolder.populateWidgets(groupPosition);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d(TAG, "getChildView: "+"child position is "+childPosition);
        Log.d(TAG, "getChildView: "+"group position is "+groupPosition);
        Log.d(TAG, "getChildView: "+" convertview is "+ convertView);
        final ChildHolder childHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.reply_item, parent,false);
            childHolder= new ChildHolder(context, convertView, commentArrayList, databaseReference);
            convertView.setTag(childHolder);
        } else {
            childHolder=(ChildHolder)convertView.getTag();
        }

        childHolder.populateWidgets(groupPosition,childPosition);



        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class GroupHolder implements View.OnClickListener {
        private SquareCircleImageView profilePhoto;
        private TextView authorName, commentContent, likeNumber,commentTime;
        private LikeButton likeButton;
        private ArrayList<Comment> commentArrayList;
        private DatabaseReference databaseReference;
        private Context context;
        private int position;
        private String commentAuthorNameText,commentText,commentUserID;
        private Long commentTimeStamp;

        public GroupHolder(Context context,View convertView, ArrayList<Comment> commentArrayList, DatabaseReference databaseReference) {
            this.profilePhoto=convertView.findViewById(R.id.comment_item_profile_photo);
            this.authorName=convertView.findViewById(R.id.comment_item_author_name);
            this.commentContent=convertView.findViewById(R.id.comment_item_comment_content);
            this.commentTime=convertView.findViewById(R.id.comment_item_comment_time);
            this.likeButton=convertView.findViewById(R.id.comment_item_like_button);
            this.likeNumber=convertView.findViewById(R.id.comment_item_like_number);
            this.commentArrayList=commentArrayList;
            this.databaseReference=databaseReference;
            this.context=context;
        }


        private void populateWidgets(int position){
            this.position=position;
            commentText=commentArrayList.get(position).getComment_text();
            commentUserID=commentArrayList.get(position).getUser_id();
            commentTimeStamp=commentArrayList.get(position).getTime_stamp();
            commentContent.setText(commentText);
            setUpTime();
            databaseReference.child(context.getResources().getString(R.string.child_user_account_setting))
                    .child(commentUserID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                if(ds.getKey().equals(context.getResources().getString(R.string.name))){
                                    commentAuthorNameText=ds.getValue().toString();
                                    authorName.setText(commentAuthorNameText);
                                }
                                if(ds.getKey().equals(context.getResources().getString(R.string.account_setting_node_profile_photo))){
                                    Glide.with(context).load(ds.getValue()).into(profilePhoto);
                                }
                            }
                            initListener();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }
        private void setUpTime(){
            long timeDifference=getTimestampDifferenceInMinutes(commentTimeStamp);
            if(timeDifference<60){
                String string= timeDifference +" "+"min";
                commentTime.setText(string);
            } else if (1<=(timeDifference/60) && (timeDifference/60)<24){
                String string=Math.round(timeDifference/60.0)+" "+"h";
                commentTime.setText(string);
            } else if(1<=(timeDifference/60/24) && (timeDifference/60/24)<6){
                String string=Math.round(timeDifference/60.0/24)+" "+"d";
                commentTime.setText(string);
            } else if(6<(timeDifference/60/24)){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));//google 'android list of timezones'
                String date=simpleDateFormat.format(new Date(commentTimeStamp*-1));
                commentTime.setText(date);
            }
        }
        /**
         * Note that the input is time in Long, the time stamp has negative sign, should be converted
         * to positive
         */
        private long getTimestampDifferenceInMinutes(Long time){
            if (time !=null){
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyy-MM-dd'T'HH:mm:ss:SSS", Locale.ENGLISH);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));//google 'android list of timezones'
                Date today = calendar.getTime();
                return (long) Math.round(((today.getTime() - time * -1) / 1000 / 60));
            }else {
                return 0;
            }

        }

        private void initListener(){
            profilePhoto.setOnClickListener(this);
            authorName.setOnClickListener(this);
            commentContent.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.comment_item_profile_photo || v.getId()==R.id.comment_item_author_name){
                ((OnCommentClickListener)context).onCommentClick(null,null,
                        commentArrayList.get(position).getUser_id(),false,null,null);
            }else if (v.getId()==R.id.comment_item_comment_content){
                ((OnCommentClickListener)context).onCommentClick(commentArrayList.get(position).getComment_id(),
                        commentAuthorNameText, commentArrayList.get(position).getUser_id(),true, String.valueOf(position),null);
            }

        }
    }
    private static class ChildHolder implements View.OnClickListener {
        private SquareCircleImageView profilePhoto;
        private TextView authorName, replyContent, likeNumber,replyTime;
        private LikeButton likeButton;
        private ArrayList<Comment> commentArrayList;
        private DatabaseReference databaseReference;
        private Context context;
        private int groupPosition,childPosition;
        private String replyAuthorNameText,replyText,replyUserID,replyID,parentCommentID,repliedID,postID;
        private Long replyTimeStamp;

        public ChildHolder(Context context,View convertView, ArrayList<Comment> commentArrayList, DatabaseReference databaseReference) {
            this.profilePhoto=convertView.findViewById(R.id.reply_item_profile_photo);
            this.authorName=convertView.findViewById(R.id.reply_item_author_name);
            this.replyContent=convertView.findViewById(R.id.reply_item_reply_content);
            this.replyTime=convertView.findViewById(R.id.reply_item_reply_time);
            this.likeButton=convertView.findViewById(R.id.reply_item_like_button);
            this.likeNumber=convertView.findViewById(R.id.reply_item_like_number);
            this.commentArrayList=commentArrayList;
            this.databaseReference=databaseReference;
            this.context=context;
        }

        private void populateWidgets(int groupPosition, int childPosition){
            this.groupPosition=groupPosition;
            this.childPosition=childPosition;
            replyText=commentArrayList.get(groupPosition).getReplies().get(childPosition)
                    .getReply_text();
            replyUserID=commentArrayList.get(groupPosition).getReplies().get(childPosition)
                    .getUser_id();
            replyTimeStamp=commentArrayList.get(groupPosition).getReplies().get(childPosition)
                    .getTime_stamp();
            replyID=commentArrayList.get(groupPosition).getReplies().get(childPosition)
                    .getReply_id();
            repliedID=commentArrayList.get(groupPosition).getReplies().get(childPosition)
                    .getReplied_id();
            parentCommentID=commentArrayList.get(groupPosition).getComment_id();
            postID=commentArrayList.get(groupPosition).getPost_id();

            if(repliedID.equals(parentCommentID)){
                replyContent.setText(replyText);
                getAuthorNameAndProfilePhoto();
            }else {
                databaseReference.child(context.getResources().getString(R.string.image_posts_node))
                        .child(postID)
                        .child(context.getResources().getString(R.string.comments_node))
                        .child(parentCommentID)
                        .child(context.getResources().getString(R.string.replies_node))
                        .child(repliedID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String repliedUserID=dataSnapshot.child(context.getResources()
                                        .getString(R.string.user_id)).getValue().toString();
                                getAuthorNameForTheRepliedComment(repliedUserID);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
            }
            setUpTime();
        }
        private void getAuthorNameForTheRepliedComment(String repliedUserID){
            databaseReference.child(context.getResources().getString(R.string.child_user_account_setting))
                    .child(repliedUserID).child(context.getResources().getString(R.string.name))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String prefix="reply to "+dataSnapshot.getValue()+": ";
                            String textToBeDisplayed=prefix+replyText;
                            SpannableString spannableString=new SpannableString(textToBeDisplayed);
                            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.dark_gray)),9,
                                    prefix.length()-2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            replyContent.setText(spannableString);
                            getAuthorNameAndProfilePhoto();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }
        private void getAuthorNameAndProfilePhoto(){
            //get the author name and profile photo for the reply/comment
            databaseReference.child(context.getResources().getString(R.string.child_user_account_setting))
                    .child(replyUserID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                if(ds.getKey().equals(context.getResources().getString(R.string.name))){
                                    replyAuthorNameText=ds.getValue().toString();
                                    authorName.setText(replyAuthorNameText);
                                }
                                if(ds.getKey().equals(context.getResources().getString(R.string.account_setting_node_profile_photo))){
                                    Glide.with(context).load(ds.getValue()).into(profilePhoto);
                                }
                            }
                            initListener();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }
        private void setUpTime(){
            long timeDifference=getTimestampDifferenceInMinutes(replyTimeStamp);
            if(timeDifference<60){
                String string= timeDifference +" "+"min";
                replyTime.setText(string);
            } else if (1<=(timeDifference/60) && (timeDifference/60)<24){
                String string=Math.round(timeDifference/60.0)+" "+"h";
                replyTime.setText(string);
            } else if(1<=(timeDifference/60/24) && (timeDifference/60/24)<6){
                String string=Math.round(timeDifference/60.0/24)+" "+"d";
                replyTime.setText(string);
            } else if(6<(timeDifference/60/24)){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));//google 'android list of timezones'
                String date=simpleDateFormat.format(new Date(replyTimeStamp*-1));
                replyTime.setText(date);
            }
        }
        /**
         * Note that the input is time in Long, the time stamp has negative sign, should be converted
         * to positive
         */
        private long getTimestampDifferenceInMinutes(Long time){
            if (time !=null){
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyy-MM-dd'T'HH:mm:ss:SSS", Locale.ENGLISH);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));//google 'android list of timezones'
                Date today = calendar.getTime();
                return (long) Math.round(((today.getTime() - time * -1) / 1000.0 / 60));
            }else {
                return 0;
            }

        }

        private void initListener(){
            profilePhoto.setOnClickListener(this);
            authorName.setOnClickListener(this);
            replyContent.setOnClickListener(this);

        }

        /**
         * reply is also a type of comment, that is why onCommentListener is applied to it.
         * @param v view
         */
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.reply_item_profile_photo || v.getId()==R.id.reply_item_author_name){
                ((OnCommentClickListener)context).onCommentClick(null,null,
                        replyUserID,false,null,null);
            }else if (v.getId()==R.id.reply_item_reply_content){

                ((OnCommentClickListener)context).onCommentClick(replyID,
                        replyAuthorNameText, replyUserID,true, groupPosition+"-"+childPosition,parentCommentID);
            }

        }
    }

    public interface OnCommentClickListener{
        void onCommentClick(String commentID, String commentAuthorName, String commentAuthorID
                , Boolean reply,String position,String parentCommentID);
    }
}
