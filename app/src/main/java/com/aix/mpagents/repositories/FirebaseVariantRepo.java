package com.aix.mpagents.repositories;

import androidx.annotation.NonNull;

import com.aix.mpagents.models.Variant;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.FirestoreConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FirebaseVariantRepo {

    private static FirebaseVariantRepo instance;
    private FirebaseFirestore db;
    private String userId;


    public static FirebaseVariantRepo getInstance() {
        if(instance == null) instance = new FirebaseVariantRepo();
        return instance;
    }

    public FirebaseVariantRepo() {
        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public void uploadVariants(List<Variant> variants, String product_id, String collection) {
        for (Variant variant : variants){
            try{
                String doc_id = db.collection(collection).document(product_id)
                        .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT).document().getId();

                variant.setDate_created(new Date());
                variant.setVariant_id(doc_id);
                db.collection(collection).document(product_id)
                        .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT).document(doc_id)
                        .set(variant).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            ErrorLog.WriteDebugLog("UPLOADED TO VARIANTS");
                        }else{
                            ErrorLog.WriteErrorLog(task.getException());
                        }
                    }
                });
            }catch (Exception e){
                ErrorLog.WriteErrorLog(e);
            }
        }
    }

    public void addVariant(Variant variant, String product_id, String collection) {
        try {
            String doc_id = db.collection(collection).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT).document().getId();
            variant.setDate_created(new Date());
            variant.setVariant_id(doc_id);
            db.collection(collection).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT)
                    .document(doc_id).set(variant).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        ErrorLog.WriteDebugLog("ADDED TO VARIANTS");
                    }else{
                        ErrorLog.WriteErrorLog(task.getException());
                    }
                }
            });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void updateVariant(Variant variant, String product_id, String collection){
        try{
            HashMap<String,Object> variantMap = new HashMap<>();
            variantMap.put("variant_name", variant.getVariant_name());
            variantMap.put("stock", variant.getStock());
            db.collection(collection).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT).document(variant.getVariant_id())
                    .update(variantMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        ErrorLog.WriteDebugLog("UPLOADED TO VARIANTS");
                    }else{
                        ErrorLog.WriteErrorLog(task.getException());
                    }
                }
            });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public void deleteVariant(Variant variant, String product_id, String collection) {
        try{
            db.collection(collection).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT).document(variant.getVariant_id())
                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        ErrorLog.WriteDebugLog("DELETED TO VARIANTS");
                    }else{
                        ErrorLog.WriteErrorLog(task.getException());
                    }
                }
            });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public FirestoreRecyclerOptions<Variant> getVariantRecyclerOptions(String product_id, String collection) {
        try{
            Query newQuery = db.collection(collection).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT);
            return new FirestoreRecyclerOptions.Builder<Variant>()
                    .setQuery(newQuery, Variant.class)
                    .build();
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
            return null;
        }
    }
}
