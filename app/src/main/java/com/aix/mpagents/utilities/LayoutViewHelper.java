package com.aix.mpagents.utilities;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LayoutViewHelper extends ContextWrapper {

    Context context;

    public LayoutViewHelper(Context base) {
        super(base);
        this.context = base;
    }

    public void hideActionBar(){
        ActionBar supportActionBar = ((AppCompatActivity) context).getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.hide();
    }

    public void showActionBar(){
        ActionBar supportActionBar = ((AppCompatActivity) context).getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.show();
    }


}
