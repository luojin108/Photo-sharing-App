package com.example.mytips.myfragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.mytips.login.LoginFragment2;
import com.example.mytips.MainActivity;
import com.example.mytips.model.User;
import com.example.mytips.model.UserAccountSetting;
import com.example.mytips.model.UserInfo;
import com.example.mytips.R;
import com.example.mytips.setting.SettingActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFragment extends Fragment implements View.OnClickListener {
    private final static String TAG="MyFragment";
    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private AppBarLayout mAppbarlayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolBar;
    private TextView mainTitle,nameText,descriptionText,followerNumText,followingNumText,likedNumText;
    private AppCompatImageView backgroundImage;
    private CircleImageView profilePhoto;
    private ProgressBar mProgressbar;
    //fireBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseRef;
    private String userID;
    private UserAccountSetting userAccountSetting;
    private UserInfo userInfo;
    private User user;
    public MyFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my, container, false);
        initWidgets(view);
        initToolBar(view);
        setHasOptionsMenu(true);
        customizeContentScrim ();
        initFireBaseComponents();
        initViewPager();
        mTablayout.setupWithViewPager(mViewPager);
        Log.d(TAG, "onCreateView: ");
        return view;

    }
    private void initToolBar(View view){
        mToolBar=view.findViewById(R.id.my_fragment_tool_bar);
        if(getActivity() !=null ){
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolBar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);}
    }
    private void initWidgets(View view){
        mTablayout=view.findViewById(R.id.fragment_my_tab);
        mViewPager=view.findViewById(R.id.fragment_my_viewpager);
        mAppbarlayout=view.findViewById(R.id.my_fragment_appbar);
        mCollapsingToolbarLayout=view.findViewById(R.id.my_fragment_collapsing_toolbar);
        mainTitle=view.findViewById(R.id.my_fragment_main_title);
        backgroundImage=view.findViewById(R.id.fragment_my_background_image);
        profilePhoto=view.findViewById(R.id.fragment_my_circle_view);
        profilePhoto.setOnClickListener(this);
        nameText=view.findViewById(R.id.my_fragment_name_text);
        descriptionText=view.findViewById(R.id.fragment_my_description_text);
        followerNumText=view.findViewById(R.id.fragment_my_follower_num);
        followingNumText=view.findViewById(R.id.fragment_my_following_num);
        likedNumText=view.findViewById(R.id.fragment_my_liked_num);
        mProgressbar=view.findViewById(R.id.fragment_my_progress_bar);

    }
    //initialize firebase components, set mDatabaseRef to value event listener
    private void initFireBaseComponents(){
        mAuth = FirebaseAuth.getInstance();
        userID=mAuth.getUid();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseRef=mFirebaseDatabase.getReference();

    }
    //initialize profile background and profile photo

    private void initViewPager(){
        Fragment myFragmentChild1= new MyFragmentChild1(userID,mDatabaseRef);
        Fragment myFragmentChild2= new MyFragmentChild2(userID,mDatabaseRef);
        Fragment myFragmentChild3= new MyFragmentChild3();
        FragmentManager fragmentManager=getChildFragmentManager();
        MyFragmentPagerAdapter myFragmentPagerAdapter=new MyFragmentPagerAdapter(fragmentManager,1);
        myFragmentPagerAdapter.addFragment(myFragmentChild1, getString(R.string.my_fragment_pager_title_1));
        myFragmentPagerAdapter.addFragment(myFragmentChild2, getString(R.string.my_fragment_pager_title_2));
        myFragmentPagerAdapter.addFragment(myFragmentChild3, getString(R.string.my_fragment_pager_title_3));
        mViewPager.setAdapter(myFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_my_circle_view:
                if(userAccountSetting!=null){
                    if(userAccountSetting.getProfile_photo()!=null){
                        Intent intent=new Intent(getContext(), PreviewProfilePhotoActivity.class);
                        intent.putExtra("profilePhotoUri", userAccountSetting.getProfile_photo());
                        startActivity(intent);
                    }
                }
                break;

        }
    }
    //...........The followings are responsible for changing the transparency of toolbar and main title when scrolling down
    // ToolBar background gradually become transparent when scrolling down
    private void customizeContentScrim (){
        mAppbarlayout.addOnOffsetChangedListener((new AppBarLayout.OnOffsetChangedListener()
        {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                mToolBar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.white),Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange()));
                mainTitle.setTextColor(changeAlpha(getResources().getColor(R.color.black),Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange()));
            }
        }));
    }
    //color changes according to the fraction parameter changes
    private int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }
    //............

    @Override
    public void onStart() {
        super.onStart();
        //fix the bug that the fragment is selected but the corresponding navigation button is not checked
        try{
            Menu menu=((MainActivity)getActivity()).bottomNavMenu;
            if(!menu.getItem(4).isChecked()){
                menu.getItem(4).setChecked(true);
            }
        }catch (NullPointerException e){
            //
        }

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser firebaseUser){
        if(firebaseUser==null){
            Fragment loginFragment2=new LoginFragment2();
            if( getActivity() !=null){
                ((MainActivity)getActivity()).replaceFragmentInsideFragment(loginFragment2);
            }
        } else{
            setDatabaseRefToValueEventListener();
        }
    }

    // ............The followings are the components needed for populating widgets with data from database
    private void setDatabaseRefToValueEventListener(){
        user=new User();
        mDatabaseRef.child(this.getActivity().getString(R.string.child_user_account_setting)).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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
        mDatabaseRef.child(this.getActivity().getString(R.string.child_user_info)).child(userID).addValueEventListener(new ValueEventListener() {
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
                        if(dataSnapshot.child("total_users_liked").exists()){
                            userAccountSetting.setLiked(dataSnapshot.child("total_users_liked").getChildrenCount());
                        } else {
                            userAccountSetting.setLiked(dataSnapshot.getValue(UserAccountSetting.class).getLiked());
                        }
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
            Glide.with(this)
                    .load(user.getUserAccountSetting().getProfile_photo())
                    .into(profilePhoto);
            Glide.with(this)
                    .load(user.getUserAccountSetting().getBackground_photo())
                    .into(backgroundImage);
        }catch (NullPointerException e){
            //
        }
        mainTitle.setText(user.getUserInfo().getUser_name());
        nameText.setText(user.getUserInfo().getUser_name());
        descriptionText.setText(user.getUserAccountSetting().getDescription());
        followingNumText.setText(String.valueOf(user.getUserAccountSetting().getFollowing()));
        followerNumText.setText(String.valueOf(user.getUserAccountSetting().getFollower()));
        likedNumText.setText(String.valueOf(user.getUserAccountSetting().getLiked()));
        Log.d(TAG, "setWidgetData: ");
    }
    //............
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_my_tool_bar_menu,menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu_setting){
            Intent mIntent=new Intent(getContext(),SettingActivity.class);
            startActivity(mIntent);
        }
        return true;
    }
}
