package com.aix.mpagents.views.fragments.account;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentAddGovernmentIDBinding;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.PermissionsUtils;

import java.util.Map;

public class AddGovernmentIDFragment extends Fragment {

    private FragmentAddGovernmentIDBinding binding;
    private PermissionsUtils permissionsUtils;

    private ActivityResultLauncher<String[]> permissionResultHandler = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                for(Map.Entry<String,Boolean> permission: result.entrySet()){
                    if(!permission.getValue()) return;
                }
                mediaSelector();
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddGovernmentIDBinding.inflate(inflater,container,false);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        permissionsUtils = PermissionsUtils.getInstance();
        initListener();
        return binding.getRoot();
    }

    private void initListener() {

        binding.relativeLayoutIdContainer.setOnClickListener(v -> {
            if(permissionsUtils.isCameraAndStoragePermissionGranted(requireActivity()))
                mediaSelector();
            else permissionsUtils.askCameraAndStoragePermission(permissionResultHandler);
        });

    }

    private void mediaSelector() {
        Toast.makeText(requireContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
    }

}