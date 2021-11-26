package com.aix.mpagents.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.repositories.FirebaseLoginRepo;
import com.google.firebase.auth.FirebaseAuth;

public class UserSharedViewModel extends ViewModel {

    private FirebaseLoginRepo firebaseLoginRepo = new FirebaseLoginRepo();


    public MutableLiveData<Boolean> isUserLoggedin(){
        return firebaseLoginRepo.isUserLoggedIn();
    }

    public FirebaseAuth.AuthStateListener initAuthListener(){
       return firebaseLoginRepo.initFirebaseAuthListener();
    }


    public String getSignInMethod() {
        return firebaseLoginRepo.getSignInMethod();
    }
}
