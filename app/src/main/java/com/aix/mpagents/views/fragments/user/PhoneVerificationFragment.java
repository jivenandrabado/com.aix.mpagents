package com.aix.mpagents.views.fragments.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentPhoneVerificationBinding;
import com.aix.mpagents.view_models.LoginViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;


public class PhoneVerificationFragment extends Fragment {

    private FragmentPhoneVerificationBinding binding;
    private LoginViewModel loginViewModel;
    private NavController navController;
    private UserSharedViewModel userSharedViewModel;
    public MutableLiveData<String> verificationCode = new MutableLiveData<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        initTimer(60);
        initObservers();
        initListeners();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPhoneVerificationBinding.inflate(inflater, container, false);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    private void initObservers() {
        loginViewModel.getIsResendAvailable().observe(getViewLifecycleOwner(), result -> {
            if(result)
                binding.textViewResendVerificationCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.mpagents_base_color));
            else
                binding.textViewResendVerificationCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.silver));
            binding.textViewResendVerificationCode.setEnabled(result);
        });
        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), result -> {
            if(result) navController.navigate(R.id.action_phoneVerificationFragment_to_homeFragment);
        });

        loginViewModel.getErrorMessage().observe(getViewLifecycleOwner(), result -> {
            if(result.isEmpty()) return;
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
            loginViewModel.getErrorMessage().setValue("");
        });


    }
    private void initListeners() {
        binding.textViewResendVerificationCode.requestFocus();
        binding.textViewResendVerificationCode.setOnClickListener(v -> {
            initTimer(60);
            loginViewModel.resendLoginPhoneCode(requireActivity());
        });

        binding.buttonLoginWithPhone.setOnClickListener(v -> {
            if(!isVerificationNotEmpty()) Toast.makeText(requireContext(), "Please enter verification code.", Toast.LENGTH_SHORT).show();
            phoneLogin();
        });
        verificationCode.observe(getViewLifecycleOwner(), result -> phoneLogin());
    }

    private void phoneLogin() {
        UIUtil.hideKeyboard(requireActivity());
        if(isVerificationNotEmpty()){
            loginViewModel.loginWithPhone(verificationCode.getValue());
        }
    }

    private boolean isVerificationNotEmpty() {
        return binding.pinViewVerificationCode.getText().toString().length() == binding.pinViewVerificationCode.getItemCount();
    }

    private void initTimer(int seconds) {
        loginViewModel.getIsResendAvailable().setValue(false);
        Long milliSeconds = seconds * 1000L;
        new CountDownTimer(milliSeconds, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                binding.textViewResendVerificationCode.setText("Resend Code "+ (milliSeconds - (milliSeconds - (l / 1000))) + "s");
            }
            @Override
            public void onFinish() {
                binding.textViewResendVerificationCode.setText(requireContext().getString(R.string.resendCode));
                loginViewModel.getIsResendAvailable().setValue(true);
            }
        }.start();
    }
}