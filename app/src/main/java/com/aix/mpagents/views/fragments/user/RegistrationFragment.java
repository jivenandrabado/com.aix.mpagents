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
import com.aix.mpagents.views.fragments.dialogs.ProgressDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;


public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;
    private RegistrationViewModel registrationViewModel;
    private AccountInfo accountInfo;
    private String email, password,confirmPassword;
    private ToastUtil toastUtil;
    private UserSharedViewModel userSharedViewModel;
    private NavController navController;
    private ProgressDialogFragment progressDialogFragment;
    private String dialogTag = "REGISTRATION";

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toastUtil = new ToastUtil();
        navController = Navigation.findNavController(view);
        registrationViewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        accountInfo = new AccountInfo();
        progressDialogFragment = new ProgressDialogFragment();
        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                navController.popBackStack(R.id.registrationFragment,true);
//                navController.navigate(R.id.action_registrationFragment_to_homeFragment);

            }
        });

        registrationViewModel.isRegistered().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (progressDialogFragment.getTag() != null && progressDialogFragment.getTag().equals(dialogTag)) {
                    progressDialogFragment.dismiss();
                }
                if(aBoolean){
                    toastUtil.toastRegistrationSucces(requireContext());
                    registrationViewModel.isRegistered().setValue(false);
                }
            }
        });

        registrationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!s.isEmpty()){
                    toastUtil.makeText(requireContext(), s, Toast.LENGTH_LONG);

                    if (progressDialogFragment.getTag() != null && progressDialogFragment.getTag().equals(dialogTag)) {
                        progressDialogFragment.dismiss();
                    }
                }

            }
        });

        binding.signUpButton.setOnClickListener(btn -> {
            email = String.valueOf(binding.regEmailEditText.getText());
            password = String.valueOf(binding.regPasswordEditText.getText());
            confirmPassword = String.valueOf(binding.regConfirmPasswordEditText.getText());

            if(!isEmptyFields(email,password,confirmPassword)){
                accountInfo.setShop_email(email);
                accountInfo.setShop_name("");
                accountInfo.setLogo("");
                accountInfo.setDate_created(new Date());

                registrationViewModel.registerUser(accountInfo,password);
                progressDialogFragment.show(getChildFragmentManager(),dialogTag);
            }else {
                ErrorLog.WriteDebugLog("Fields are empty");
                toastUtil.makeText(requireContext(),"Fields are empty", Toast.LENGTH_LONG);
            }
        });


    }

    private boolean isEmptyFields(String email, String password, String confirmPassword){

        int passwordLength = 6;
        if (TextUtils.isEmpty(email)){
            toastUtil.makeText(requireContext(), "Empty email", Toast.LENGTH_LONG);
            setAlertHint(binding.textInputEmail);
            return true;
        }else if (TextUtils.isEmpty(password)){
            toastUtil.makeText(requireContext(), "Empty password", Toast.LENGTH_LONG);
            setAlertHint(binding.textInputPassword);
            return true;
        }else if ( password.length() < passwordLength){
            toastUtil.makeText(requireContext(), "Password must be atleast 6 characters", Toast.LENGTH_LONG);
            setAlertHint(binding.textInputPassword);
            return true;
        }else if (confirmPassword.isEmpty()){
            toastUtil.makeText(requireContext(), "Confirm Password Empty", Toast.LENGTH_LONG);
            setAlertHint(binding.textInputConfirmPassword);
            return true;
        }else if(!confirmPassword.equals(password)){
            toastUtil.makeText(requireContext(), "Password does not match", Toast.LENGTH_LONG);
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