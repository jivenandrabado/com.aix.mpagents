package com.aix.mpagents.helpers;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.aix.mpagents.utilities.ErrorLog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseUserHelper {

    private static FirebaseAuth mAuth;
    public static  final String googleClientOauth = "654705842577-47r890mtlda3hcj7r874rlruj5nr5ao5.apps.googleusercontent.com";
    private static final int RC_SIGN_IN = 9001;
    public GoogleSignInClient mGoogleSignInClient;

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleClientOauth)
            .requestEmail()
            .build();


    public void signInWithGoogle(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher) {
        try {
            mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

//    public void signInWithFacebook(Activity activity,AccessToken token){
//        ErrorLog.WriteDebugLog("Sign in with facebook helper");

//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        FirebaseAuth.getInstance().signInWithCredential(credential)
//                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            ErrorLog.WriteDebugLog("Sign in with facebook success");
//
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            ErrorLog.WriteErrorLog(task.getException());
//                        }
//                    }
//                });

//    }
}
