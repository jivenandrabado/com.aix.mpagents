package com.aix.mpagents.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.repositories.FirebaseLoginRepo;

public class LoginViewModel extends ViewModel{

    MutableLiveData<Boolean> resetSuccess;
    FirebaseLoginRepo firebaseLoginRepo = new FirebaseLoginRepo();
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
}
