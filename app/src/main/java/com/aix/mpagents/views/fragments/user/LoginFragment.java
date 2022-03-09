package com.aix.mpagents.views.fragments.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentLoginBinding;
import com.aix.mpagents.helpers.FirebaseUserHelper;
import com.aix.mpagents.interfaces.LoginInterface;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.NetworkUtil;
import com.aix.mpagents.utilities.ToastUtil;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.fragments.base.BaseLoginFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


public class LoginFragment extends BaseLoginFragment implements LoginInterface {

    private FragmentLoginBinding binding;

    private UserSharedViewModel userSharedViewModel;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentLoginBinding.bind(getView());
        binding.setLoginInterface(this);
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
    }

    @Override
    public void isUserLogin(Boolean isLogin) {
        if (isLogin) {
            navController.navigate(R.id.action_loginFragment_to_homeFragment3);
            showLoading(false);
        }
    }

    @Override
    public void onLoginWithUsernamePasswordClick() {
        if(!isEmptyFields()) {
            showLoading(true);
            getLoginViewModel().usernamePasswordLogin(getEmail(), getPassword());
        }
    }

    @Override
    public void onSignUpClick() {
        navController.navigate(R.id.action_loginFragment_to_registrationFragment);
    }

    @Override
    public void onForgotPasswordClick() {
        navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
    }

    @Override
    public void onLoginAsGuestClick() {
        getLoginViewModel().loginAsGuest();
    }

    @Override
    public void onLoginWithGoogle() {
        firebaseUserHelper.signInWithGoogle(requireActivity(), activityResultLauncherGoogle);
    }

    @Override
    public void onLoginMobile() {
        navController.navigate(R.id.action_loginFragment_to_phoneLoginFragment);
    }

    private boolean isEmptyFields(){
        if (!getEmail().isEmpty() && !getPassword().isEmpty()) {
            return false;
        }else if (getEmail().isEmpty() && getPassword().isEmpty()){
            showToast("Empty email and password");
            return true;
        }else if (getEmail().isEmpty()){
            showToast("Empty email");
            return true;
        }else if (getPassword().isEmpty()){
            showToast("Empty password");
            return true;
        }
        return true;

    }

    private String getEmail(){
        return String.valueOf(binding.editTextUsername.getText()).trim();
    }

    private String getPassword(){
        return String.valueOf(binding.editTextPassword.getText()).trim();
    }
}