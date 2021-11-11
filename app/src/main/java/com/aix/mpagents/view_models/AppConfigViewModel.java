package com.aix.mpagents.view_models;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.repositories.AppConfigRepo;

public class AppConfigViewModel extends ViewModel {

    private AppConfigRepo appConfigRepo = new AppConfigRepo();

    public void initAppConfig(Context context){
        appConfigRepo.initAppVersionControl(context);
    }

    public MutableLiveData<Boolean> isForceUpdate(){
        return appConfigRepo.getIsForceUpdate();
    }

    public void detachAppConfigListener(){
        appConfigRepo.detachAppConfigDocumentListener();
    }
}
