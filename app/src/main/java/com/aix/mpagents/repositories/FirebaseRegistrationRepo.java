package com.aix.mpagents.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.FirestoreConstants;
import com.aix.mpagents.utilities.SigninENUM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class FirebaseRegistrationRepo {

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final FirebaseLoginRepo firebaseLoginRepo;
    private final MutableLiveData<Boolean> isRegistered = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public FirebaseRegistrationRepo(FirebaseLoginRepo firebaseLoginRepo){
        this.firebaseLoginRepo = firebaseLoginRepo;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void registerUser(String password, AccountInfo accountInfo){
        try{
            mAuth.createUserWithEmailAndPassword(accountInfo.getShop_email(), password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                ErrorLog.WriteDebugLog("registration success");
                                ErrorLog.WriteDebugLog("Saving user info...");
                                accountInfo.setShop_id(Objects.requireNonNull(task.getResult().getUser()).getUid());

                                saveRegistrationToUsers(accountInfo,password, SigninENUM.NONE);
                                isRegistered.setValue(true);

                            } else {
                                ErrorLog.WriteDebugLog("registration failed");
                                ErrorLog.WriteErrorLog(task.getException());
                                errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });
        }catch (Exception e){
            errorMessage.setValue(e.toString());
            ErrorLog.WriteErrorLog(e);
        }

    }

    public void saveRegistrationToUsers(AccountInfo accountInfo, String password, SigninENUM signinENUM){
        try {
            db.collection(FirestoreConstants.MPARTNER_MERCHANTS).document(accountInfo.getShop_id()).set(accountInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        switch (signinENUM){
                            case NONE:
                                ErrorLog.WriteDebugLog("Logging in to mpagents");
                                if(mAuth.getCurrentUser() != null) {
                                    firebaseLoginRepo.loginUserUsernamePassword(accountInfo.getShop_email(), password);
                                }
                                break;
                            case GOOGLE:
                                ErrorLog.WriteDebugLog("User info saved from google");
                                break;
                            case FACEBOOK:
                                ErrorLog.WriteDebugLog("User info saved from facebook");
                                break;
                        }
                    } else {
                        ErrorLog.WriteErrorLog(task.getException());
                    }
                }
            });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void checkUserExist(AccountInfo accountInfo, SigninENUM signinENUM){
        try{

            db.collection(FirestoreConstants.MPARTNER_MERCHANTS).document(accountInfo.getShop_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()){
                            ErrorLog.WriteDebugLog("user data exists");

                        }else{
                            ErrorLog.WriteDebugLog("user data does not exists");
                            saveRegistrationToUsers(accountInfo,"",signinENUM);
                        }
                    }
                }
            });



        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public MutableLiveData<Boolean> getIsRegistered(){return isRegistered;}
    public MutableLiveData<String> getErrorMessage(){return errorMessage;}
}
