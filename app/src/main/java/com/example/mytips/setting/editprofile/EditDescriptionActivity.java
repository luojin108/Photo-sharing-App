package com.example.mytips.setting.editprofile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mytips.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditDescriptionActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private EditText mEditText;
    private TextView counterTextView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private String userID;
    public EditDescriptionActivity() {
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_description);
        mEditText=this.findViewById(R.id.edit_description_et);
        counterTextView=this.findViewById(R.id.edit_description_counter_tv);
        setDefaultDescription();
        initFirebaseComponents();
        initCounterTextView();
        initToolBar();
        mEditText.addTextChangedListener(new MyTextWatcher(mEditText, counterTextView, 100));
    }
    private void initToolBar(){
        mToolBar=this.findViewById(R.id.activity_edit_description_tool_bar);
        setSupportActionBar(mToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void setDefaultDescription(){
        Bundle extra=getIntent().getExtras();
        String defaultDescription=extra.getString("descriptionText");
        mEditText.setText(defaultDescription);
    }
    private void initCounterTextView() {
        int textLength=mEditText.getText().toString().length();
        int numberOfCharRemain=100-textLength;
        String numberOfCharRemainString=Integer.toString(numberOfCharRemain);
        counterTextView.setText(numberOfCharRemainString);
    }
    static class MyTextWatcher implements TextWatcher {
        private EditText editText;
        private int maxStringLength;
        private TextView counter;
        MyTextWatcher(EditText editText, TextView counter, int maxStringLength) {
            this.editText=editText;
            this.counter=counter;
            this.maxStringLength=maxStringLength;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            String numberOfCharacterLeft=Integer.toString(maxStringLength-s.length());
            counter.setText(numberOfCharacterLeft);
            if(s.length()>maxStringLength){
                editText.setText(s.subSequence(0,maxStringLength));
            }
        }
    }
    private void initFirebaseComponents(){
        mAuth=FirebaseAuth.getInstance();
        userID=mAuth.getUid();
        mDatabase=FirebaseDatabase.getInstance();
        mDatabaseRef=mDatabase.getReference();
    }
    private void changeUserDescriptionInDatabase(){
        String nameTobeUploaded=mEditText.getText().toString();
        mDatabaseRef.child(getString(R.string.child_user_account_setting))
                    .child(userID)
                    .child(getString(R.string.account_setting_node_description)).setValue(nameTobeUploaded);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.tool_bar_check,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_check){
            changeUserDescriptionInDatabase();
            finish();
        }
        return true;
    }
}
