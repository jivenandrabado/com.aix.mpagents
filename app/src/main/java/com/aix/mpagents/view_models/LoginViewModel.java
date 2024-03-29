package com.aix.mpagents.view_models;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.repositories.FirebaseLoginRepo;

public class LoginViewModel extends ViewModel{

    MutableLiveData<Boolean> resetSuccess;
    private MutableLiveData<Boolean> isResendAvailable = new MutableLiveData<>();
    FirebaseLoginRepo firebaseLoginRepo = FirebaseLoginRepo.getInstance();
    
    public void usernamePasswordLogin(String email, String password){
        firebaseLoginRepo.loginUserUsernamePassword(email,password);
    }

    public void resetPassword(String email){
        firebaseLoginRepo.resetPassword(email);
    }

    public MutableLiveData<Boolean> getResetResult(){
        return firebaseLoginRepo.getResetPasswordSuccess();
    }

    public void loginWithGoogle(String idToken, AccountInfo accountInfo){
        firebaseLoginRepo.loginWithGoogle(idToken, accountInfo);
    }

    public void loginAsGuest(){
        firebaseLoginRepo.loginAsGuest();
    }

    public MutableLiveData<String> getErrorMessage(){
        return firebaseLoginRepo.getErrorMessage();
    }

    public MutableLiveData<String> getVerificationId() {
        return firebaseLoginRepo.getVerificationId();
    }

    public void phoneVerificationSetup(String number, FragmentActivity fragmentActivity) {
        firebaseLoginRepo.phoneVerificationSetup(number,fragmentActivity);
    }

    public MutableLiveData<Boolean> getIsResendAvailable() {
        if(isResendAvailable.getValue() == null)
            isResendAvailable.setValue(false);
        return isResendAvailable;
    }

    public void resendLoginPhoneCode(FragmentActivity fragmentActivity) {
        firebaseLoginRepo.resendLoginPhoneCode(fragmentActivity);
    }

    public void loginWithPhone(String code) {
        firebaseLoginRepo.loginWithPhone(code);
    }
}
