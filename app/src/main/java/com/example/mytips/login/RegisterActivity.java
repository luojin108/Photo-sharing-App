package com.example.mytips.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytips.model.UserAccountSetting;
import com.example.mytips.model.UserInfo;
import com.example.mytips.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private final static String TAG="RegisterActivity";
    private ProgressBar mProgressBar;
    private TextView textWait;
    private EditText email,userName,password,confirmPassword;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseRef;
    private String defaultImagePhotoUrl="https://firebasestorage.googleapis.com/v0/b/bigmonkey-f8685.appspot.com/o/defaulPhotos%2FbigMonkey.jpg?alt=media&token=05d5c838-0f3e-4138-9075-d78639761c11";
    private String defaultBackgroundImageUri="https://firebasestorage.googleapis.com/v0/b/bigmonkey-f8685.appspot.com/o/defaulPhotos%2Fdefault%20background.jpg?alt=media&token=a0a82769-23b6-4cbc-9f57-a2fedccd3560";

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initWidgets();
        initFirebaseComponents();
        initRegisterButton();
        progressBarDisappear();
    }
    private void initWidgets(){
        mProgressBar=this.findViewById(R.id.activity_register_progress_bar);
        textWait=this.findViewById(R.id.activity_register_text_wait);
        email=this.findViewById(R.id.activity_register_edit_text_email);
        userName=this.findViewById(R.id.activity_register_edit_text_name);
        password=this.findViewById(R.id.activity_register_edit_text_password);
        confirmPassword=this.findViewById(R.id.activity_register_edit_text_confirm_password);
        registerButton=this.findViewById(R.id.activity_register_button);
    }
    private void progressBarDisappear(){
        mProgressBar.setVisibility(View.GONE);
        textWait.setVisibility(View.GONE);

    }
    public void initRegisterButton(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText=email.getText().toString();
                String userNameText=userName.getText().toString();
                String passwordText=password.getText().toString();
                String confirmPasswordText=confirmPassword.getText().toString();
                if(TextUtils.isEmpty(emailText)||!isAccountValid(emailText)){
                    Toast.makeText(getApplicationContext(),R.string.invalid_email_format_reminder,Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(userNameText)){
                    Toast.makeText(getApplicationContext(),R.string.input_username_reminder,Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(passwordText)){
                    Toast.makeText(getApplicationContext(),R.string.input_password_reminder,Toast.LENGTH_SHORT).show();
                } else if (!isPasswordValid(passwordText)){
                    Toast.makeText(getApplicationContext(),R.string.password_length_reminder,Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(confirmPasswordText)){
                    Toast.makeText(getApplicationContext(),R.string.confirm_password_reminder,Toast.LENGTH_SHORT).show();
                } else if (!doesPasswordMatch(passwordText,confirmPasswordText)){
                    Toast.makeText(getApplicationContext(),R.string.password_match_reminder,Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    textWait.setVisibility(View.VISIBLE);
                    checkUserNameExistenceThenSignUp(emailText,userNameText,passwordText);
                }
            }
        });
    }

    //check if the password is valid. Pattern:  Password length should be 8-30
    private Boolean isPasswordValid(String password){
        Pattern pattern=Pattern.compile("^.{8,30}$");
        return pattern.matcher(password).matches();
    }
    private Boolean isAccountValid(String account){
        Pattern emailPattern= Patterns.EMAIL_ADDRESS;
        return emailPattern.matcher(account).matches();
    }
    //check if the password matches
    private Boolean doesPasswordMatch(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }
    //init FirebaseAuth, FirebaseDatabase, DatabaseReference
    private void initFirebaseComponents(){
        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseRef = mFirebaseDatabase.getReference();

    }
    //send verification email and log out if task completes.
    private void sendVerificationEmail(FirebaseUser firebaseUser){
        firebaseUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mAuth.signOut();
                } else {
                    Toast.makeText(getApplicationContext(),"couldn't send verification email", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //check if user name exists, if not existing, sign up
    private void checkUserNameExistenceThenSignUp(final String email, final String username, final String password){
        Query mQuery=mDatabaseRef.child(getString(R.string.child_user_info))
                .orderByChild(getString(R.string.user_info_node_user_name))
                .equalTo(username);
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mProgressBar.setVisibility(View.GONE);
                    textWait.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),R.string.name_exists_reminder,Toast.LENGTH_SHORT).show();
                } else { signUp(email,password,username); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),R.string.server_side_issue,Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * @param email
     * @param password
     *firebase sign up
     */
    private void signUp(final String email, String password, final String userName){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String userID=user.getUid();
                            sendVerificationEmail(user);
                            UserInfo userInfo=new UserInfo(email,userName);
                            UserAccountSetting userAccountSetting=new UserAccountSetting("","",0L, 0L,"",0L,defaultImagePhotoUrl,defaultBackgroundImageUri,"",userName);
                            mDatabaseRef.child(getString(R.string.child_user_info)).child(userID).setValue(userInfo);
                            mDatabaseRef.child(getString(R.string.child_user_account_setting)).child(userID).setValue(userAccountSetting);
                            mProgressBar.setVisibility(View.GONE);
                            textWait.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),R.string.registration_success_reminder,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mProgressBar.setVisibility(View.GONE);
                            textWait.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Registration failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}
