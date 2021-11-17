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
import com.aix.mpagents.views.fragments.dialogs.UserTypeDialog;
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
        UserSharedViewModel userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
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
        binding.editTextShopName.setText(accountInfo.getShop_name());
        binding.editTextShopEmail.setText(accountInfo.getShop_email());
        binding.editAddGovernmentID.setText(accountInfo.getGov_id_type_primary());
        if(!accountInfo.getGov_id_type_primary().isEmpty())
            binding.editAddGovernmentID.setHint("Government ID");

        if(accountInfo.isIs_individual()){
            binding.editUserType.setText("Individual");
        }else if(accountInfo.isIs_corporate()){
            binding.editUserType.setText("Corporate");
        }else if(accountInfo.isIs_agent()){
            binding.editUserType.setText("Agent");
        }

        if(!accountInfo.getMobile_no().isEmpty()){
            binding.editMobileNo.setText(accountInfo.getMobile_no());
        }

        if(!accountInfo.getContact_person().isEmpty()){
            binding.editTextContactPerson.setText(accountInfo.getContact_person());
        }

        if(!accountInfo.getLogo().isEmpty()){
            Glide.with(requireContext()).load(Uri.parse(accountInfo.getLogo()))
                    .fitCenter()
                    .error(R.drawable.ic_baseline_photo_24).into((binding.imageViewProfilePic));
        }

        //agent
        if(accountInfo.isIs_agent()){
            binding.textInputLayoutContactPerson.setVisibility(View.GONE);
            binding.imageButtonContactPerson.setVisibility(View.GONE);
            binding.textInputLayoutName.setHint("Agent Name");
        }else{
            binding.textInputLayoutName.setHint("Business Name");
            binding.textInputLayoutContactPerson.setVisibility(View.VISIBLE);

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
    public void onLogoutClick() {
        try{
            accountInfoViewModel.logoutUser(requireActivity());
//            navController.navigate(R.id.action_profileFragment_to_homeFragment2);

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    @Override
    public void onEditName() {
        enableViewsName();
    }

    @Override
    public void onEditUserType() {
        UserTypeDialog userTypeDialog = new UserTypeDialog();
        userTypeDialog.show(getChildFragmentManager(),"USER TYPE DIALOG");
    }

    @Override
    public void onEditMobile() {
        enableViewsMobile();
    }

    @Override
    public void onEditContactPerson() {
        enableViewsContactperson();
    }

    private void onSave(){
        try {
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.setShop_name(binding.editTextShopName.getText().toString().trim());
            Map<String,Object> account_info = new HashMap<>();

            if(binding.editTextShopName.isEnabled()) {
                account_info.put("shop_name", accountInfo.getShop_name());
                disableViewsName();
            }

            if(binding.editMobileNo.isEnabled()){
                if(!isMobileNoValid(String.valueOf(binding.editMobileNo.getText()).trim())) {
                    account_info.put("mobile_no", String.valueOf(binding.editMobileNo.getText()).trim());
                    disableViewsMobile();
                }
            }

            if(binding.editTextContactPerson.isEnabled()){
                account_info.put("contact_person", String.valueOf(binding.editTextContactPerson.getText()).trim());
                disableViewsContactperson();
            }

            accountInfoViewModel.updateShopInfo(account_info);

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    private void enableViewsName(){
        binding.editTextShopName.requestFocus();
        binding.buttonSave.setVisibility(View.VISIBLE);
        binding.editTextShopName.setEnabled(true);
        binding.imageButtonEditShopName.setVisibility(View.GONE);
    }

    private void disableViewsName(){
        binding.buttonSave.setVisibility(View.GONE);
        binding.editTextShopName.setEnabled(false);
        binding.imageButtonEditShopName.setVisibility(View.VISIBLE);

    }

    private void enableViewsMobile(){
        binding.editMobileNo.requestFocus();
        binding.buttonSave.setVisibility(View.VISIBLE);
        binding.editMobileNo.setEnabled(true);
        binding.imageButtonMobileNo.setVisibility(View.GONE);
    }

    private void disableViewsMobile(){
        binding.buttonSave.setVisibility(View.GONE);
        binding.editMobileNo.setEnabled(false);
        binding.imageButtonMobileNo.setVisibility(View.VISIBLE);

    }

    private void enableViewsContactperson(){
        binding.buttonSave.setVisibility(View.VISIBLE);
        binding.editTextContactPerson.setEnabled(true);
        binding.imageButtonContactPerson.setVisibility(View.GONE);
    }

    private void disableViewsContactperson(){
        binding.buttonSave.setVisibility(View.GONE);
        binding.editTextContactPerson.setEnabled(false);
        binding.imageButtonContactPerson.setVisibility(View.VISIBLE);

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