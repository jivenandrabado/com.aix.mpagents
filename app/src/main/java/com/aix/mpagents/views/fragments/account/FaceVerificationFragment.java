package com.aix.mpagents.views.fragments.account;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentFaceVerificationBinding;
import com.aix.mpagents.utilities.ErrorLog;
import com.yoti.mobile.android.capture.face.ui.FaceCaptureListener;
import com.yoti.mobile.android.capture.face.ui.models.camera.CameraState;
import com.yoti.mobile.android.capture.face.ui.models.camera.CameraStateListener;
import com.yoti.mobile.android.capture.face.ui.models.face.FaceCaptureConfiguration;
import com.yoti.mobile.android.capture.face.ui.models.face.FaceCaptureResult;
import com.yoti.mobile.android.capture.face.ui.models.face.FaceCaptureState;
import com.yoti.mobile.android.capture.face.ui.models.face.ImageQuality;

public class FaceVerificationFragment extends Fragment {

    private FragmentFaceVerificationBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFaceVerificationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        initFaceCaptureConfigs();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initFaceCaptureConfigs() {
        Rect scanning_region = new Rect(20, 200, 700, 800);
        FaceCaptureConfiguration configuration =
                new FaceCaptureConfiguration(scanning_region,
                        ImageQuality.MEDIUM,
                        true,
                        true,
                        3);

        binding.faceCapture.startCamera(getViewLifecycleOwner(), new CameraStateListener() {
            @Override
            public void onCameraState(@NonNull CameraState cameraState) {
                ErrorLog.WriteDebugLog("onCameraState" + cameraState);
            }
        });

        binding.faceCapture.startAnalysing(configuration, new FaceCaptureListener() {
            @Override
            public void onFaceCaptureResult(@NonNull FaceCaptureResult faceCaptureResult) {
                ErrorLog.WriteDebugLog("onFaceCaptureResult" + faceCaptureResult.getState() );
            }
        });
    }
}