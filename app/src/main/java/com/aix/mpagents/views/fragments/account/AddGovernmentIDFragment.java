package com.aix.mpagents.views.fragments.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentAddGovernmentIDBinding;
import com.aix.mpagents.interfaces.GovernmentIDInterface;
import com.aix.mpagents.utilities.PermissionsUtils;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.views.fragments.dialogs.IdTypeDialog;
import com.aix.mpagents.views.fragments.dialogs.MediaSelectorDialog;
import com.aix.mpagents.views.fragments.dialogs.UploadDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddGovernmentIDFragment extends Fragment {

    private FragmentAddGovernmentIDBinding binding;
    private PermissionsUtils permissionsUtils;
    private MediaSelectorDialog mediaSelector;
    private ArrayAdapter adapter;
    private AccountInfoViewModel accountInfoViewModel;
    private Uri selectedIDUri;
    private UploadDialog uploadDialog;

    private ActivityResultLauncher<String[]> permissionResultHandler = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                for(Map.Entry<String,Boolean> permission: result.entrySet()){
                    if(!permission.getValue()) return;
                }
                mediaSelector();
            }
    );

    private ActivityResultLauncher<Intent> onPhotoSelected = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    mediaSelector.dismiss();
                    if(result.getData().getData() == null){
                        Bitmap sampleBitmap = (Bitmap) result.getData().getExtras().get("data");
                        binding.imageViewIdPreview.setImageBitmap(sampleBitmap);
                    }else {
                        Glide.with(this)
                                .asBitmap()
                                .load(result.getData().getData())
                                .into(new CustomTarget<Bitmap>(){
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        binding.imageViewIdPreview.setImageBitmap(resource);
                                    }
                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                                });
                    }
                    binding.imageViewIdPreview.setVisibility(View.VISIBLE);
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddGovernmentIDBinding.inflate(inflater,container,false);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        accountInfoViewModel = new ViewModelProvider(this).get(AccountInfoViewModel.class);
        permissionsUtils = PermissionsUtils.getInstance();
        uploadDialog = new UploadDialog();
        mediaSelector = new MediaSelectorDialog(onPhotoSelected);
        binding.imageViewIdPreview.setAspectRatio(3375,2125);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                requireContext().getResources().getStringArray(R.array.id_types));
        initListener();
        return binding.getRoot();
    }

    private void initListener() {

//        accountInfoViewModel.getAccountInfo().observe(getViewLifecycleOwner(), result -> {
//
//        });

        binding.relativeLayoutIdContainer.setOnClickListener(v -> {
            if(permissionsUtils.isCameraAndStoragePermissionGranted(requireActivity()))
                mediaSelector();
            else permissionsUtils.askCameraAndStoragePermission(permissionResultHandler);
        });

        binding.editTextIdType.setOnClickListener(v -> {
            new IdTypeDialog(adapter,binding.editTextIdType).show(requireActivity().getSupportFragmentManager(), "ID_TYPE_SELECTOR");
        });

        binding.editTextIdNumber.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_DONE)
                saveID();
            return true;
        });
    }

    public void saveID(){
        Map<String,Object> account_info = new HashMap<>();
        try {
            selectedIDUri = generateUriFromBitmap(binding.imageViewIdPreview.getCroppedImage());
        } catch (IOException e) {
            System.out.println("saveID "+e.getLocalizedMessage());
            e.printStackTrace();
        }

        if(selectedIDUri == null ||
                binding.editTextIdType.getText().toString().isEmpty() ||
                binding.editTextIdNumber.getText().toString().isEmpty()){
            Toast.makeText(requireContext(), "Please complete the form" + selectedIDUri, Toast.LENGTH_SHORT).show();
        } else {
            uploadDialog.show(getChildFragmentManager(), "UPLOAD_DIALOG");
            accountInfoViewModel.uploadIDtoFirebase(selectedIDUri, new GovernmentIDInterface(){
                @Override
                public void onIdUploaded(String link) {
                    fileUploadedView(account_info);
                }
                @Override
                public void onError(String error) { }
            });
        };
    }

    private void fileUploadedView(Map<String, Object> account_info) {
        disableCropGesture(true);
        account_info.put("gov_id_no_primary",binding.editTextIdNumber.getText().toString());
        account_info.put("gov_id_type_primary",binding.editTextIdType.getText().toString());
        accountInfoViewModel.updateAgentInfo(account_info);
        uploadDialog.dismiss();
        Toast.makeText(requireContext(), "Government ID submitted!", Toast.LENGTH_SHORT).show();
        requireActivity().onBackPressed();
    }

    private Uri generateUriFromBitmap(Bitmap croppedImage) throws IOException {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File cameraDir = new File(dir, "Camera/");
        if (!cameraDir.exists()) cameraDir.mkdir();
        File file =  new File(cameraDir, "LK_"+ System.currentTimeMillis() + ".png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        croppedImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        return Uri.fromFile(file);
    }

    private void mediaSelector() {
        mediaSelector.show(requireActivity().getSupportFragmentManager(), "MEDIA_SELECTOR");
    }

    private void disableCropGesture(Boolean isDisable){
        binding.imageViewIdPreview.setDoubleTapEnabled(!isDisable);
        binding.imageViewIdPreview.setScaleEnabled(!isDisable);
        binding.imageViewIdPreview.setScrollEnabled(!isDisable);
    }
}