package com.example.mytips.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytips.R;
import com.example.mytips.setting.editprofile.EditProfileActivity;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout editProfileRelativeLayout,logOutRelativeLayout;
    private androidx.appcompat.widget.Toolbar mToolBar;
    public SettingActivity() {
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        editProfileRelativeLayout=this.findViewById(R.id.setting_activity_rl_edit_profile);
        logOutRelativeLayout=this.findViewById(R.id.activity_setting_logout_rl);
        editProfileRelativeLayout.setOnClickListener(this);
        logOutRelativeLayout.setOnClickListener(this);
        initToolBar();
    }
    private void initToolBar() {
        mToolBar = this.findViewById(R.id.setting_activity_tool_bar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_activity_rl_edit_profile:
                Intent mIntent=new Intent(this, EditProfileActivity.class);
                startActivity(mIntent);
                break;
            case R.id.activity_setting_logout_rl:
                new LogOutFragment().show(getSupportFragmentManager(),"LogoutDialogue");
                break;
        }
    }
}
