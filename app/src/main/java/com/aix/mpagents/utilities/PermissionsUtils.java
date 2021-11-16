package com.aix.mpagents.utilities;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class PermissionsUtils {
    private static PermissionsUtils permissionsUtils = null;

    public static PermissionsUtils getInstance() {
        if (permissionsUtils == null)
            permissionsUtils = new PermissionsUtils();
        return permissionsUtils;
    }


    public boolean isCameraAndStoragePermissionGranted(FragmentActivity fragmentActivity) {
        boolean cameraPermission =
                ContextCompat.checkSelfPermission(fragmentActivity, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED;
        boolean storagePermission =
                ContextCompat.checkSelfPermission(fragmentActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
        return cameraPermission && storagePermission;
    }

    public void askCameraAndStoragePermission(ActivityResultLauncher<String[]> requireActivity) {
        requireActivity.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
}
