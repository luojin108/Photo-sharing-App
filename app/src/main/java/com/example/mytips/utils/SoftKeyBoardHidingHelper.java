package com.example.mytips.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyBoardHidingHelper {
    public static void hideSoftKeyBoard(Activity activity){
        InputMethodManager inputMethodManager=(InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus()!=null){
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
        }
    }
}
