package com.aix.mpagents.views.fragments.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.view_models.ProductViewModel;

import java.util.List;

public abstract class BaseProductFragment extends BaseItemsFragment {

    private ProductViewModel productViewModel;

    public BaseProductFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        productViewModel.getAllProductInfo().observe(getViewLifecycleOwner(), this::onAllProductsLoaded);
    }

    public void onAllProductsLoaded(List<ProductInfo> list){}

    @Override
    public void isUserLogin(Boolean isLogin) {
        super.isUserLogin(isLogin);
        if(isLogin) productViewModel.addProductsListener();
    }

    public ProductViewModel getProductViewModel(){
        return productViewModel;
    }

    @Override
    public void onPause() {
        super.onPause();
        productViewModel.detachProductsListener();
    }
}
