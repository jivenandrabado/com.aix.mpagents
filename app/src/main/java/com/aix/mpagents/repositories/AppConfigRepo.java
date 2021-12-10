package com.aix.mpagents.repositories;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.lifecycle.MutableLiveData;

import com.aix.mpagents.models.AppConfig;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.FirestoreConstants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class AppConfigRepo {

    private final FirebaseFirestore db;
    private MutableLiveData<Boolean> isForceUpdate = new MutableLiveData<>();
    private ListenerRegistration appConfigRegistratin;
    private static AppConfigRepo instance;

    public AppConfigRepo() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static AppConfigRepo getInstance() {
        if(instance == null){
            instance = new AppConfigRepo();
        }
        return instance;
    }

    public void initAppVersionControl(Context context){
        try {

            appConfigRegistratin = db.collection(FirestoreConstants.MPARTNER_APP_CONFIG).document(FirestoreConstants.MPARTNER_VERSION_CONTROL)
                    .addSnapshotListener((value, error) -> {
                        AppConfig appConfig = value.toObject(AppConfig.class);
                        if(appConfig != null) {
                            if (appConfig.getForce_update()) {
                                if (appConfig.getVersion_code() != getAppVersion(context)) {
                                    isForceUpdate.postValue(true);
                                } else {
                                    isForceUpdate.postValue(false);
                                }
                            }else if(!appConfig.getForce_update()){
                                isForceUpdate.postValue(false);
                            }
                        }
                    });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }


    public void detachAppConfigDocumentListener(){
        if(appConfigRegistratin!=null){
            ErrorLog.WriteDebugLog("APP CONFIG SNAPSHOT REMOVED");
            appConfigRegistratin.remove();
        }
    }

    private int getAppVersion(Context context){
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            ErrorLog.WriteErrorLog(e);
        }

        return 0;

    }

    public MutableLiveData<Boolean> getIsForceUpdate(){
        return isForceUpdate;
    }
}
