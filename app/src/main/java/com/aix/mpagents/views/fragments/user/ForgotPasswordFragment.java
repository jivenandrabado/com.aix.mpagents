package com.aix.mpagents.views.fragments.user;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentForgotPasswordBinding;
import com.aix.mpagents.views.fragments.base.BaseLoginFragment;


public class ForgotPasswordFragment extends BaseLoginFragment {

    private FragmentForgotPasswordBinding binding;

    public ForgotPasswordFragment() {
        super(R.layout.fragment_forgot_password);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentForgotPasswordBinding.bind(getView());
        binding.buttonResetPassword.setOnClickListener(btn -> {
            if(!String.valueOf(binding.editTextEmail.getText()).isEmpty()) {
                getLoginViewModel().resetPassword(String.valueOf(binding.editTextEmail.getText()));
            }else showToast("Empty email field");
        });

        getLoginViewModel().getResetResult().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean) showToast("Email has been sent to your email");
        });
    }
}