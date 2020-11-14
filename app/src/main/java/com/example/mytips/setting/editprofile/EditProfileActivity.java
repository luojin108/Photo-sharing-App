package com.example.mytips.setting.editprofile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mytips.model.User;
import com.example.mytips.model.UserAccountSetting;
import com.example.mytips.model.UserInfo;
import com.example.mytips.R;
import com.example.mytips.myfragment.PreviewProfilePhotoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolBar;
    private RelativeLayout editNameRelativeLayout,editDescriptionRelativeLayout,editBirthdayRelativeLayout
            ,editProfilePhotoRelativeLayout;
    private TextView mName,mDescription,mBirthday,mResidence,mEmail;
    private CircleImageView profilePhoto;
    private ProgressBar mProgressbar;
    //firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseRef;
    private String userID;
    private User user;
    //model
    private UserAccountSetting userAccountSetting;
    private UserInfo userInfo;
    public EditProfileActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initWidgets();
        initFirebaseComponents();
        initToolBar();

    }
    @Override
    protected void onStart() {
        super.onStart();
        setDatabaseRefToValueEventListener();
    }

    private void initToolBar(){
        mToolBar=this.findViewById(R.id.activity_edit_profile_tool_bar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initWidgets(){
        editNameRelativeLayout=this.findViewById(R.id.edit_profile_name_rl);
        editNameRelativeLayout.setOnClickListener(this);
        editDescriptionRelativeLayout=this.findViewById(R.id.edit_profile_description_rl);
        editDescriptionRelativeLayout.setOnClickListener(this);
        editBirthdayRelativeLayout=this.findViewById(R.id.edit_profile_birthday_rl);
        editBirthdayRelativeLayout.setOnClickListener(this);
        editProfilePhotoRelativeLayout=this.findViewById(R.id.edit_profile_photo_rl);
        editProfilePhotoRelativeLayout.setOnClickListener(this);
        profilePhoto=this.findViewById(R.id.edit_profile_circle_view);
        profilePhoto.setOnClickListener(this);
        mName=this.findViewById(R.id.edit_profile_name);
        mDescription=this.findViewById(R.id.edit_profile_description);
        mBirthday=this.findViewById(R.id.edit_profile_birthday);
        mResidence=this.findViewById(R.id.edit_profile_residence);
        mEmail=this.findViewById(R.id.edit_profile_email);
        mProgressbar=this.findViewById(R.id.activity_edit_profile_progress_bar);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_profile_name_rl:
                Intent mIntent=new Intent(this, EditNameActivity.class);
                startActivity(mIntent);
                break;
            case R.id.edit_profile_description_rl:
                Intent mIntent2=new Intent(this,EditDescriptionActivity.class);
                mIntent2.putExtra("descriptionText",mDescription.getText().toString());
                startActivity(mIntent2);
                break;
            case R.id.edit_profile_birthday_rl:
                OnDateSetListenerForEditingBirthday onDateSetListenerForEditingBirthday=
                        new OnDateSetListenerForEditingBirthday(mDatabaseRef,userID,getApplicationContext());
                Calendar calendar=Calendar.getInstance();
                DatePickerDialog datePickerDialog=
                        new DatePickerDialog(this,onDateSetListenerForEditingBirthday,calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.edit_profile_circle_view:
                if(userAccountSetting!=null){
                    if(userAccountSetting.getProfile_photo()!=null){
                        Intent mIntent3=new Intent(this, PreviewProfilePhotoActivity.class);
                        mIntent3.putExtra("profilePhotoUri", userAccountSetting.getProfile_photo());
                        startActivity(mIntent3);
                    }
                }
                break;
            case R.id.edit_profile_photo_rl:
                Intent mIntent4=new Intent(this, EditProfilePhotoAlbum.class);
                startActivity(mIntent4);
                break;
        }
    }
    //init firebase components
    private void initFirebaseComponents(){
        mFirebaseAuth=FirebaseAuth.getInstance();
        userID=mFirebaseAuth.getUid();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseRef=mFirebaseDatabase.getReference();
    }

    // ............The followings are the components needed for populating widgets with data from database
    private void setDatabaseRefToValueEventListener(){
        user=new User();
        mDatabaseRef.child(this.getString(R.string.child_user_account_setting)).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieve information from database
                UserAccountSetting userAccountSetting=getUserAccountSettingFromDB(dataSnapshot);
                user.setUserAccountSetting(userAccountSetting);
                if(user.getUserAccountSetting()!=null&&user.getUserInfo()!=null){
                    setWidgetData(user);
                    mProgressbar.setVisibility(View.GONE);
                }
                //retrieve images from database
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabaseRef.child(this.getString(R.string.child_user_info)).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieve information from database
                UserInfo userInfo=getUserInfoFromDB(dataSnapshot);
                user.setUserInfo(userInfo);
                if(user.getUserAccountSetting()!=null&&user.getUserInfo()!=null){
                    setWidgetData(user);
                    mProgressbar.setVisibility(View.GONE);
                }
                //retrieve images from database
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //get user data from the database, store them in the model,UserInfo and UserAccountSetting, which are placed in the class User
    private UserInfo getUserInfoFromDB(DataSnapshot dataSnapshot){
        userInfo=new UserInfo();
        try{
            userInfo.setEmail(dataSnapshot.getValue(UserInfo.class).getEmail());
            userInfo.setUser_name(dataSnapshot.getValue(UserInfo.class).getUser_name());
        }
        catch (NullPointerException e){
            //
        }
        return userInfo;
    }
    private UserAccountSetting getUserAccountSettingFromDB(DataSnapshot dataSnapshot){
        userAccountSetting=new UserAccountSetting();
        try {
            userAccountSetting.setBirthday(dataSnapshot.getValue(UserAccountSetting.class).getBirthday());
            userAccountSetting.setDescription(dataSnapshot.getValue(UserAccountSetting.class).getDescription());
            userAccountSetting.setFollower(dataSnapshot.getValue(UserAccountSetting.class).getFollower());
            userAccountSetting.setFollowing(dataSnapshot.getValue(UserAccountSetting.class).getFollowing());
            userAccountSetting.setGender(dataSnapshot.getValue(UserAccountSetting.class).getGender());
            userAccountSetting.setLiked(dataSnapshot.getValue(UserAccountSetting.class).getLiked());
            userAccountSetting.setResidence(dataSnapshot.getValue(UserAccountSetting.class).getResidence());
            userAccountSetting.setProfile_photo(dataSnapshot.getValue(UserAccountSetting.class).getProfile_photo());
            userAccountSetting.setBackground_photo(dataSnapshot.getValue(UserAccountSetting.class).getBackground_photo());
        } catch (NullPointerException e){
            //;
        }
        return userAccountSetting;
    }


    //set the user data from database to corresponding widgets
    private void setWidgetData(User user){
        try {
            Glide.with(getApplicationContext())
                    .load(user.getUserAccountSetting().getProfile_photo())
                    .into(profilePhoto);
        }catch (NullPointerException e){
            //
        }
        mName.setText(user.getUserInfo().getUser_name());
        mDescription.setText(user.getUserAccountSetting().getDescription());
        mResidence.setText(user.getUserAccountSetting().getResidence());
        mBirthday.setText(user.getUserAccountSetting().getBirthday());
        mEmail.setText(user.getUserInfo().getEmail());
    }
    //............
}
