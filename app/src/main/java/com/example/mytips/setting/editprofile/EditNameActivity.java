package com.example.mytips.setting.editprofile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytips.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditNameActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backButton;
    private ImageButton checkButton;
    private EditText mEditText;
    private TextView counterTextView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private String userID;
    public EditNameActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        backButton=this.findViewById(R.id.edit_name_activity_back_button);
        checkButton=this.findViewById(R.id.activity_edit_name_check_button);
        backButton.setOnClickListener(this);
        checkButton.setOnClickListener(this);
        mEditText=this.findViewById(R.id.edit_name_et);
        counterTextView=this.findViewById(R.id.edit_name_counter_tv);
        initFirebaseComponents();
        counterTextView.setText("30");
        mEditText.addTextChangedListener(new MyTextWatcher(mEditText, counterTextView, 30));
    }
    private void initFirebaseComponents(){
        mAuth=FirebaseAuth.getInstance();
        userID=mAuth.getUid();
        mDatabase=FirebaseDatabase.getInstance();
        mDatabaseRef=mDatabase.getReference();
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.edit_name_activity_back_button){
            finish();
        }else if(v.getId()==R.id.activity_edit_name_check_button){
            Intent mIntent=new Intent(this, EditProfileActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mIntent);
        }
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


}
