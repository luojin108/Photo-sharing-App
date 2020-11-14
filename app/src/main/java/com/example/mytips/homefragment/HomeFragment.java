package com.example.mytips.homefragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.mytips.MainActivity;
import com.example.mytips.R;
import com.example.mytips.utils.SoftKeyBoardHidingHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {
    private final String TAG="HomeFragment";
    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private SearchView searchView;
    //fireBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseRef;
    private String userID;
    public HomeFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        initWidgets(view);
        initFireBaseComponents();
        initViewPager();
        return  view;
    }
    private void initWidgets(View view){
        mTablayout=view.findViewById(R.id.fragment_home_tab);
        mViewPager=view.findViewById(R.id.fragment_home_viewpager);
        mTablayout.setupWithViewPager(mViewPager);
        searchView=view.findViewById(R.id.fragment_home_search_view);
    }

    //if touching outside input soft keyboard, close keyboard
    /*private void closeKeyBoardIfTouchingOutside (View view){
        if(!(view instanceof EditText)){
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: view 1");
                    SoftKeyBoardHidingHelper.hideSoftKeyBoard(getActivity());
                    return false;
                }
            });

        }
        if(view instanceof ViewGroup){
            for(int i=0; i<((ViewGroup)view).getChildCount();i++){
                View innerView=((ViewGroup)view).getChildAt(i);
                Log.d(TAG, "onTouch: view 2"+innerView.getClass());

                innerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.d(TAG, "onTouch: view 2");

                        SoftKeyBoardHidingHelper.hideSoftKeyBoard(getActivity());
                        return false;
                    }
                });
            }
        }
    }*/
    //initialize firebase components
    private void initFireBaseComponents(){
        mAuth = FirebaseAuth.getInstance();
        userID=mAuth.getUid();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseRef=mFirebaseDatabase.getReference();
    }
    @Override
    public void onStart() {
        super.onStart();
        //fix the bug that the fragment is selected but the corresponding navigation button is not checked
        try{
            Menu menu=((MainActivity)getActivity()).bottomNavMenu;
            if(!menu.getItem(0).isChecked()){
                menu.getItem(0).setChecked(true);
            }
        }catch (NullPointerException e){
            //
        }
    }
    private void initViewPager(){
        Fragment homeFragmentChild1= new HomeFragmentChild1(userID,mDatabaseRef);
        Fragment homeFragmentChild2= new HomeFragmentChild2();
        Fragment homeFragmentChild3= new HomeFragmentChild3();
        FragmentManager fragmentManager=getChildFragmentManager();
        HomeFragmentPagerAdapter homeFragmentPagerAdapter=new HomeFragmentPagerAdapter(fragmentManager,1);
        homeFragmentPagerAdapter.addFragment(homeFragmentChild1, "Discovery");
        homeFragmentPagerAdapter.addFragment(homeFragmentChild2, "Following");
        homeFragmentPagerAdapter.addFragment(homeFragmentChild3, "Local");
        mViewPager.setAdapter(homeFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
    }


}
