package com.aix.mpagents.view_models;

import android.app.Activity;
import android.net.Uri;

import androidx.core.util.Supplier;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.interfaces.GovernmentIDInterface;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.models.ShopAddress;
import com.aix.mpagents.repositories.FirebaseLoginRepo;
import com.aix.mpagents.repositories.FirebaseProfileRepo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Map;

public class AccountInfoViewModel extends ViewModel {

    private final FirebaseLoginRepo firebaseLoginRepo = new FirebaseLoginRepo();
    private final FirebaseProfileRepo firebaseProfileRepo = new FirebaseProfileRepo();

    private MutableLiveData<ShopAddress> selectedShopAddress = new MutableLiveData<>();
    private MutableLiveData<String> selectedIdType = new MutableLiveData<>();

    public void logoutUser(Activity activity){
        firebaseLoginRepo.logoutUser(activity);
    }

    public void getShopInfo(){firebaseProfileRepo.getShopInfo();}

    public void addAccountInfoSnapshot(){firebaseProfileRepo.addAccountInfoSnaphotListener();}
    public void detachAccountInfoListener(){firebaseProfileRepo.detachAccountInfoListener();}
    public MutableLiveData<AccountInfo> getAccountInfo(){return firebaseProfileRepo.getAccountInfoMutableLiveData();}


    public void updateShopInfo(Map<String,Object> account_info){firebaseProfileRepo.updateShopInfo(account_info);}
    public MutableLiveData<AccountInfo> getProfileObservable(){return firebaseProfileRepo.getProfileObservable();}
    public MutableLiveData<Boolean> updateProfileSuccess(){return firebaseProfileRepo.updateProfileSuccess;}
    public MutableLiveData<String> getErrorMessage(){return firebaseProfileRepo.errorMessage;}
    public void uploadToFirebaseStorage(Uri path){
        firebaseProfileRepo.uploadToFirebaseStorage(path);

    }

    public void uploadIDtoFirebase(Uri path, GovernmentIDInterface handler){
        firebaseProfileRepo.uploadIDtoFirebase(path,handler);
    }

    public void saveAddress(ShopAddress shopAddress){
        firebaseProfileRepo.saveAddress(shopAddress);
    }

    public void updateAddress(ShopAddress shopAddress){
        firebaseProfileRepo.updateAddress(shopAddress);
    }

    public void deleteAddress(ShopAddress shopAddress){
        firebaseProfileRepo.deleteAddress(shopAddress);
    }

    //address
    public MutableLiveData<Boolean> isAddressUpdated(){
        return firebaseProfileRepo.getIsAddressUpdated();
    }

    public FirestoreRecyclerOptions getShopAddressOptions(){
        return firebaseProfileRepo.getShopAddressFirestoreRecyclerOptions();
    }

    public MutableLiveData<ShopAddress> getSelectedShopAddress(){
        return selectedShopAddress;
    }

}
