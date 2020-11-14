package com.example.mytips.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mytips.MainActivity;
import com.example.mytips.messagefragment.MessageFragment;
import com.example.mytips.myfragment.MyFragment;
import com.example.mytips.R;
import com.example.mytips.setting.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginFragment2 extends Fragment {
    private static final String TAG="LoginFragment2";
    private ProgressBar mProgressBar;
    private TextView textWait;
    private TextView register;
    private EditText inputAccount;
    private EditText inputPassword;
    private Button loginButton;
    private ImageButton settingButton;
    private FirebaseAuth mAuth;
    private MainActivity mainActivity;


    public LoginFragment2() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_login,container,false);
        mainActivity=(MainActivity)getActivity();
        mAuth=FirebaseAuth.getInstance();
        mProgressBar=view.findViewById(R.id.activity_login_progress_bar);
        textWait=view.findViewById(R.id.activity_login_text_wait);
        register=view.findViewById(R.id.activity_login_text_register);
        inputAccount=view.findViewById(R.id.activity_login_edit_text_email);
        inputPassword=view.findViewById(R.id.activity_login_edit_text_password);
        loginButton=view.findViewById(R.id.activity_login_button);
        settingButton=view.findViewById(R.id.activity_login_setting_button);
        initSettingButton();
        initLoginButton();
        progressBarDisappear();
        initRegisterEntry();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //If the user is logged in, navigate to message fragment
        updateUIForOnStart(currentUser);
    }
    private void progressBarDisappear(){
        mProgressBar.setVisibility(View.GONE);
        textWait.setVisibility(View.GONE);

    }
    private void initRegisterEntry(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), RegisterActivity.class);
                getContext().startActivity(intent);
            }
        });
    }
    private void initLoginButton(){
      loginButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String account=inputAccount.getText().toString();
              String password=inputPassword.getText().toString();
              if(TextUtils.isEmpty(account)||!isAccountValid(account)){
                  Toast.makeText(getContext(),R.string.invalid_email_format_reminder,Toast.LENGTH_SHORT).show();
              }else if ((TextUtils.isEmpty(password))){
                  Toast.makeText(getContext(),R.string.input_password_reminder,Toast.LENGTH_SHORT).show();
              } else {
                  mProgressBar.setVisibility(View.VISIBLE);
                  textWait.setVisibility(View.VISIBLE);
                  mAuth.signInWithEmailAndPassword(account, password)
                          .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                              @Override
                              public void onComplete(@NonNull Task<AuthResult> task) {
                                  if (task.isSuccessful()) {
                                      // Sign in success, update UI with the signed-in user's information

                                      Log.d(TAG, "signInWithEmail:success");
                                      mProgressBar.setVisibility(View.GONE);
                                      textWait.setVisibility(View.GONE);
                                      FirebaseUser user = mAuth.getCurrentUser();
                                      updateUI(user);

                                  } else {
                                      // If sign in fails, display a message to the user.
                                      Log.w(TAG, "signInWithEmail:failure", task.getException());
                                      Toast.makeText(getActivity(), "Authentication failed.",
                                              Toast.LENGTH_SHORT).show();
                                      mProgressBar.setVisibility(View.GONE);
                                      textWait.setVisibility(View.GONE);
                                  }
                              }
                          });
              }
          }
      });
  }
    private void initSettingButton(){
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(getContext(), SettingActivity.class);
                startActivity(mIntent);
            }
        });
    }
    private Boolean isAccountValid(String account){
        Pattern emailPattern= Patterns.EMAIL_ADDRESS;
        return emailPattern.matcher(account).matches();
    }
    //If the user is logged in and email is verified, navigate to message fragment.
    //If the user is logged in but email is not verified, log out.
    private void updateUI(FirebaseUser firebaseUser){
        if(firebaseUser!=null){
            if(firebaseUser.isEmailVerified()) {
                Fragment myFragment = new MyFragment();
                mainActivity.replaceFragmentInsideFragment(myFragment);
            } else {
                mAuth.signOut();
                Toast.makeText(getActivity(), "Please verify your email", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void updateUIForOnStart(FirebaseUser firebaseUser){
        if(firebaseUser!=null){
            if(firebaseUser.isEmailVerified()) {
                Fragment messageFragment = new MessageFragment();
                mainActivity.replaceFragmentInsideFragment(messageFragment);
            } else {
                mAuth.signOut();
            }
        }
    }
}
