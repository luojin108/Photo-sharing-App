package com.example.mytips.messagefragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mytips.login.LoginFragment;
import com.example.mytips.MainActivity;
import com.example.mytips.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MessageFragment extends Fragment {
    private FirebaseAuth mAuth;
    private static final String TAG="MessageFragment";
    private Button signout;

    public MessageFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_message, container, false);
        mAuth=FirebaseAuth.getInstance();
        signout=view.findViewById(R.id.tempsignout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                updateUI(currentUser);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //fix the bug that the fragment is selected but the corresponding navigation button is not checked
        try{
            Menu menu=((MainActivity)getActivity()).bottomNavMenu;
            if(!menu.getItem(3).isChecked()){
                menu.getItem(3).setChecked(true);
            }
        }catch (NullPointerException e){
            //
        }
        // Check if user is signed in (non-null) and update UI accordingly.
       FirebaseUser currentUser = mAuth.getCurrentUser();
        //If the user is not logged in, navigate to login fragment
        updateUI(currentUser);
        if (currentUser!=null){
            Log.d(TAG,"UserID:"+ currentUser.getUid()) ;
        }

    }

    //If the user is not logged in, navigate to login fragment
    private void updateUI(FirebaseUser firebaseUser){
        if(firebaseUser==null){
            Fragment loginFragment=new LoginFragment();
            ((MainActivity)getActivity()).replaceFragmentInsideFragment(loginFragment);
        }
    }
}
