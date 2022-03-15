package com.aix.mpagents.views.fragments.base;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.R;
import com.aix.mpagents.interfaces.EditProductInterface;
import com.aix.mpagents.interfaces.VariantInterface;
import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.ProductType;
import com.aix.mpagents.models.Variant;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.fragments.dialogs.AddVariantDialog;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAddEditServiceItemFragment extends BaseServiceFragment implements VariantInterface, EditProductInterface {

    private ProductViewModel productViewModel;

    private ProductType productTypeModel;

    protected ActivityResultLauncher<Intent> chooseImageActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        List<String> uriList = new ArrayList<>();
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                uriList.add(String.valueOf(imageUri));
                            }
                        } else {
                            Uri uri = data.getData();
                            uriList.add(String.valueOf(uri));
                        }
                        getServiceViewModel().getPictureSelectedList().setValue(uriList);
                    }
                }
            }
    );

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        productViewModel.getProductType("Service").observe(getViewLifecycleOwner(), productType -> {
            if(productType != null) onProductTypeSet(productType);
        });

        getServiceViewModel().getSelectedCategory().observe(getViewLifecycleOwner(), this::onCategorySelected);

        getServiceViewModel().isProductSaved().observe(getViewLifecycleOwner(), isSaved -> {
            if(isSaved) onItemSaved();
        });

        getServiceViewModel().isProductUpdated().observe(getViewLifecycleOwner(), isUpdated -> {
            if(isUpdated) onItemUpdated();
        });

        getServiceViewModel().getPictureSelectedList().observe(getViewLifecycleOwner(), this::onPictureSelected);
    }

    public void onProductTypeSet(ProductType type){
        productTypeModel = type;
        productViewModel.getSelectedProductType().setValue(type);
    }

    public ProductType getProductType(){
        return productTypeModel;
    }

    public abstract void onCategorySelected(Category category);

    public void onItemSaved(){
        showToast("Service Saved!");
        getServiceViewModel().isProductSaved().setValue(false);
    }

    public void onItemUpdated(){
        showToast("Product Saved!");
        getServiceViewModel().isProductUpdated().setValue(false);
    }

    protected void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        chooseImageActivityResult.launch(intent);
    }

    public BaseAddEditServiceItemFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    public abstract void onPictureSelected(List<String> uriList);


    public boolean isEmptyFields(String product_name, String description, String category, String price, List<String> photoList, String product_type){
        if(photoList == null){
            showToast("No product photo");
            return true;
        }

        if (TextUtils.isEmpty(product_name)){
            showToast("Empty Product Name");
            return true;
        }else if (TextUtils.isEmpty(price)){
            showToast("Empty Prodcut Price");
            return true;
        }else if (TextUtils.isEmpty(description)) {
            showToast("Empty Description");
            return true;
        }else if (TextUtils.isEmpty(category)) {
            showToast("Empty Category");
            return true;
        }else if (photoList.isEmpty()) {
            showToast("No product photo");
            return true;
        }else if (TextUtils.isEmpty(product_type)) {
            showToast("Empty Product Type");
            return true;
        }else {
            return false;
        }
    }

}
