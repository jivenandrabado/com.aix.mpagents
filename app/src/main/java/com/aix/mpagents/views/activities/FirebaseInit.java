package com.aix.mpagents.views.activities;

import android.app.Application;

import com.aix.mpagents.utilities.ErrorLog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseInit extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseApp.initializeApp(this);
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true)
                    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build();
            firebaseFirestore.setFirestoreSettings(settings);
            ErrorLog.WriteDebugLog("Firebase initialized");
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }
}
