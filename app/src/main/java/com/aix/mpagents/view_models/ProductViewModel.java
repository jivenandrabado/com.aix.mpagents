package com.aix.mpagents.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.Media;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ProductType;
import com.aix.mpagents.repositories.FirebaseProductRepo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class ProductViewModel extends ViewModel {

    private FirebaseProductRepo productRepo = new FirebaseProductRepo();
    private MutableLiveData<Category> selectedCategory = new MutableLiveData<>();
    private MutableLiveData<ProductInfo> selectedProduct = new MutableLiveData<>();
    private MutableLiveData<ProductType> selectedProductType = new MutableLiveData<>();

    public void addProduct(ProductInfo productInfo, List<String> photoList){
        productRepo.addProduct(productInfo,photoList);
    }

    public MutableLiveData<Boolean> isProductSaved(){
        return productRepo.getIsProductSaved();
    }

    public FirestoreRecyclerOptions getProductRecyclerOptions(String type,String status){
        return productRepo.getProductRecyclerOptions(type,status);
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

    public void addProductsListener(String productType) {
        productRepo.addProductsWithTypeListener(productType);
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
}
