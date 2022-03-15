package com.aix.mpagents.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.Media;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ServiceInfo;
import com.aix.mpagents.models.Variant;
import com.aix.mpagents.repositories.FirebaseServiceRepo;
import com.aix.mpagents.repositories.FirebaseVariantRepo;
import com.aix.mpagents.utilities.FirestoreConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceViewModel extends ViewModel {

    private FirebaseServiceRepo serviceRepo = FirebaseServiceRepo.getInstance();
    private FirebaseVariantRepo variantRepo = FirebaseVariantRepo.getInstance();
    private MutableLiveData<ServiceInfo> selectedService = new MutableLiveData<>();
    private MutableLiveData<Category> selectedCategory = new MutableLiveData<>();
    private MutableLiveData<List<String>> pictureSelectedList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Boolean> isProductSaved() {
        return serviceRepo.getIsServiceSaved();
    }

    public void addService(ServiceInfo serviceInfo, List<String> photoList, List<Variant> variants) {
        serviceRepo.addService(serviceInfo,photoList,variants);
    }

    public FirestoreRecyclerOptions<ServiceInfo> getServiceRecyclerOptions() {
        return getServiceRecyclerOptions(ServiceInfo.Status.ONLINE);
    }
    public FirestoreRecyclerOptions<ServiceInfo> getServiceRecyclerOptions(String status) {
        return serviceRepo.getServiceRecyclerOptions(status);
    }

    public void addListener() {
        serviceRepo.addListener();
    }

    public MutableLiveData<List<ServiceInfo>> getAllServicesInfo() {
        return serviceRepo.getAllServicesInfo();
    }

    public void detachListener() {
        serviceRepo.detachListener();
    }

    public MutableLiveData<ServiceInfo> getSelectedService() {
        return selectedService;
    }

    public void changeStatus(ServiceInfo service, String status) {
        serviceRepo.changeServiceStatus(service,status);
    }

    public FirestoreRecyclerOptions<Variant> getVariantRecyclerOptions(String product_id) {
        return variantRepo.getVariantRecyclerOptions(product_id, FirestoreConstants.MPARTNER_SERVICES);
    }

    public MutableLiveData<Category> getSelectedCategory() {
        return selectedCategory;
    }

    public void getMedia(String product_id) {
        serviceRepo.getMedia(product_id);
    }

    public MutableLiveData<List<Media>> getMediaList() {
        return serviceRepo.getGetMediaList();
    }

    public MutableLiveData<Boolean> isProductUpdated() {
        return serviceRepo.getIsServiceUpdated();
    }

    public void updateService(ServiceInfo serviceInfo, List<String> newPhotoList, List<String> deletePhotoList) {
        serviceRepo.updateService(serviceInfo, newPhotoList, deletePhotoList);
    }

    public void addVariant(Variant variant, String service_id) {
        variantRepo.addVariant(variant,service_id,FirestoreConstants.MPARTNER_SERVICES);
    }

    public void deleteVariant(Variant variant, String service_id) {
        variantRepo.deleteVariant(variant, service_id, FirestoreConstants.MPARTNER_SERVICES);
    }

    public void updateVariant(Variant variant, String service_id) {
        variantRepo.updateVariant(variant,service_id, FirestoreConstants.MPARTNER_SERVICES);
    }

    public MutableLiveData<List<String>> getPictureSelectedList() {
        return pictureSelectedList;
    }
}
