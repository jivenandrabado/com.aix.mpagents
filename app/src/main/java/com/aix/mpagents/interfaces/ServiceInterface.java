package com.aix.mpagents.interfaces;

import android.view.View;

import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ServiceInfo;

public interface ServiceInterface {

    void onEditProduct(ServiceInfo service);
    void onOnlineProduct(ServiceInfo service);
    void onInactiveProduct(ServiceInfo service);
    void onShareProduct(ServiceInfo service);
    void onMoreProductOption(ServiceInfo service, View view);
    void onDeleteProduct(ServiceInfo service);
    void onEmptyProduct();
    void onNotEmptyProduct();
}
