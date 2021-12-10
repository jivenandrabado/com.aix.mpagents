package com.aix.mpagents.view_models;

import android.content.Intent;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.Media;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ProductType;
import com.aix.mpagents.models.Variant;
import com.aix.mpagents.repositories.FirebaseProductRepo;
import com.aix.mpagents.repositories.FirebaseVariantRepo;
import com.aix.mpagents.utilities.FirestoreConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class ProductViewModel extends ViewModel {

    private FirebaseVariantRepo variantRepo = FirebaseVariantRepo.getInstance();
    private FirebaseProductRepo productRepo = FirebaseProductRepo.getInstance();
    private MutableLiveData<Category> selectedCategory = new MutableLiveData<>();
    private MutableLiveData<ProductInfo> selectedProduct = new MutableLiveData<>();
    private MutableLiveData<ProductType> selectedProductType = new MutableLiveData<>();

    public void addProduct(ProductInfo productInfo, List<String> photoList, List<Variant> variants){
        productRepo.addProduct(productInfo,photoList,variants);
    }

    public MutableLiveData<Boolean> isProductSaved(){
        return productRepo.getIsProductSaved();
    }

    public FirestoreRecyclerOptions getProductRecyclerOptions(String status){
        return productRepo.getProductRecyclerOptions(status);
    }

    public FirestoreRecyclerOptions<ProductInfo> getProductSearchRecyclerOptions(String query) {
        return productRepo.getProductSearchRecyclerOptions(query.toLowerCase());
    }

    public FirestoreRecyclerOptions getCategoriesRecyclerOptions(String product_type){
        return productRepo.getCategoriesRecyclerOptions(product_type);
    }

    public FirestoreRecyclerOptions getProductTypeRecyclerOptions(){
        return productRepo.getProductTypeRecyclerOptions();
    }

    public MutableLiveData<Category> getSelectedCategory(){
        return selectedCategory;
    }

    public MutableLiveData<ProductInfo> getSelectedProduct() {
        return selectedProduct;
    }

    public void getMedia(String product_id){
        productRepo.getMedia(product_id);
    }

    public MutableLiveData<List<Media>> getMediaList() {
        return productRepo.getGetMediaList();
    }

        public void deleteProduct(ProductInfo productInfo){
        productRepo.deleteProduct(productInfo);
    }

    public MutableLiveData<Boolean> isProductDeleted(){
        return productRepo.getIsProductDeleted();
    }


    public void updateProduct(ProductInfo productInfo ,List<String> newPhotoList, List<String> deletePhotoList){
        productRepo.updateProduct(productInfo,newPhotoList,deletePhotoList);
    }

    public void changeProductStatus(ProductInfo productInfo, String status) {
        productRepo.changeProductStatus(productInfo,status);
    }

    public MutableLiveData<Boolean> isProductUpdated(){
        return productRepo.getIsProductUpdated();
    }

    public MutableLiveData<ProductType> getSelectedProductType(){
        return selectedProductType;
    }


    public MutableLiveData<List<ProductInfo>> getAllProductInfo() {
        return productRepo.getAllProductInfo();
    }

    public void addProductsListener() {
        productRepo.addProductsAllListener();
    }


    public void detachProductsListener() {
        productRepo.detachProductsListener();
    }

    public MutableLiveData<ProductType> getProductType(String productType) {
        return productRepo.getProductType(productType);
    }

    public void updateVariant(Variant variant, String product_id) {
        variantRepo.updateVariant(variant, product_id, FirestoreConstants.MPARTNER_PRODUCTS);
    }

    public void addVariant(Variant variant, String product_id) {
        variantRepo.addVariant(variant, product_id, FirestoreConstants.MPARTNER_PRODUCTS);
    }

    public void deleteVariant(Variant variant, String product_id) {
        variantRepo.deleteVariant(variant, product_id, FirestoreConstants.MPARTNER_PRODUCTS);
    }

    public FirestoreRecyclerOptions<Variant> getVariantRecyclerOptions(String product_id) {
        return productRepo.getVariantRecyclerOptions(product_id);
    }

    public void chooseImage(ActivityResultLauncher<Intent> activityResult) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResult.launch(intent);
    }
}
