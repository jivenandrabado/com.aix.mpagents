package com.aix.mpagents.interfaces;

import android.view.View;

import com.aix.mpagents.models.ProductInfo;

public interface ProductInterface {

    void onProductClick(ProductInfo productInfo);
    void onEditProduct(ProductInfo productInfo);
    void onOnlineProduct(ProductInfo productInfo);
    void onInactiveProduct(ProductInfo productInfo);
    void onShareProduct(ProductInfo productInfo);
    void onMoreProductOption(ProductInfo productInfo, View view);
    void onDeleteProduct(ProductInfo productInfo);
    void onEmptyProduct();
    void onNotEmptyProduct();
}
