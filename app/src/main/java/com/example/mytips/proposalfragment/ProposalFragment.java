package com.example.mytips.proposalfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mytips.MainActivity;
import com.example.mytips.R;

public class ProposalFragment extends Fragment {

    public ProposalFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_proposal, container, false);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        //fix the bug that the fragment is selected but the corresponding navigation button is not checked
        try{
            Menu menu=((MainActivity)getActivity()).bottomNavMenu;
            if(!menu.getItem(1).isChecked()){
                menu.getItem(1).setChecked(true);
            }
        }catch (NullPointerException e){
            //
        }
    }
}
