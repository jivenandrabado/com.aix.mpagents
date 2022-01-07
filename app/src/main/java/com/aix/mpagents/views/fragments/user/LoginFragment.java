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
import androidx.lifecycle.Observer;
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
import com.aix.mpagents.view_models.LoginViewModel;
import com.aix.mpagents.view_models.RegistrationViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.fragments.dialogs.ProgressDialogFragment;
import com.aix.mpagents.views.fragments.dialogs.WelcomeMessageDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


public class LoginFragment extends Fragment implements LoginInterface {

    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private NavController navController;
    private UserSharedViewModel userSharedViewModel;
    private FirebaseUserHelper firebaseUserHelper;
    private ActivityResultLauncher<Intent> activityResultLauncherGoogle, activityResultLauncherFacebook;
    private ProgressDialogFragment progressDialogFragment;

    //facebook
    public final String EMAIL = "email";
//    public CallbackManager callbackManager;
    private ToastUtil toastUtil;
    private String dialogLoginTag = "DialogLoginTag";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnActivityResultForGoogle();
//        initOnActivityResultForFacebook();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toastUtil = new ToastUtil();
        binding.setLoginInterface(this);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        navController = Navigation.findNavController(view);
        firebaseUserHelper = new FirebaseUserHelper();
        progressDialogFragment = new ProgressDialogFragment();
        initLoginListener();
    }

    private void initLoginListener(){
        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                if (progressDialogFragment.getTag() != null && progressDialogFragment.getTag().equals(dialogLoginTag)) {
                    progressDialogFragment.dismiss();
                }

                navController.navigate(R.id.action_loginFragment_to_homeFragment3);
                toastUtil.toastSingInSuccess(requireContext());
            }
        });

        loginViewModel.getErrorMessage().setValue("");
        loginViewModel.getErrorMessage().observe(getViewLifecycleOwner(), s -> {
            if(!s.isEmpty()) {
                toastUtil.makeText(requireContext(),
                        s,
                        Toast.LENGTH_LONG);
                if (progressDialogFragment.getTag() != null && progressDialogFragment.getTag().equals(dialogLoginTag)) {
                    progressDialogFragment.dismiss();
                }
            }
        });

    }

    @Override
    public void onLoginWithUsernamePasswordClick() {
        try {
            if(NetworkUtil.isNetworkAvailable(requireActivity())) {
                if (!isEmptyFields()) {
                    progressDialogFragment.show(getChildFragmentManager(),dialogLoginTag);
                    loginViewModel.usernamePasswordLogin(getEmail(), getPassword());
                }
            }else{
                toastUtil.toastNoInternetConnection(requireContext());
            }
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    @Override
    public void onSignUpClick() {
        navController.navigate(R.id.action_loginFragment_to_registrationFragment);
    }

    @Override
    public void onForgotPasswordClick() {
        if(NetworkUtil.isNetworkAvailable(requireActivity())) {
            navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        }else{
            toastUtil.toastNoInternetConnection(requireContext());
        }
    }

    @Override
    public void onLoginAsGuestClick() {
        if(NetworkUtil.isNetworkAvailable(requireActivity())) {
            progressDialogFragment.show(getChildFragmentManager(),dialogLoginTag);
            loginViewModel.loginAsGuest();
        }else{
            toastUtil.toastNoInternetConnection(requireContext());
        }
    }

    @Override
    public void onLoginWithGoogle() {
        try {
            if(NetworkUtil.isNetworkAvailable(requireActivity())) {
                firebaseUserHelper.signInWithGoogle(requireActivity(), activityResultLauncherGoogle);
            }else{
                toastUtil.toastNoInternetConnection(requireContext());
            }
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    @Override
    public void onLoginMobile() {
        navController.navigate(R.id.action_loginFragment_to_phoneLoginFragment);
    }

    private boolean isEmptyFields(){
        if (!getEmail().isEmpty() && !getPassword().isEmpty()) {
            return false;
        }else if (getEmail().isEmpty() && getPassword().isEmpty()){
            toastUtil.makeText(requireContext(), "Empty email and password", Toast.LENGTH_LONG);
            return true;
        }else if (getEmail().isEmpty()){
            toastUtil.makeText(requireContext(), "Empty email", Toast.LENGTH_LONG);
            return true;
        }else if (getPassword().isEmpty()){
            toastUtil.makeText(requireContext(), "Empty password", Toast.LENGTH_LONG);
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

    private void initOnActivityResultForGoogle(){
        try {
            activityResultLauncherGoogle = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
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
                                ErrorLog.WriteDebugLog("Google sign in failed");
                            }

                        }else{
                            ErrorLog.WriteDebugLog("Activity result error" +result.getResultCode());
                        }
                    });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

    }

    private void initOnActivityResultForFacebook(){
//        try {
//            activityResultLauncherFacebook = registerForActivityResult(
//                    new ActivityResultContracts.StartActivityForResult(),
//                    result -> {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Intent data = result.getData();
//                            ErrorLog.WriteDebugLog("Activity result received" + data);
//                            assert result.getData() != null;
//                            callbackManager.onActivityResult(result.getData().getIntExtra("REQUEST_CODE",0),result.getResultCode(),result.getData());
//
//                        }else{
//                            ErrorLog.WriteDebugLog(result.getData()+"");
//                        }
//                    });
//        }catch (Exception e){
//            ErrorLog.WriteErrorLog(e);
//        }

    }

}