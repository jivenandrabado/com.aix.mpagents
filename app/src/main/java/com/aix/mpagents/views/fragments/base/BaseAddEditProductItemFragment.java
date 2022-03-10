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
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.interfaces.EditProductInterface;
import com.aix.mpagents.interfaces.VariantInterface;
import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.ProductType;
import com.aix.mpagents.view_models.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAddEditProductItemFragment extends BaseProductFragment implements VariantInterface, EditProductInterface {

    private ProductType productTypeModel;

    protected ActivityResultLauncher<Intent> chooseImageActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
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
                    getProductViewModel().getPictureSelectedList().setValue(uriList);
                }
            }
    );

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getProductViewModel().getProductType("Service").observe(getViewLifecycleOwner(), productType -> {
            if(productType != null) onProductTypeSet(productType);
        });

        getProductViewModel().getSelectedCategory().observe(getViewLifecycleOwner(), this::onCategorySelected);

        getProductViewModel().isProductSaved().observe(getViewLifecycleOwner(), isSaved -> {
            if(isSaved) onItemSaved();
        });

        getProductViewModel().isProductUpdated().observe(getViewLifecycleOwner(), isUpdated -> {
            if(isUpdated) onItemUpdated();
        });

        getProductViewModel().getPictureSelectedList().observe(getViewLifecycleOwner(), this::onPictureSelected);
    }

    public void onProductTypeSet(ProductType type){
        productTypeModel = type;
        getProductViewModel().getSelectedProductType().setValue(type);
    }

    public ProductType getProductType(){
        return productTypeModel;
    }

    public abstract void onCategorySelected(Category category);

    public void onItemSaved(){
        showToast("Product Saved!");
        getProductViewModel().isProductSaved().setValue(false);
    }

    public void onItemUpdated(){
        showToast("Product Updated!");
        getProductViewModel().isProductUpdated().setValue(false);
    }

    protected void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        chooseImageActivityResult.launch(intent);
    }

    public BaseAddEditProductItemFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    public abstract void onPictureSelected(List<String> uriList);


    public boolean isEmptyFields(String product_name, String description, String category, String price, List<String> photoList, String product_type){
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
