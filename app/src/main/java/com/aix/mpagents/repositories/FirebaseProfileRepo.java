package com.aix.mpagents.repositories;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.util.Supplier;
import androidx.lifecycle.MutableLiveData;

import com.aix.mpagents.interfaces.GovernmentIDInterface;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.models.ShopAddress;
import com.aix.mpagents.utilities.AgentStatusENUM;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.FirestoreConstants;
import com.aix.mpagents.utilities.ToastUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseProfileRepo {

    private FirebaseAuth mAuth;
    private ToastUtil toastUtil;
    private FirebaseFirestore db;
    private final MutableLiveData<AccountInfo> userInfoMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Boolean> updateProfileSuccess = new MutableLiveData<>();
    public final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference().child("MPAgents");
    private final MutableLiveData<Boolean> isAddressUpdated = new MutableLiveData<>();
    private ListenerRegistration accountInfoListener;
    private MutableLiveData<AccountInfo> accountInfoMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ShopAddress>> allAddresses = new MutableLiveData<>();
    private static FirebaseProfileRepo instance;

    public static FirebaseProfileRepo getInstance() {
        if(instance == null){
            instance = new FirebaseProfileRepo();
        }
        return instance;
    }

    public FirebaseProfileRepo() {
        mAuth = FirebaseAuth.getInstance();
        toastUtil = new ToastUtil();
        db = FirebaseFirestore.getInstance();
    }

    public String getUserID(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public MutableLiveData<AccountInfo> getProfileObservable(){
        return userInfoMutableLiveData;
    }

    public void updateAgentInfo(Map<String,Object> account_info) {
        try{
            db.collection(FirestoreConstants.MPARTNER_AGENTS).document(String.valueOf(getUserID())).update(account_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        updateProfileSuccess.setValue(true);
                        ErrorLog.WriteDebugLog("Saved user info");
                    }else{
                        errorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
                        ErrorLog.WriteDebugLog("Failed to save user info "+task.getException());
                    }
                }
            });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void uploadIDtoFirebase(Uri path, GovernmentIDInterface resultHandler){
        try {
            StorageReference mediaRef = storageRef.child("MPAgents/" + getUserID() + "/" + path.getLastPathSegment());
//            InputStream stream = new FileInputStream(new File(path));

            UploadTask uploadTask = mediaRef.putFile(path);
            uploadTask.addOnFailureListener(e -> ErrorLog.WriteDebugLog("FAILED TO UPLOAD " + e))
                    .addOnSuccessListener(taskSnapshot ->
                            mediaRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                            ErrorLog.WriteDebugLog("SUCCESS UPLOAD " + uri);
                                            AccountInfo accountInfo = new AccountInfo();
                                            accountInfo.setProfile_pic(String.valueOf(uri));
                                            Map<String,Object> account_info = new HashMap<>();
                                            account_info.put("gov_id_primary", accountInfo.getProfile_pic());
                                            updateAgentInfo(account_info);
                                            resultHandler.onIdUploaded(uri.toString());
                                        }
                                    )
                            );
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void uploadToFirebaseStorage(Uri path) {
        try {
            StorageReference mediaRef = storageRef.child(getUserID() + "/" + path.getLastPathSegment());
//            InputStream stream = new FileInputStream(new File(path));

            UploadTask uploadTask = mediaRef.putFile(path);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ErrorLog.WriteDebugLog("FAILED TO UPLOAD " + e);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mediaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ErrorLog.WriteDebugLog("SUCCESS UPLOAD " + uri);
                            AccountInfo accountInfo = new AccountInfo();
                            accountInfo.setProfile_pic(String.valueOf(uri));

                            Map<String,Object> account_info = new HashMap<>();
                            account_info.put("profile_pic", accountInfo.getProfile_pic());

                            updateAgentInfo(account_info);
                        }
                    });
                }
            });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
            ErrorLog.WriteDebugLog("ERROR "+ e);
        }
    }


    public void saveAddress(ShopAddress shopAddress) {
        try{
            String address_id = db.collection(FirestoreConstants.MPARTNER_AGENTS).document(String.valueOf(getUserID())).collection(FirestoreConstants.MPARTNER_ADDRESSES)
                    .document().getId();
            shopAddress.setAddress_id(address_id);
            db.collection(FirestoreConstants.MPARTNER_AGENTS).document(String.valueOf(getUserID())).collection(FirestoreConstants.MPARTNER_ADDRESSES)
                    .document(address_id).set(shopAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            ErrorLog.WriteDebugLog("SUCCESS ADDRESS UPDATE");
                            isAddressUpdated.setValue(true);
                        }
                    });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void updateAddress(ShopAddress shopAddress) {
        try{

            db.collection(FirestoreConstants.MPARTNER_AGENTS).document(String.valueOf(getUserID())).collection(FirestoreConstants.MPARTNER_ADDRESSES)
                    .document(shopAddress.getAddress_id()).set(shopAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            ErrorLog.WriteDebugLog("SUCCESS ADDRESS UPDATE");
                            isAddressUpdated.setValue(true);
                        }
                    });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void updateAddressForDefault(ShopAddress shopAddress) {
        try{
            db.collection(FirestoreConstants.MPARTNER_AGENTS).document(String.valueOf(getUserID())).collection(FirestoreConstants.MPARTNER_ADDRESSES)
                    .document(shopAddress.getAddress_id()).set(shopAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            ErrorLog.WriteDebugLog("SUCCESS ADDRESS UPDATE");
                        }
                    });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void deleteAddress(ShopAddress shopAddress) {
        try{
            Map<String,Object> addressMap = new HashMap<>();
            addressMap.put("is_deleted", true);
            addressMap.put("date_deleted", new Date());
            db.collection(FirestoreConstants.MPARTNER_AGENTS).document(String.valueOf(getUserID())).collection(FirestoreConstants.MPARTNER_ADDRESSES)
                    .document(shopAddress.getAddress_id()).update(addressMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            ErrorLog.WriteDebugLog("SUCCESS ADDRESS UPDATE");
                            isAddressUpdated.setValue(true);
                        }
                    });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public MutableLiveData<Boolean> getIsAddressUpdated(){
        return isAddressUpdated;
    }

    public FirestoreRecyclerOptions getShopAddressFirestoreRecyclerOptions() {
        Query query = db.collection(FirestoreConstants.MPARTNER_AGENTS)
                .document(getUserID())
                .collection(FirestoreConstants.MPARTNER_ADDRESSES)
                .whereEqualTo("is_deleted",false);
        return new FirestoreRecyclerOptions.Builder<ShopAddress>()
                .setQuery(query, ShopAddress.class)
                .build();
    }

    public void addAccountInfoSnapshotListener() {

        try{
        accountInfoListener = db.collection(FirestoreConstants.MPARTNER_AGENTS).document(String.valueOf(getUserID()))
                .addSnapshotListener((value, error) -> {
                    if(value.exists()) {
                        AccountInfo accountInfo = value.toObject(AccountInfo.class);
                        accountInfoMutableLiveData.setValue(accountInfo);
                    }

                });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }


    public void detachAccountInfoListener(){
        if(accountInfoListener!=null){
            accountInfoListener.remove();
        }
    }

    public MutableLiveData<AccountInfo> getAccountInfoMutableLiveData(){
        return accountInfoMutableLiveData;
    }

    public MutableLiveData<List<ShopAddress>> getAllAddresses() {
        try{
            List<ShopAddress> addresses = new ArrayList<>();
            db.collection(FirestoreConstants.MPARTNER_AGENTS)
                    .document(getUserID())
                    .collection(FirestoreConstants.MPARTNER_ADDRESSES).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document: task.getResult().getDocuments()){
                        addresses.add(document.toObject(ShopAddress.class));
                        allAddresses.setValue(addresses);
                    }
                }else {
                    ErrorLog.WriteErrorLog(task.getException());
                }
            });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
        return allAddresses;
    }
}
