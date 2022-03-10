package com.aix.mpagents.views.fragments.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentRegistrationBinding;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.ToastUtil;
import com.aix.mpagents.view_models.RegistrationViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.fragments.base.BaseFragment;
import com.aix.mpagents.views.fragments.dialogs.ProgressDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;


public class RegistrationFragment extends BaseFragment {

    private FragmentRegistrationBinding binding;
    private RegistrationViewModel registrationViewModel;
    private AccountInfo accountInfo;
    private String email, password,confirmPassword;
    private UserSharedViewModel userSharedViewModel;
    private NavController navController;

    public RegistrationFragment() {
        super(R.layout.fragment_registration);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentRegistrationBinding.bind(getView());
        navController = Navigation.findNavController(view);
        registrationViewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        accountInfo = new AccountInfo();

        registrationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!s.isEmpty()){
                    showToast(s);
                    showLoading(false);
                }

            }
        });

        binding.signUpButton.setOnClickListener(btn -> {
            email = String.valueOf(binding.regEmailEditText.getText());
            password = String.valueOf(binding.regPasswordEditText.getText());
            confirmPassword = String.valueOf(binding.regConfirmPasswordEditText.getText());

            if(!isEmptyFields(email,password,confirmPassword)){
                accountInfo.setEmail(email);
                accountInfo.setDate_created(new Date());

                registrationViewModel.registerUser(accountInfo,password);
                showLoading(true);
            }else {
                ErrorLog.WriteDebugLog("Fields are empty");
                showToast("Fields are empty");
            }
        });
    }

    @Override
    public void isUserLogin(Boolean isLogin) {
        if (isLogin) {
            showLoading(false);
            navController.popBackStack(R.id.registrationFragment,true); // welcome message dialog
        }
    }

    private boolean isEmptyFields(String email, String password, String confirmPassword){

        int passwordLength = 6;
        if (TextUtils.isEmpty(email)){
            showToast("Empty email");
            setAlertHint(binding.textInputEmail);
            return true;
        }else if (TextUtils.isEmpty(password)){
            showToast("Empty password");
            setAlertHint(binding.textInputPassword);
            return true;
        }else if ( password.length() < passwordLength){
            showToast("Password must be atleast 6 characters");
            setAlertHint(binding.textInputPassword);
            return true;
        }else if (confirmPassword.isEmpty()){
            showToast("Confirm Password Empty");
            setAlertHint(binding.textInputConfirmPassword);
            return true;
        }else if(!confirmPassword.equals(password)){
            showToast("Password does not match");
            setAlertHint(binding.textInputConfirmPassword);
            return true;
        }

        else{
            return false;
        }

    }

    private void setAlertHint(TextInputLayout textInputLayout){
//        textInputLayout.setText("*");
//        editText.setTextColor(getResources().getColor(R.color.red));
//        textInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));

    }
}