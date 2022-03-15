package com.aix.mpagents.views.fragments.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.helpers.FirebaseUserHelper;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.LoginViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.fragments.dialogs.ProgressDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public abstract class BaseLoginFragment extends BaseFragment{

    private LoginViewModel loginViewModel;

    protected FirebaseUserHelper firebaseUserHelper;

    protected ActivityResultLauncher<Intent> activityResultLauncherGoogle = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    showLoading(true);

                    Intent data = result.getData();

                    ErrorLog.WriteDebugLog("Activity result received" + data);

                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);

                        ErrorLog.WriteDebugLog("firebaseAuthWithGoogle:" + account.getId());

                        AccountInfo accountInfo = new AccountInfo();

                        ErrorLog.WriteDebugLog("firebaseAuthWithGoogle:" + account.getId());

                        loginViewModel.loginWithGoogle(account.getIdToken(), accountInfo);

                    } catch (ApiException e) {

                        showLoading(false);

                        ErrorLog.WriteDebugLog("Google sign in failed");

                    }

                }else ErrorLog.WriteDebugLog("Activity result error" +result.getResultCode());
            });

    public BaseLoginFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        firebaseUserHelper = new FirebaseUserHelper();

        loginViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {

            if(!message.isEmpty()){
                showLoading(false);

                showToast(message);

                loginViewModel.getErrorMessage().setValue("");

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    protected LoginViewModel getLoginViewModel(){
        return loginViewModel;
    }
}
