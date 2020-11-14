package com.example.mytips.setting.editprofile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import com.example.mytips.R;
import com.google.firebase.database.DatabaseReference;

public class OnDateSetListenerForEditingBirthday implements DatePickerDialog.OnDateSetListener {
    private DatabaseReference databaseReference;
    private Context mContext;
    private String userID;

    public OnDateSetListenerForEditingBirthday(DatabaseReference databaseReference,String userID,Context mContext) {
        this.databaseReference=databaseReference;
        this.userID=userID;
        this.mContext=mContext;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int monthPlusOne=month+1;
        String birthdayText = dayOfMonth + "/" + monthPlusOne + "/" + year;
        databaseReference.child(mContext.getString(R.string.child_user_account_setting))
                .child(userID)
                .child(mContext.getString(R.string.account_setting_node_birthday))
                .setValue(birthdayText);
    }
}
