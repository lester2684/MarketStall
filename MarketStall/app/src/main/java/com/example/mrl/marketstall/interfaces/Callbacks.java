package com.example.mrl.marketstall.interfaces;

import android.support.v4.app.Fragment;

public interface Callbacks
{
    String getTAG();
    void onReturn(Fragment fragment, String fromTabType);
    void onBackPressedCallback();
    void backPress();
    String getTabType();
}
