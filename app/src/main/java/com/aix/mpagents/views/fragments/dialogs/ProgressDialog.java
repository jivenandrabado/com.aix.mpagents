package com.aix.mpagents.views.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aix.mpagents.databinding.DialogProgressbarBinding;

import java.util.Objects;


public class ProgressDialog extends Dialog {

    private DialogProgressbarBinding dialogProgressbarBinding;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogProgressbarBinding = DialogProgressbarBinding.inflate(getLayoutInflater(),null, false );
        setContentView(dialogProgressbarBinding.getRoot());
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }
}