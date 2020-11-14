package com.example.mytips;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mytips.login.LoginActivity;
import com.example.mytips.share.ShareActivity;
import com.example.mytips.utils.Permissions;
import com.example.mytips.utils.SoftKeyBoardHidingHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BottomNavigationViewEx bottomNavigationViewEx;
    private FloatingActionButton mFloatingActionButton;
    private FirebaseAuth mAuth;
    public Menu bottomNavMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        initFirebaseComponents();
        setUpBottomNavigation();
    }

    private void initFirebaseComponents(){
        mAuth=FirebaseAuth.getInstance();
        
    }
    private void initWidgets(){
        bottomNavigationViewEx=this.findViewById(R.id.main_activity_bottom_nav);
        bottomNavMenu=bottomNavigationViewEx.getMenu();
        mFloatingActionButton=this.findViewById(R.id.main_activity_fab);
        mFloatingActionButton.setOnClickListener(this);
    }
    private void setUpBottomNavigation(){
        BottomNavigationSetter bottomNavigationSetter = new BottomNavigationSetter(this);
        bottomNavigationSetter.setUpNavigation(bottomNavigationViewEx);
    }

// Method invoked in the fragment that is about to be replaced with another one (for directing to login page when unauthenticated)
    public void replaceFragmentInsideFragment (Fragment fragment) {
    FragmentManager fragmentManager=getSupportFragmentManager();
    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_activity_frame_layout,fragment);
    fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.main_activity_fab){
            FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
            if(mFirebaseUser!=null) {
                if (checkPermissions()) {
                    Intent mIntent = new Intent(this, ShareActivity.class);
                    startActivity(mIntent);
                } else {
                    ActivityCompat.requestPermissions(this, Permissions.PERMISSIONS, 1);
                }
            }else {
                Intent mIntent=new Intent(this, LoginActivity.class);
                startActivity(mIntent);
            }
        }
    }
    private Boolean checkPermissions(){
        for(int i = 0; i< Permissions.PERMISSIONS.length; i++){
            if(!checkSinglePermission(Permissions.PERMISSIONS[i])){
                return false;
            }
        } return true;
    }
    private Boolean checkSinglePermission(String s){
        int permissionRequest= ActivityCompat.checkSelfPermission(this,s);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }

}