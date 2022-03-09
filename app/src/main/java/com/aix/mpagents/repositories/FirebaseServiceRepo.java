package com.aix.mpagents.repositories;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.models.Media;
import com.aix.mpagents.models.ServiceInfo;
import com.aix.mpagents.models.Variant;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.FirestoreConstants;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

public class FirebaseServiceRepo {

    private FirebaseFirestore db;
    private String userId;
    private MutableLiveData<Boolean> isServiceSaved = new MutableLiveData<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private MutableLiveData<Boolean> isProductDeleted = new MutableLiveData<>();
    private MutableLiveData<List<Media>> getMediaList = new MutableLiveData<>();
    private List<Media> mediaList = new ArrayList<>();
    private MutableLiveData<Boolean> isServiceUpdated = new MutableLiveData<>();
    private MutableLiveData<List<ServiceInfo>> allServices = new MutableLiveData<>();
    private ListenerRegistration serviceListener;
    private static FirebaseServiceRepo instance;
    private FirebaseVariantRepo variantRepo;
    private MutableLiveData<List<ServiceInfo>> allService = new MutableLiveData<>();
    private ListenerRegistration servicesListener;

    public static FirebaseServiceRepo getInstance() {
        if(instance == null){
            instance = new FirebaseServiceRepo();
        }
        return instance;
    }

    public FirebaseServiceRepo() {
        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        variantRepo = FirebaseVariantRepo.getInstance();
    }

