package com.aix.mpagents.interfaces;

import com.aix.mpagents.models.ProductInfo;

public interface ProductInterface {

    void onProductClick(ProductInfo productInfo);
    void onEditProduct(ProductInfo productInfo);
}
