package com.aix.mpagents.views.fragments.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.databinding.BottomSheetDialogMediaSelectorBinding;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MediaSelectorDialog extends BottomSheetDialogFragment {

    private BottomSheetDialogMediaSelectorBinding binding;
    private ActivityResultLauncher<Intent> onSelectedUriHandler;

    public MediaSelectorDialog(ActivityResultLauncher<Intent> onSelectedUriHandler){
        this.onSelectedUriHandler = onSelectedUriHandler;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetDialogMediaSelectorBinding.inflate(inflater, container, false);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    public void openPhotos(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        onSelectedUriHandler.launch(intent);
    }

    public void openCamera(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        onSelectedUriHandler.launch(intent);
    }

}
