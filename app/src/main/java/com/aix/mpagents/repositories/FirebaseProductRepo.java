package com.aix.mpagents.repositories;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.Media;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ProductType;
import com.aix.mpagents.models.ShopAddress;
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

public class FirebaseProductRepo {
    private FirebaseFirestore db;
    private String userId;
    private MutableLiveData<Boolean> isProductSaved = new MutableLiveData<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private MutableLiveData<Boolean> isProductDeleted = new MutableLiveData<>();
    private MutableLiveData<List<Media>> getMediaList = new MutableLiveData<>();
    private List<Media> mediaList = new ArrayList<>();
    private MutableLiveData<Boolean> isProductUpdated = new MutableLiveData<>();
    private MutableLiveData<List<ProductInfo>> allProducts = new MutableLiveData<>();
    private ListenerRegistration productsListener;
    private static FirebaseProductRepo instance;

    public static FirebaseProductRepo getInstance() {
        if(instance == null){
            instance = new FirebaseProductRepo();
        }
        return instance;
    }

    public FirebaseProductRepo() {
        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    }

    public void addProduct(ProductInfo productInfo, List<String> photoList, List<Variant> variants){
        try{
            //get shop info
            db.collection(FirestoreConstants.MPARTNER_AGENTS).document(String.valueOf(userId)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        task.getResult();
                        AccountInfo accountInfo = task.getResult().toObject(AccountInfo.class);

                        String doc_id = db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document().getId();
                        ErrorLog.WriteDebugLog("GETTING SHOP INFO");
                        //add merchant info
                        productInfo.setProduct_id(doc_id);
                        productInfo.setMerchant_id(userId);
                        productInfo.setMerchant_name(accountInfo.getFirst_name()+" "+accountInfo.getMiddle_name() + " " + accountInfo.getLast_name());
                        productInfo.setMerchant_address(null);
                        productInfo.setPreview_image("");

                        db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(doc_id).set(productInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    ErrorLog.WriteDebugLog("Product Saved");
                                    uploadPhotoList(photoList,doc_id);
                                    uploadVariants(variants,doc_id);
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

    private void uploadVariants(List<Variant> variants, String product_id) {
        for (Variant variant : variants){
            try{
                String doc_id = db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
                        .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT).document().getId();

                variant.setDate_created(new Date());
                variant.setVariant_id(doc_id);
                db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
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

    public void addVariant(Variant variant, String product_id) {
        try {
            String doc_id = db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT).document().getId();
            variant.setDate_created(new Date());
            variant.setVariant_id(doc_id);
            db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
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

    public void updateVariant(Variant variant, String product_id){
        try{
            HashMap<String,Object> variantMap = new HashMap<>();
            variantMap.put("variant_name", variant.getVariant_name());
            variantMap.put("stock", variant.getStock());
            db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
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

    public void deleteVariant(Variant variant, String product_id) {
        try{
            db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
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

    public MutableLiveData<Boolean> getIsProductSaved(){
        return isProductSaved;
    }

    public void uploadPhotoList(List<String> photoList,String product_id){
        for(int i =0; i<photoList.size();i++){
            uploadProductImageToStorage(Uri.parse(photoList.get(i)),product_id, i, photoList.size()-1);
            ErrorLog.WriteDebugLog("PHOTOLIST ON METHOD "+photoList.size());
        }
    }

    public void uploadProductImageToStorage(Uri path, String product_id, int counter, int listSize) {
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

    public void updateProductPreview(String product_id, int counter, int listSize){

        db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id).collection(FirestoreConstants.MPARTNER_MEDIA)
                .orderBy("date_uploaded")
                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(!task.getResult().isEmpty()) {
                    List<Media> mediaList = task.getResult().toObjects(Media.class);
                    if(!mediaList.isEmpty()) {
                        ErrorLog.WriteDebugLog("MEDIA LIST FIRST UPLOAD " + mediaList.get(0).media_id);
                        HashMap<String,Object> productInfo = new HashMap<>();
                        productInfo.put("preview_image",mediaList.get(0).getPath());
                        db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id).update(productInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        ErrorLog.WriteDebugLog("PRODUCT PREVIEW IMAGE UPDATED");
                                        if(counter == listSize) {
                                            isProductSaved.setValue(true);
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

            String doc_id = db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_MEDIA).document().getId();

            media.setMedia_id(doc_id);
            db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_MEDIA).document(doc_id)
                    .set(media).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        ErrorLog.WriteDebugLog("UPLOADED TO MEDIA");
                        updateProductPreview(product_id, counter, listSize);

                    }else{
                        ErrorLog.WriteErrorLog(task.getException());
                    }
                }
            });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

    }

    public FirestoreRecyclerOptions getProductRecyclerOptions(String status) {
        try{
            Query query = db.collection(FirestoreConstants.MPARTNER_PRODUCTS)
                    .whereEqualTo("merchant_id", userId)
                    .whereEqualTo("product_status", status)
                    .orderBy("dateCreated");
            return new FirestoreRecyclerOptions.Builder<ProductInfo>()
                    .setQuery(query, ProductInfo.class)
                    .build();
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
            return null;
        }
    }

    public FirestoreRecyclerOptions<ProductInfo> getProductSearchRecyclerOptions(String query) {
        try{
            Query newQuery = db.collection(FirestoreConstants.MPARTNER_PRODUCTS)
                    .whereEqualTo("merchant_id", userId)
                    .orderBy("search_name")
                    .startAt(query)
                    .endAt(query + "~");
            return new FirestoreRecyclerOptions.Builder<ProductInfo>()
                    .setQuery(newQuery, ProductInfo.class)
                    .build();
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
            return null;
        }
    }

    public FirestoreRecyclerOptions getCategoriesRecyclerOptions(String product_type) {
        Query query = null;
        if(product_type.equalsIgnoreCase("product")){
            ErrorLog.WriteDebugLog("IS PRODUCT");
            query = db.collection(FirestoreConstants.MPARTNER_PRODUCT_CATEGORY);
        }else if(product_type.equalsIgnoreCase("service")) {
            ErrorLog.WriteDebugLog("IS Service");

            query = db.collection(FirestoreConstants.MPARTNER_SERVICE_CATEGORY);
        }else{
            ErrorLog.WriteDebugLog("IS ERROR");

        }
        return new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
    }

    public FirestoreRecyclerOptions getProductTypeRecyclerOptions() {
        Query query = db.collection(FirestoreConstants.MPARTNER_PRODUCT_TYPE);
        return new FirestoreRecyclerOptions.Builder<ProductType>()
                .setQuery(query, ProductType.class)
                .build();
    }

    public void deleteProduct(ProductInfo productInfo) {

        Map<String, Object> productDelete = new HashMap<>();
        productDelete.put("is_deleted",true);

        db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(productInfo.getProduct_id())
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

    public void changeProductStatus(ProductInfo productInfo, String status) {
        Map<String, Object> productDelete = new HashMap<>();
        productDelete.put("product_status",status);
        db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(productInfo.getProduct_id())
                .update(productDelete).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public MutableLiveData<Boolean> getIsProductDeleted(){
        return isProductDeleted;
    }

    public void getMedia(String product_id) {

        db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
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

    public void updateProduct(ProductInfo productInfo, List<String> newPhotoList, List<String> deletePhotoList){
            //get shop info

        try {
            HashMap<String,Object> productMap = new HashMap<>();
            productMap.put("product_name", productInfo.getProduct_name());
            productMap.put("product_desc", productInfo.getProduct_desc());
            productMap.put("product_price", productInfo.getProduct_price());
            productMap.put("dateUpdated", productInfo.getDateUpdated());
            productMap.put("category_name",productInfo.getCategory_name());
            productMap.put("category_id",productInfo.getCategory_id());


            db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(productInfo.getProduct_id()).update(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        ErrorLog.WriteDebugLog("Product Saved");
                        if (!newPhotoList.isEmpty()) {
                            uploadPhotoList(newPhotoList, productInfo.getProduct_id());
                        }

                        if(!deletePhotoList.isEmpty()){
                            deleteProductImage(deletePhotoList,productInfo.getProduct_id());
                        }


                        isProductUpdated.setValue(true);
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

    public MutableLiveData<Boolean> getIsProductUpdated(){
        return isProductUpdated;
    }

    public void deleteProductImage(List<String> deletePhotoList, String product_id){

        for (int i=0;i<deletePhotoList.size();i++){
            db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_MEDIA).whereEqualTo("path",deletePhotoList.get(i))
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Media> mediaList = task.getResult().toObjects(Media.class);

                            for(int i=0; i<mediaList.size(); i++){
                                deleteImageStorage(mediaList.get(i));
                                deleteMediaFirestore(product_id,mediaList.get(i), i, deletePhotoList.size()-1);

                            }

                        }
                    });
        }
    }

    public void deleteMediaFirestore(String product_id, Media media, int counter, int listSize){
        db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
                .collection(FirestoreConstants.MPARTNER_MEDIA).document(media.getMedia_id())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateProductPreview(product_id,counter,listSize);
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

    public void detachProductsListener(){
        if(productsListener!=null){
            productsListener.remove();
        }
    }

    public void addProductsAllListener(){
        try{
            productsListener = db.collection(FirestoreConstants.MPARTNER_PRODUCTS)
                    .whereEqualTo("merchant_id", userId)
                    .addSnapshotListener((value, error) -> {
                        List<ProductInfo> products = new ArrayList<>();
                        for(DocumentSnapshot product: value.getDocuments()){
                            ProductInfo productInfo = product.toObject(ProductInfo.class);
                            products.add(productInfo);
                        }
                        allProducts.setValue(products);
                    });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public MutableLiveData<List<ProductInfo>> getAllProductInfo() {
        return allProducts;
    }

    public MutableLiveData<ProductType> getProductType(String productType) {
        MutableLiveData<ProductType> type = new MutableLiveData<>();
        try {
            db.collection(FirestoreConstants.MPARTNER_PRODUCT_TYPE)
                    .whereEqualTo("name", productType)
                    .get().addOnSuccessListener(result ->
                    type.setValue(result.getDocuments().get(0).toObject(ProductType.class)))
                    .addOnFailureListener(ErrorLog::WriteErrorLog);
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
        return type;
    }


    public FirestoreRecyclerOptions<Variant> getVariantRecyclerOptions(String product_id) {
        try{
            Query newQuery = db.collection(FirestoreConstants.MPARTNER_PRODUCTS).document(product_id)
                    .collection(FirestoreConstants.MPARTNER_PRODUCT_VARIANT)
                    .orderBy("date_created", Query.Direction.ASCENDING);
            return new FirestoreRecyclerOptions.Builder<Variant>()
                    .setQuery(newQuery, Variant.class)
                    .build();
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
            return null;
        }
    }
}
