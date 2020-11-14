package com.example.mytips;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.mytips.homefragment.HomeFragment;
import com.example.mytips.messagefragment.MessageFragment;
import com.example.mytips.myfragment.MyFragment;
import com.example.mytips.proposalfragment.ProposalFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationSetter {
    private static final String TAG="BottomNavigationSetter";
    private MainActivity activity;


    public BottomNavigationSetter(MainActivity activity) {

        this.activity = activity;

    }

    // setup navigation for the BottomNavigationView
    public void setUpNavigation(BottomNavigationViewEx bottomNavigationViewEx){
        //get each fragment.
        final Fragment homeFragment = new HomeFragment();
        final Fragment proposalFragment = new ProposalFragment();
        final Fragment messageFragment = new MessageFragment();
        final Fragment myFragment = new MyFragment();
        //default page when entering MainActivity
        manageFragment(homeFragment);
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bottom_nav_item1:
                        manageFragment(homeFragment);
                        break;
                    case R.id.bottom_nav_item2:
                        manageFragment(proposalFragment);
                        break;
                    case R.id.bottom_nav_item4:
                        manageFragment(messageFragment);
                        break;
                    case R.id.bottom_nav_item5:
                        manageFragment(myFragment);
                        break;
                }
                return true;
            }
        });
    }
    //method for Instantiating FragmentManager
    private void manageFragment(Fragment fragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_frame_layout,fragment);
        fragmentTransaction.commit();
    }
}
