package com.example.mytips.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytips.MainActivity;
import com.example.mytips.R;
import com.example.mytips.setting.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG="LoginActivity";
    private ProgressBar mProgressBar;
    private TextView textWait;
    private TextView register;
    private EditText inputAccount;
    private EditText inputPassword;
    private Button loginButton;
    private ImageButton settingButton;
    private FirebaseAuth mAuth;
    //private MainActivity mainActivity;
    public LoginActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        mProgressBar=this.findViewById(R.id.activity_login_progress_bar);
        textWait=this.findViewById(R.id.activity_login_text_wait);
        register=this.findViewById(R.id.activity_login_text_register);
        register.setOnClickListener(this);
        inputAccount=this.findViewById(R.id.activity_login_edit_text_email);
        inputPassword=this.findViewById(R.id.activity_login_edit_text_password);
        loginButton=this.findViewById(R.id.activity_login_button);
        loginButton.setOnClickListener(this);
        settingButton=this.findViewById(R.id.activity_login_setting_button);
        initSettingButton();
        progressBarDisappear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //If the user is logged in, navigate to message fragment
        //updateUIForOnStart(currentUser);
    }

    private void progressBarDisappear(){
        mProgressBar.setVisibility(View.GONE);
        textWait.setVisibility(View.GONE);

    }

    private void initSettingButton(){
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(getApplicationContext(), SettingActivity.class);
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
                Intent mIntent=new Intent(this,MainActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            } else {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_login_button:
                String account=inputAccount.getText().toString();
                String password=inputPassword.getText().toString();
                if(TextUtils.isEmpty(account)||!isAccountValid(account)){
                    Toast.makeText(getApplicationContext(),R.string.invalid_email_format_reminder,Toast.LENGTH_SHORT).show();
                }else if ((TextUtils.isEmpty(password))){
                    Toast.makeText(getApplicationContext(),R.string.input_password_reminder,Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    textWait.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(account, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        textWait.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
                break;
            case R.id.activity_login_text_register:
                    Intent intent=new Intent(this, RegisterActivity.class);
                    this.startActivity(intent);
        }

    }
    /*private void updateUIForOnStart(FirebaseUser firebaseUser){
        if(firebaseUser!=null){
            if(firebaseUser.isEmailVerified()) {
                Intent mIntent=new Intent(this,MainActivity.class);
                startActivity(mIntent);
            } else {
                mAuth.signOut();
            }
        }
    }*/
}
