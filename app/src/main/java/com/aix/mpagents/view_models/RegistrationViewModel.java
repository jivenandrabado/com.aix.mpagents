package com.aix.mpagents.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.repositories.FirebaseLoginRepo;
import com.aix.mpagents.repositories.FirebaseRegistrationRepo;


public class RegistrationViewModel extends ViewModel {

    private final FirebaseLoginRepo firebaseLoginRepo = new FirebaseLoginRepo();
    private final FirebaseRegistrationRepo firebaseRegistrationRepo = new FirebaseRegistrationRepo(firebaseLoginRepo);
    public void registerUser(AccountInfo accountInfo, String password){
        firebaseRegistrationRepo.registerUser(password, accountInfo);
    }
    public MutableLiveData<Boolean> isRegistered(){return firebaseRegistrationRepo.getIsRegistered();}
    public MutableLiveData<String> getErrorMessage(){return firebaseRegistrationRepo.getErrorMessage();}
}