    public void addService(ServiceInfo serviceInfo, List<String> photoList, List<Variant> variants){
        try{
            //get shop info
            db.collection(FirestoreConstants.MPARTNER_AGENTS).document(String.valueOf(userId)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        task.getResult();
                        AccountInfo accountInfo = task.getResult().toObject(AccountInfo.class);

                        String doc_id = db.collection(FirestoreConstants.MPARTNER_SERVICES).document().getId();
                        ErrorLog.WriteDebugLog("GETTING SHOP INFO");
                        //add merchant info
                        serviceInfo.setService_id(doc_id);
                        serviceInfo.setMerchant_id(userId);
                        serviceInfo.setMerchant_name(accountInfo.getFirst_name()+" "+accountInfo.getMiddle_name() + " " + accountInfo.getLast_name());
                        serviceInfo.setMerchant_address(null);
                        serviceInfo.setPreview_image("");

                        db.collection(FirestoreConstants.MPARTNER_SERVICES).document(doc_id).set(serviceInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    ErrorLog.WriteDebugLog("Product Saved");
                                    uploadPhotoList(photoList,doc_id);
                                    variantRepo.uploadVariants(variants,doc_id, FirestoreConstants.MPARTNER_SERVICES);
                                }else{
                                    ErrorLog.WriteErrorLog(task.getException());
                                    ErrorLog.WriteDebugLog("Product not Saved");
                                }
                            }
                        });


                    }else {
                        ErrorLog.WriteDebugLog(task.getException());
                    }
                }
            });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void uploadPhotoList(List<String> photoList,String product_id){
        for(int i =0; i<photoList.size();i++){
            uploadServiceImageToStorage(Uri.parse(photoList.get(i)),product_id, i, photoList.size()-1);
            ErrorLog.WriteDebugLog("PHOTOLIST ON METHOD "+photoList.size());
        }
    }

    public void uploadServiceImageToStorage(Uri path, String product_id, int counter, int listSize) {
        try{

            StorageReference mediaRef = storageRef.child(userId + "/image" + System.currentTimeMillis());

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
                            addMedia(String.valueOf(uri),product_id, counter,listSize);
                            ErrorLog.WriteDebugLog("SUCCESS UPLOAD " + uri);
                        }
                    });
                }


            });


        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void updateServicePreview(String product_id, int counter, int listSize){

        db.collection(FirestoreConstants.MPARTNER_SERVICES).document(product_id).collection(FirestoreConstants.MPARTNER_MEDIA)
                .orderBy("date_uploaded")
                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(!task.getResult().isEmpty()) {
                    List<Media> mediaList = task.getResult().toObjects(Media.class);
                    if(!mediaList.isEmpty()) {
                        ErrorLog.WriteDebugLog("MEDIA LIST FIRST UPLOAD " + mediaList.get(0).media_id);
                        HashMap<String,Object> serviceInfo = new HashMap<>();
                        serviceInfo.put("preview_image",mediaList.get(0).getPath());
                        db.collection(FirestoreConstants.MPARTNER_SERVICES).document(product_id).update(serviceInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        ErrorLog.WriteDebugLog("PRODUCT PREVIEW IMAGE UPDATED");
                                        if(counter == listSize) {
                                            isServiceSaved.setValue(true);
                                        }
                                    }
                                }
                            });
                    }
                }
            }
        });

    }

    private void addMedia(String path, String product_id, int counter, int listSize) {

        try{
            Media media = new Media();
            media.setType(1);
            media.setPath(path);
            media.setDate_uploaded(new Date());

            String doc_id = db.collection(FirestoreConstants.MPARTNER_SERVICES).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_MEDIA).document().getId();

            media.setMedia_id(doc_id);
            db.collection(FirestoreConstants.MPARTNER_SERVICES).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_MEDIA).document(doc_id)
                    .set(media).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        ErrorLog.WriteDebugLog("UPLOADED TO MEDIA");
                        updateServicePreview(product_id, counter, listSize);

                    }else{
                        ErrorLog.WriteErrorLog(task.getException());
                    }
                }
            });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

    }

    public FirestoreRecyclerOptions getServiceRecyclerOptions(String status) {
        try{
            Query query = db.collection(FirestoreConstants.MPARTNER_SERVICES)
                    .whereEqualTo("merchant_id", userId)
                    .whereEqualTo("service_status", status)
                    .orderBy("dateCreated");
            return new FirestoreRecyclerOptions.Builder<ServiceInfo>()
                    .setQuery(query, ServiceInfo.class)
                    .build();
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
            return null;
        }
    }

    public FirestoreRecyclerOptions getServiceSearchRecyclerOptions(String query) {
        try{
            Query newQuery = db.collection(FirestoreConstants.MPARTNER_SERVICES)
                    .whereEqualTo("merchant_id", userId)
                    .orderBy("search_name")
                    .startAt(query)
                    .endAt(query + "~");
            return new FirestoreRecyclerOptions.Builder<ServiceInfo>()
                    .setQuery(newQuery, ServiceInfo.class)
                    .build();
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
            return null;
        }
    }

    public void deleteService(ServiceInfo serviceInfo) {

        Map<String, Object> productDelete = new HashMap<>();
        productDelete.put("is_deleted",true);

        db.collection(FirestoreConstants.MPARTNER_SERVICES).document(serviceInfo.getService_id())
                .update(productDelete).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ErrorLog.WriteDebugLog("PRODUCT DELETED");
                    isProductDeleted.setValue(true);
                }else{
                    ErrorLog.WriteErrorLog(task.getException());
                }
            }
        });
    }

    public void changeServiceStatus(ServiceInfo serviceInfo, String status) {
        Map<String, Object> serviceHash = new HashMap<>();
        serviceHash.put("service_status",status);
        db.collection(FirestoreConstants.MPARTNER_SERVICES).document(serviceInfo.getService_id())
                .update(serviceHash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ErrorLog.WriteDebugLog("PRODUCT "+ status);
                }else{
                    ErrorLog.WriteErrorLog(task.getException());
                }
            }
        });
    }

    public void getMedia(String service_id) {

        db.collection(FirestoreConstants.MPARTNER_SERVICES).document(service_id)
                .collection(FirestoreConstants.MPARTNER_MEDIA).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    mediaList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mediaList.add(document.toObject(Media.class));
                    }

                    getMediaList.setValue(mediaList);
                }
            }
        });
    }

    public MutableLiveData<List<Media>> getGetMediaList(){
        return getMediaList;
    }

    public void updateService(ServiceInfo serviceInfo, List<String> newPhotoList, List<String> deletePhotoList){
            //get shop info

        try {
            HashMap<String,Object> productMap = new HashMap<>();
            productMap.put("service_name", serviceInfo.getService_name());
            productMap.put("service_desc", serviceInfo.getService_desc());
            productMap.put("service_price", serviceInfo.getService_price());
            productMap.put("dateUpdated", serviceInfo.getDateUpdated());
            productMap.put("category_name",serviceInfo.getCategory_name());
            productMap.put("category_id",serviceInfo.getCategory_id());


            db.collection(FirestoreConstants.MPARTNER_SERVICES).document(serviceInfo.getService_id()).update(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        ErrorLog.WriteDebugLog("Product Saved");
                        if (!newPhotoList.isEmpty()) {
                            uploadPhotoList(newPhotoList, serviceInfo.getService_id());
                        }

                        if(!deletePhotoList.isEmpty()){
                            deleteServiceImage(deletePhotoList,serviceInfo.getService_id());
                        }


                        isServiceUpdated.setValue(true);
                    } else {
                        ErrorLog.WriteErrorLog(task.getException());
                        ErrorLog.WriteDebugLog("Product not Saved");

                    }
                }
            });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

    }

    public MutableLiveData<Boolean> getIsServiceUpdated(){
        return isServiceUpdated;
    }

    public void deleteServiceImage(List<String> deletePhotoList, String service_id){

        for (int i=0;i<deletePhotoList.size();i++){
            db.collection(FirestoreConstants.MPARTNER_SERVICES).document(service_id)
                    .collection(FirestoreConstants.MPARTNER_MEDIA).whereEqualTo("path",deletePhotoList.get(i))
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Media> mediaList = task.getResult().toObjects(Media.class);

                            for(int i=0; i<mediaList.size(); i++){
                                deleteImageStorage(mediaList.get(i));
                                deleteMediaFirestore(service_id,mediaList.get(i), i, deletePhotoList.size()-1);

                            }

                        }
                    });
        }
    }

    public void deleteMediaFirestore(String product_id, Media media, int counter, int listSize){
        db.collection(FirestoreConstants.MPARTNER_SERVICES).document(product_id)
                .collection(FirestoreConstants.MPARTNER_MEDIA).document(media.getMedia_id())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateServicePreview(product_id,counter,listSize);
                    ErrorLog.WriteDebugLog("MEDIA DELETE FROM FIRESTORE");
                }else {
                    ErrorLog.WriteErrorLog(task.getException());
                }
            }
        });
    }

    public void deleteImageStorage(Media media) {
        StorageReference storageReference = storage.getReferenceFromUrl(media.getPath());

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ErrorLog.WriteDebugLog("MEDIA DELETED FROM STORAGE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ErrorLog.WriteErrorLog(e);
            }
        });

    }

    public void detachListener(){
        if(serviceListener !=null){
            serviceListener.remove();
        }
    }

    public MutableLiveData<List<ServiceInfo>> getAllServiceInfo() {
        return allServices;
    }

    public MutableLiveData<Boolean> getIsServiceSaved(){
        return isServiceSaved;
    }

    public void addListener() {
        try{
            servicesListener = db.collection(FirestoreConstants.MPARTNER_SERVICES)
                    .whereEqualTo("merchant_id", userId)
                    .addSnapshotListener((value, error) -> {
                        List<ServiceInfo> services = new ArrayList<>();
                        for(DocumentSnapshot product: value.getDocuments()){
                            ServiceInfo serviceInfo = product.toObject(ServiceInfo.class);
                            services.add(serviceInfo);
                        }
                        allService.setValue(services);
                    });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public MutableLiveData<List<ServiceInfo>> getAllServicesInfo() {
        return allService;
    }


}
