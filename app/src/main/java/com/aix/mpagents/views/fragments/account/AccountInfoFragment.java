package com.aix.mpagents.views.fragments.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentAccountInformationBinding;
import com.aix.mpagents.interfaces.AccountInfoInterface;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.LayoutViewHelper;
import com.aix.mpagents.utilities.ToastUtil;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.fragments.dialogs.UploadDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AccountInfoFragment extends Fragment implements AccountInfoInterface {
    private NavController navController;
    private FragmentAccountInformationBinding binding;
    private AccountInfoViewModel accountInfoViewModel;
    private UserSharedViewModel userSharedViewModel;
    private ToastUtil toastUtil;
    private UploadDialog uploadDialog;

    private LayoutViewHelper layoutViewHelper;

    public AccountInfoFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountInformationBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        accountInfoViewModel = new ViewModelProvider(requireActivity()).get(AccountInfoViewModel.class);
        binding.setAccountInfoInterface(this);
        toastUtil = new ToastUtil();
        layoutViewHelper = new LayoutViewHelper(requireActivity());
        uploadDialog = new UploadDialog();

        initAccount();

        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    if(!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isAnonymous()) {
                        ErrorLog.WriteDebugLog("Display user profile");
                        //init user info
//                        accountInfoViewModel.getShopInfo();

                        accountInfoViewModel.addAccountInfoSnapshot();
                    }else{
                        ErrorLog.WriteDebugLog("Init anonymous user");
                        accountInfoViewModel.detachAccountInfoListener();
                    }
                }else{
                    ErrorLog.WriteDebugLog("No User");
                }
            }
        });

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave();
            }
        });

        binding.textViewStoreLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        accountInfoViewModel.getAccountInfo().observe(getViewLifecycleOwner(), new Observer<AccountInfo>() {
            @Override
            public void onChanged(AccountInfo accountInfo) {
                initHasUser(accountInfo);
            }
        });

        binding.editAddGovernmentID.setOnClickListener(v -> {
            if(((TextInputEditText) v).getText().toString().isEmpty())
                navController.navigate(R.id.action_businessProfileFragment_to_addGovernmentIDFragment);
            else {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setTitle("Government ID")
                        .setMessage("It seems that you already submitted an ID,\n Do you want to submit new ID?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            navController.navigate(R.id.action_businessProfileFragment_to_addGovernmentIDFragment);
                        })
                        .setNegativeButton("No",(dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .show();
            }
        });

    }


    private void initHasUser(AccountInfo accountInfo) {
//        binding.editTextShopEmail.setText(accountInfo.getShop_email());
        binding.editAddGovernmentID.setText(accountInfo.getGov_id_type_primary());
        if(!accountInfo.getGov_id_type_primary().isEmpty()){
            binding.editAddGovernmentID.setHint("Government ID");
        }

//        if(accountInfo.isIs_individual()){
//            binding.editUserType.setText("Individual");
//        }else if(accountInfo.isIs_corporate()){
//            binding.editUserType.setText("Corporate");
//        }else if(accountInfo.isIs_agent()){
//            binding.editUserType.setText("Agent");
//        }
        binding.editTextFirstName.setText(accountInfo.getFirst_name());
        binding.editTextMiddleName.setText(accountInfo.getMiddle_name());
        binding.editTextLastName.setText(accountInfo.getLast_name());


        //Connected to login
        if(userSharedViewModel.getSignInMethod().equalsIgnoreCase("email")){
            binding.imageButtonEmail.setVisibility(View.GONE);
        }else binding.imageButtonMobileNo.setVisibility(View.GONE);


        binding.editTextAgentEmail.setText(accountInfo.getEmail());
        if(!accountInfo.getMobile_no().isEmpty()){
            binding.editMobileNo.setText(accountInfo.getMobile_no());
        }


        if(!accountInfo.getProfile_pic().isEmpty()){
            Glide.with(requireContext()).load(Uri.parse(accountInfo.getProfile_pic()))
                    .fitCenter()
                    .error(R.drawable.ic_baseline_photo_24).into((binding.imageViewProfilePic));
        }


        binding.editMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String prefix = "+63";

                int count = (editable == null) ? 0 : editable.toString().length();
                if (count < prefix.length()) {

                    binding.editMobileNo.setText(prefix);

                    /*
                     * This line ensure when you do a rapid delete (by holding down the
                     * backspace button), the caret does not end up in the middle of the
                     * prefix.
                     */
                    int selectionIndex = Math.max(count + 1, prefix.length());

                    binding.editMobileNo.setSelection(selectionIndex);
                }
            }
        });

    }

    private void initAccount(){

        accountInfoViewModel.updateProfileSuccess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    toastUtil.toastProfileUpdateSuccess(requireContext());
                    accountInfoViewModel.updateProfileSuccess().setValue(false);

                    if(uploadDialog.isVisible()){
                        uploadDialog.dismiss();
                    }
                }
            }
        });

        accountInfoViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!s.isEmpty()) {
                    toastUtil.makeText(requireContext(),
                            s,
                            Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public void onEditFirstName() {
        enableViews(binding.editTextFirstName,binding.imageButtonEditFirstName);
    }

    @Override
    public void onEditMiddleName() {
        enableViews(binding.editTextMiddleName,binding.imageButtonEditMiddleName);

    }

    @Override
    public void onEditLastName() {
        enableViews(binding.editTextLastName,binding.imageButtonEditLastName);
    }


    @Override
    public void onEditMobile() {
        enableViews(binding.editMobileNo,binding.imageButtonMobileNo);
    }

    @Override
    public void onEditEmail() {
        enableViews(binding.editTextAgentEmail,binding.imageButtonEmail);
    }


    private void onSave(){
        try {
            Map<String,Object> account_info = new HashMap<>();

            if(binding.editTextAgentEmail.isEnabled()) {
                account_info.put("email", binding.editTextAgentEmail.getText().toString().trim());
                disableViewsMobile(binding.editTextAgentEmail, binding.imageButtonEmail);
            }

            if(binding.editTextFirstName.isEnabled()) {
                account_info.put("first_name", binding.editTextFirstName.getText().toString().trim());
                disableViewsMobile(binding.editTextFirstName, binding.imageButtonEditFirstName);
            }

            if(binding.editTextMiddleName.isEnabled()) {
                account_info.put("middle_name", binding.editTextMiddleName.getText().toString().trim());
                disableViewsMobile(binding.editTextMiddleName, binding.imageButtonEditMiddleName);
            }

            if(binding.editTextLastName.isEnabled()) {
                account_info.put("last_name", binding.editTextLastName.getText().toString().trim());
                disableViewsMobile(binding.editTextLastName, binding.imageButtonEditLastName);
            }

            if(binding.editMobileNo.isEnabled()){
                if(!isMobileNoValid(String.valueOf(binding.editMobileNo.getText()).trim())) {
                    account_info.put("mobile_no", String.valueOf(binding.editMobileNo.getText()).trim());
                    disableViewsMobile(binding.editMobileNo, binding.imageButtonMobileNo);
                }
            }


            accountInfoViewModel.updateAgentInfo(account_info);

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    private void enableViews(TextInputEditText textInputEditText, ImageButton imageButton){
        textInputEditText.requestFocus();
        textInputEditText.setEnabled(true);
        imageButton.setVisibility(View.GONE);

        binding.buttonSave.setVisibility(View.VISIBLE);
    }


    private void disableViewsMobile(TextInputEditText textInputEditText, ImageButton imageButton){
        binding.buttonSave.setVisibility(View.GONE);
        textInputEditText.setEnabled(false);
        imageButton.setVisibility(View.VISIBLE);

    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        chooseImageActivityResult.launch(intent);
    }

    private ActivityResultLauncher<Intent> chooseImageActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();

                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                // your code for multiple image selection
                                accountInfoViewModel.uploadToFirebaseStorage(imageUri);
                            }
                        } else {
                            Uri uri = data.getData();
                            // your codefor single image selection
                            ErrorLog.WriteDebugLog("DATA RECEIVED "+uri);

                            uploadDialog.show(getChildFragmentManager(),"UPLOAD_DIALOG");
                            accountInfoViewModel.uploadToFirebaseStorage(uri);

                        }
                    }
                }
            }
    );

    private boolean isMobileNoValid(String mobile_no){
        int mobileNoLength = 13;
        if(TextUtils.isEmpty(mobile_no) || mobile_no.length() < mobileNoLength || mobile_no.length() > mobileNoLength){
            toastUtil.makeText(requireContext(), "Invalid mobile no.", Toast.LENGTH_LONG);
            return true;
        }

        return false;


    }

    @Override
    public void onResume() {
        super.onResume();
//        layoutViewHelper.showActionBar();
    }

    @Override
    public void onStop() {
        super.onStop();
//        layoutViewHelper.hideActionBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accountInfoViewModel.detachAccountInfoListener();
    }
}