package com.aix.mpagents.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.models.PushNotification;
import com.aix.mpagents.repositories.PushNotificationRepo;

public class PushNotificationViewModel extends ViewModel {

    private final PushNotificationRepo pushNotificationRepo ;

    public PushNotificationViewModel() {
        this.pushNotificationRepo = new PushNotificationRepo();
    }

    public void addSnapshotForPushNotif(){
        pushNotificationRepo.addSnapshotForPushNotif();
    }

    public MutableLiveData<PushNotification> getPushNotif1(){
        return pushNotificationRepo.getPushNotif1();
    }
    public MutableLiveData<PushNotification> getPushNotif2(){
        return pushNotificationRepo.getPushNotif2();
    }
    public MutableLiveData<PushNotification> getPushNotif3(){
        return pushNotificationRepo.getPushNotif3();
    }

    public void detachMediaSnapshotListener() {
        pushNotificationRepo.detachMediaSnapshotListener();
    }
}
