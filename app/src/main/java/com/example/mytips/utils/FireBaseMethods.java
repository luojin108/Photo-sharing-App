package com.example.mytips.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mytips.R;
import com.example.mytips.model.Comment;
import com.example.mytips.model.ImagePosts;
import com.example.mytips.model.Reply;
import com.example.mytips.model.UserAccountSetting;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FireBaseMethods {
    private String userID, TAG="FireBaseMethods";
    private Context context;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    public FireBaseMethods(Context context) {
        this.context=context;
        this.storageReference= FirebaseStorage.getInstance().getReference();
        this.databaseReference=FirebaseDatabase.getInstance().getReference();
        try {
            this.userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e){
            //
        }
    }
    //...............................................
    public void uploadPublicImagePost(final String title, final String tags, final String description, final ArrayList<String> imageUriList){
        databaseReference.child(context.getString(R.string.child_user_account_setting)).child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String profilePhotoUri=dataSnapshot.getValue(UserAccountSetting.class).getProfile_photo();
                        String userName=dataSnapshot.getValue(UserAccountSetting.class).getName();
                        startUploadingImagePosts(title,tags,description,imageUriList,profilePhotoUri,userName);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
    private void startUploadingImagePosts(String title, String tags, String description, final ArrayList<String> imageUriList,String profilePhotoUri, String userName) {
        // create image posts model
        final ImagePosts imagePosts=new ImagePosts();
        imagePosts.setTitle(title);
        imagePosts.setTags(tags);
        imagePosts.setUser_name(userName);
        imagePosts.setDescription(description);
        imagePosts.setDate_posted(getMyTime());
        imagePosts.setTime_stamp(-1*new Date().getTime());
        imagePosts.setUser_id(userID);
        imagePosts.setProfile_photo(profilePhotoUri);
        imagePosts.setNumber_of_like(0);
        FilePaths filePaths=new FilePaths();
        //Create a http url string array with the size of imageUriList
        final String[] httpUrlList= new String[imageUriList.size()];
        //create a image height list
        final ArrayList<Integer> bitmapHeights=new ArrayList<>();
        //create a image width list
        final ArrayList<Integer> bitmapWidths=new ArrayList<>();
        //upload each image to firebase storage, when each image is uploaded, add the download
        //Uri to the imagePosts model, if all images are uploaded, send the model to database
        BitmapGetter bitmapGetter=new BitmapGetter(context);
        for(int i=0;i<=imageUriList.size()-1;i++){
            MyOnProgressListener myOnProgressListener=new MyOnProgressListener(context,i,imageUriList);
            String time=getMyTime();
            final StorageReference imageStorageReference=storageReference
                    .child(filePaths.fireBaseStorage+"/"+userID+"/"+time+"-"+i);
            Bitmap bitmap=bitmapGetter.getBitMap(imageUriList.get(i));
            // One problem when uploading photo to the server is that the photo in portrait orientation from camera
            //will be stored in the server in landscape orientation. This is because this photo
            //is specified as ROTATE 90 in ExifInterface, but is probably in landscape orientation in fact. The solution
            //is to detect the orientation tag of the photo from ExifInterface, if it is e.g.,ROTATE 90, then rotate it 90
            // degree prior to uploading.
            Bitmap rotatedBitmap=bitmap;
            try{
                ExifInterface exifInterface=new ExifInterface(imageUriList.get(i));
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                ImageRotator imageRotator=new ImageRotator();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = imageRotator.rotateImage(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = imageRotator.rotateImage(bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = imageRotator.rotateImage(bitmap, 270);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }
            } catch(IOException e){
                //
            }
            bitmapHeights.add(rotatedBitmap.getHeight());
            bitmapWidths.add(rotatedBitmap.getWidth());
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[] data=byteArrayOutputStream.toByteArray();
            //reduce image quality if image is lager than 4 Mb before uploading to firebase
            while(data.length/1024.0/1024.0>4){
                byteArrayOutputStream = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                data=byteArrayOutputStream.toByteArray();
            };
            bitmap.recycle();
            rotatedBitmap.recycle();
            UploadTask uploadTask=imageStorageReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, R.string.share_activity_fail_reminder,Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(myOnProgressListener);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imageStorageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Uri> task) {
                                             if (task.isSuccessful()) {
                                                 Uri downloadUri = task.getResult();
                                                String positionInTheList=downloadUri.getEncodedPath().substring(downloadUri.getEncodedPath().length()-1);
                                                int positionInTheListInt=Integer.parseInt(positionInTheList);
                                                httpUrlList[positionInTheListInt]=downloadUri.toString();
                                                 if(!Arrays.asList(httpUrlList).contains(null)){
                                                     imagePosts.setHttp_uri(new ArrayList<>(Arrays.asList(httpUrlList)));
                                                     imagePosts.setImageHeights(bitmapHeights);
                                                     imagePosts.setImageWidths(bitmapWidths);
                                                     addPublicImagePostsToDatabase(imagePosts, userID);
                                                 }
                                             } else {
                                                 // Handle failures
                                                 // ...
                                             }
                                         }
                                     }
            );
        }
        Log.d("qwe", "startUploadingImagePosts: "+bitmapHeights+"\n"+bitmapWidths);
    }
    private String getMyTime(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));
        return simpleDateFormat.format(new Date());
    }
    private void addPublicImagePostsToDatabase(ImagePosts imagePosts, String userID){
        String imagePostsKey=databaseReference.child(context.getString(R.string.image_posts_node)).push().getKey();
        imagePosts.setPost_id(imagePostsKey);
        //add the post to public image post area in the database
        databaseReference.child(context.getString(R.string.image_posts_node)).child(imagePostsKey).setValue(imagePosts);
        //add the post id to user's image post node in the database, note that only time stamp and post id are added in the node
        databaseReference.child(context.getString(R.string.user_posts_node)).child(userID).child(context.getString(R.string.user_posts_node_user_image_posts)).child(imagePostsKey)
                .child("time_stamp").setValue(imagePosts.getTime_stamp());
        databaseReference.child(context.getString(R.string.user_posts_node)).child(userID).child(context.getString(R.string.user_posts_node_user_image_posts)).child(imagePostsKey)
                .child("post_id").setValue(imagePosts.getPost_id());
    }
    //...............................................^^^

    //use bitmap as input for uploading profile photo.............
    public void uploadProfilePhoto(Bitmap bitmap){
        if(bitmap!=null){
            FilePaths filePaths=new FilePaths();
            final StorageReference imageStorageReference=storageReference
                    .child(filePaths.fireBaseStorage+"/"+userID+"/"+context.getString(R.string.account_setting_node_profile_photo));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            //reduce image quality if image is lager than 4 Mb before uploading to firebase
            while(data.length/1024.0/1024.0>4){
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                data=baos.toByteArray();
            };
            UploadTask uploadTask=imageStorageReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, R.string.share_activity_fail_reminder,Toast.LENGTH_SHORT).show();
                }
            });
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imageStorageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Uri> task) {
                                             if (task.isSuccessful()) {
                                                 Uri downloadUri = task.getResult();
                                                 if(downloadUri!=null){
                                                     addProfilePhotoToDatabase(downloadUri);
                                                 }
                                             } else {
                                                 // Handle failures
                                                 // ...
                                             }
                                         }
                                     }
            );
        }
    }
    private void addProfilePhotoToDatabase(final Uri downLoadUri){
        databaseReference.child(context.getString(R.string.child_user_account_setting)).child(userID).child(context.getString(R.string.account_setting_node_profile_photo)).setValue(downLoadUri.toString());
        //update profile photo in user image posts node
        Query query=databaseReference.child(context.getString(R.string.user_posts_node)).child(userID).child(context.getString(R.string.user_posts_node_user_image_posts));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ds.getRef().child("profile_photo").setValue(downLoadUri.toString());
                    databaseReference.child(context.getString(R.string.image_posts_node)).child(ds.getKey()).child("profile_photo").setValue(downLoadUri.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
    //...............................................^^^

    //post comment.....................................
    public void postComment(String text, String postID){
        String commentKey=databaseReference.child(context.getResources().getString(R.string.image_posts_node))
                .child(postID).child(context.getString(R.string.comments_node)).push().getKey();
        Comment comment=new Comment();
        comment.setComment_id(commentKey);
        comment.setPost_id(postID);
        comment.setTime_stamp(-1*new Date().getTime());
        comment.setUser_id(userID);
        comment.setComment_text(text);
        databaseReference.child(context.getResources().getString(R.string.image_posts_node))
                .child(postID).child(context.getString(R.string.comments_node)).child(commentKey).setValue(comment);
    }
    //...............................................^^^

    //post reply.....................................
    public void postReply(String text, String postId,String commentID,String parentCommentID){
        Log.d(TAG, "postReply: "+parentCommentID);
        String replyKey=databaseReference.child(context.getResources().getString(R.string.image_posts_node))
                .child(postId).child(context.getString(R.string.comments_node)).child(commentID)
                .child(context.getString(R.string.replies_node)).push().getKey();
        Reply reply=new Reply();
        if(parentCommentID==null){
            reply.setParent_id(commentID);
            reply.setReply_id(replyKey);
            reply.setReply_text(text);
            reply.setReplied_id(commentID);
            reply.setTime_stamp(-1*new Date().getTime());
            reply.setUser_id(userID);
             databaseReference.child(context.getResources().getString(R.string.image_posts_node))
                     .child(postId).child(context.getString(R.string.comments_node)).child(commentID)
                     .child(context.getString(R.string.replies_node))
                     .child(replyKey).setValue(reply);
        }else {
            reply.setParent_id(parentCommentID);
            reply.setReply_id(replyKey);
            reply.setReply_text(text);
            reply.setReplied_id(commentID);
            reply.setTime_stamp(-1*new Date().getTime());
            reply.setUser_id(userID);
             databaseReference.child(context.getResources().getString(R.string.image_posts_node))
                     .child(postId).child(context.getString(R.string.comments_node)).child(parentCommentID)
                     .child(context.getString(R.string.replies_node))
                     .child(replyKey).setValue(reply);


        }






    }
    //...............................................^^^


}
