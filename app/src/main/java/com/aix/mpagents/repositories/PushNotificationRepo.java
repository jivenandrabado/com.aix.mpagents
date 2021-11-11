package com.aix.mpagents.repositories;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.aix.mpagents.models.PushNotification;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.FirestoreConstants;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PushNotificationRepo {

    private FirebaseFirestore db;
    private ListenerRegistration pushNotifRegistration;
    private MutableLiveData<PushNotification> pushNotif1 = new MutableLiveData<>();
    private MutableLiveData<PushNotification> pushNotif2 = new MutableLiveData<>();
    private MutableLiveData<PushNotification> pushNotif3 = new MutableLiveData<>();


    public PushNotificationRepo() {
        db = FirebaseFirestore.getInstance();
    }

    public void addSnapshotForPushNotif() {

        try{
            List<PushNotification> pushNotificationList = new ArrayList<>();

            pushNotifRegistration = db.collection(FirestoreConstants.MPARTNER_PUSH_NOTIF).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            PushNotification pushNotification = dc.getDocument().toObject(PushNotification.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    ErrorLog.WriteDebugLog("ADD PUSH NOTIF");
                                    pushNotificationList.add(pushNotification);

                                    if(pushNotification.getPosition() == 1){
                                        pushNotif1.postValue(pushNotification);
                                    }else if (pushNotification.getPosition() == 2){
                                        pushNotif2.postValue(pushNotification);
                                    }else if (pushNotification.getPosition() == 3){
                                        pushNotif3.postValue(pushNotification);
                                    }

                                    break;

                                case REMOVED:
                                    ErrorLog.WriteDebugLog("REMOVE MEDIA");
                                    if(pushNotification.getPosition() == 1){
                                        pushNotif1.postValue(null);
                                    }else if (pushNotification.getPosition() == 2){
                                        pushNotif2.postValue(null);
                                    }else if (pushNotification.getPosition() == 3){
                                        pushNotif3.postValue(null);
                                    }

                                    break;

                                case MODIFIED:
                                    if(pushNotification.getPosition() == 1){
                                        pushNotif1.postValue(pushNotification);
                                    }else if (pushNotification.getPosition() == 2){
                                        pushNotif2.postValue(pushNotification);
                                    }else if (pushNotification.getPosition() == 3){
                                        pushNotif3.postValue(pushNotification);
                                    }
                                    break;

                            }
                        }

                    }

                }
            });
        }catch (Exception e){
            ErrorLog.WriteDebugLog(e);
        }
    }

    public MutableLiveData<PushNotification> getPushNotif1(){
        return pushNotif1;
    }
    public MutableLiveData<PushNotification> getPushNotif2(){
        return pushNotif2;
    }
    public MutableLiveData<PushNotification> getPushNotif3(){
        return pushNotif3;
    }

    public void detachMediaSnapshotListener(){
        if(pushNotifRegistration!=null){
            ErrorLog.WriteDebugLog("PSUH NOTIF SNAPSHOT REMOVED");
            pushNotifRegistration.remove();
        }
    }
}
