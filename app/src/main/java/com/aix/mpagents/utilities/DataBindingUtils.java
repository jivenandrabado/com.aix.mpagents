package com.aix.mpagents.utilities;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.aix.mpagents.R;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.interfaces.ServiceInterface;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ServiceInfo;

public class DataBindingUtils {

    @BindingAdapter(value = {"android:productOnlineInactive", "android:productInterface"}, requireAll = true)
    public static void productOnlineInactive(TextView view, ProductInfo productInfo, ProductInterface productInterface){
        if(productInfo.getProduct_status().equalsIgnoreCase(ProductInfo.Status.INACTIVE) ||
                productInfo.getProduct_status().equalsIgnoreCase(ProductInfo.Status.DRAFT)){
            view.setText(view.getContext().getString(R.string.makeOnline));
            view.setOnClickListener(v -> productInterface.onOnlineProduct(productInfo));
        }else {
            view.setText(view.getContext().getString(R.string.makeInactive));
            view.setOnClickListener(v -> productInterface.onInactiveProduct(productInfo));
        }
    }

    @BindingAdapter(value = {"android:serviceOnlineInactive", "android:serviceInterface"}, requireAll = true)
    public static void serviceOnlineInactive(TextView view, ServiceInfo serviceInfo, ServiceInterface serviceInterface){
        if(serviceInfo.getService_status().equalsIgnoreCase(ServiceInfo.Status.INACTIVE) ||
                serviceInfo.getService_status().equalsIgnoreCase(ServiceInfo.Status.DRAFT)){
            view.setText(view.getContext().getString(R.string.makeOnline));
            view.setOnClickListener(v -> serviceInterface.onOnlineProduct(serviceInfo));
        }else {
            view.setText(view.getContext().getString(R.string.makeInactive));
            view.setOnClickListener(v -> serviceInterface.onInactiveProduct(serviceInfo));
        }
    }
    
    @BindingAdapter("android:variantName")
    public static void variantName(TextView view, String text){
        view.setText("Variant: " + text);
    }

    @BindingAdapter("android:variantStock")
    public static void variantStock(TextView view, int stock){
        view.setText("Stock: " + stock);
    }
}
