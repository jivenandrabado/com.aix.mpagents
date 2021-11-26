package com.aix.mpagents.repositories;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.SigninENUM;
import com.aix.mpagents.utilities.StringUtils;
import com.aix.mpagents.utilities.ToastUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FirebaseLoginRepo {

    static MutableLiveData<Boolean> resetPasswordSuccess = new MutableLiveData<>();
    static MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>();
    static MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> verificationId = new MutableLiveData<>();
    static MutableLiveData<String> userPhoneNumber = new MutableLiveData<>();
    private MutableLiveData<PhoneAuthProvider.ForceResendingToken> resendCodeToken = new MutableLiveData<>();

    private final GoogleSignInOptions gso = new GoogleSignInOptions.
            Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
            build();

    private final FirebaseAuth mAuth;
    private final ToastUtil toastUtil;
    private final FirebaseRegistrationRepo firebaseRegistrationRepo;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks loginPhoneCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {}
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            ErrorLog.WriteErrorLog(e);
            ErrorLog.WriteDebugLog(e);
            errorMessage.setValue("Request Time out.");
        }
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            verificationId.setValue(s);
            resendCodeToken.setValue(forceResendingToken);
        }
    };

    public FirebaseLoginRepo() {
        mAuth = FirebaseAuth.getInstance();
        toastUtil = new ToastUtil();
        firebaseRegistrationRepo = new FirebaseRegistrationRepo(this);
    }

    public void loginUserUsernamePassword(String email, String password) {
        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                ErrorLog.WriteDebugLog("signInWithEmail:success");
                                //check if user exist
                                AccountInfo accountInfo = new AccountInfo();
                                accountInfo.setEmail(email);
                                accountInfo.setDate_created(new Date());
                                accountInfo.setAgent_id(mAuth.getUid());
                                firebaseRegistrationRepo.checkUserExist(accountInfo,SigninENUM.NONE);
                            } else {
                                errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
                                isUserLoggedIn.setValue(false);
                            }
                        }
                    });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void resetPassword(String email){
        try {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ErrorLog.WriteDebugLog("Email Sent");
                                resetPasswordSuccess.setValue(task.isSuccessful());
                                resetPasswordSuccess.setValue(false);
                            }else{
                                ErrorLog.WriteDebugLog(task.getException());
                                errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }

                    });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

    }

    public MutableLiveData<Boolean> getResetPasswordSuccess(){
        return resetPasswordSuccess;
    }

    public void logoutUser(Activity activity){
        try {
            FirebaseAuth.getInstance().signOut();
            logoutGoogleAccount(activity);
//            LoginManager.getInstance().logOut();
            toastUtil.toastLogoutSuccess(activity);
            restartActivity(activity);
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    private void restartActivity(Activity activity) {

        //restart activity
        Intent i = activity.getBaseContext().getPackageManager().
                getLaunchIntentForPackage(activity.getBaseContext().getPackageName());

        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            activity.finish();
            activity.startActivity(i);
        }

    }

    public void logoutGoogleAccount(Activity activity){
        try {
            GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(activity,gso);

            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        ErrorLog.WriteDebugLog("Google Account Logged out");
                    }else{
                        ErrorLog.WriteErrorLog(task.getException());
                    }
                }
            });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

    }

    public FirebaseAuth.AuthStateListener initFirebaseAuthListener(){

        try {
            return new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        ErrorLog.WriteDebugLog("User logged in Auth state listener");
                        isUserLoggedIn.setValue(true);
                    } else {
                        ErrorLog.WriteDebugLog("User logged out Auth state listener");
                        isUserLoggedIn.setValue(false);
                    }
                }
            };
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

        return null;

    }

    public MutableLiveData<Boolean> isUserLoggedIn(){
        return isUserLoggedIn;
    }


    public void loginWithGoogle(String idToken, AccountInfo accountInfo){

        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                ErrorLog.WriteDebugLog("signInWithCredential:success");
//                                accountInfo.setShop_email(email);
                                accountInfo.setDate_created(new Date());
                                accountInfo.setFirst_name(StringUtils.capitalize(StringUtils.getFistName(mAuth.getCurrentUser().getDisplayName())));
                                accountInfo.setLast_name(StringUtils.capitalize(StringUtils.getLastName(mAuth.getCurrentUser().getDisplayName())));
                                if(mAuth.getCurrentUser().getPhoneNumber() != null){
                                    accountInfo.setMobile_no(mAuth.getCurrentUser().getPhoneNumber());
                                }
                                accountInfo.setProfile_pic(mAuth.getCurrentUser().getPhotoUrl().toString());
                                accountInfo.setEmail(mAuth.getCurrentUser().getEmail());
                                accountInfo.setDate_created(new Date());
                                accountInfo.setAgent_id(mAuth.getUid());
                                firebaseRegistrationRepo.checkUserExist(accountInfo, SigninENUM.GOOGLE);
                            } else {
                                ErrorLog.WriteErrorLog(task.getException());
                                errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });
        }catch (Exception e){
            System.out.println("loginWithGoogle" + e.getLocalizedMessage());
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void loginAsGuest(){
        try {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                ErrorLog.WriteDebugLog("signInAnonymously:success");
                            } else {
                                // If sign in fails, display a message to the user.
                                ErrorLog.WriteDebugLog("signInAnonymously:failure");
                                errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public MutableLiveData<String> getErrorMessage(){
        return errorMessage;
    }


    public MutableLiveData<String> getVerificationId() {
        return verificationId;
    }

    public void phoneVerificationSetup(String number, FragmentActivity fragmentActivity) {
        try {
            userPhoneNumber.setValue(number);
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(number)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(fragmentActivity)                 // Activity (for callback binding)
                            .setCallbacks(loginPhoneCallback)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }catch (Exception e){
            errorMessage.setValue(e.getLocalizedMessage());
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void resendLoginPhoneCode(FragmentActivity fragmentActivity) {
        try {
            PhoneAuthOptions phoneOptions = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(userPhoneNumber.getValue())
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(fragmentActivity)
                    .setCallbacks(loginPhoneCallback)
                    .setForceResendingToken(resendCodeToken.getValue())
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(phoneOptions);
        }catch (Exception e){
            errorMessage.setValue(e.getLocalizedMessage());
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void loginWithPhone(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId.getValue(), code);
            mAuth.signInWithCredential(credential)
                    .addOnSuccessListener(result -> {
                        ErrorLog.WriteDebugLog("signInWithPhone:success");
                        //check if user exist
                        AccountInfo accountInfo = new AccountInfo();
                        accountInfo.setEmail("");
                        accountInfo.setLast_name("");
                        accountInfo.setMiddle_name("");
                        accountInfo.setLast_name("");
                        accountInfo.setProfile_pic("");
                        accountInfo.setDate_created(new Date());
                        try{
                            accountInfo.setMobile_no(mAuth.getCurrentUser().getPhoneNumber());
                        }catch (Exception e){
                            ErrorLog.WriteErrorLog(e);
                        }
                        accountInfo.setAgent_id(mAuth.getUid());
                        firebaseRegistrationRepo.checkUserExist(accountInfo,SigninENUM.PHONE);
                    }).addOnFailureListener(result -> {
                errorMessage.setValue(result.getLocalizedMessage());
                ErrorLog.WriteErrorLog(result);
            });
        }catch (Exception e){
            errorMessage.setValue(e.getLocalizedMessage());
            ErrorLog.WriteErrorLog(e);
        }
    }

    public String getSignInMethod() {
        try {
            if(mAuth.getCurrentUser().getEmail().isEmpty()) return "Phone";
            else return "Email";
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
            return "Email";
        }
    }
}

