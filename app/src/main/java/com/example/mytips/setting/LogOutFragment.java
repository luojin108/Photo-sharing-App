package com.example.mytips.setting;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mytips.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogOutFragment extends DialogFragment implements View.OnClickListener{
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Button yesButton;
    private Button noButton;

    public LogOutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.dialog_fragment_logout,container,false);
       yesButton=view.findViewById(R.id.dialog_yes_button);
       noButton=view.findViewById(R.id.dialog_no_button);
       yesButton.setOnClickListener(this);
       noButton.setOnClickListener(this);
       initFireBaseComponents();
       return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.dialog_yes_button){
            firebaseAuth.signOut();
            if(getActivity()!=null){
                getActivity().finish();
            }
        } else if (v.getId()==R.id.dialog_no_button){
                dismiss();
        }
    }
    private void initFireBaseComponents(){
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window=getDialog().getWindow();
        //window.setWindowAnimations(R.style.dialogue_style);
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER_VERTICAL);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
