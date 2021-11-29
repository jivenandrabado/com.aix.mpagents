package com.aix.mpagents.utilities;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.aix.mpagents.R;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.models.ProductInfo;

public class DataBindingUtils {

    @BindingAdapter(value = {"android:productOnlineInactive", "android:productInterface"}, requireAll = true)
    public static void productOnlineInactive(TextView view, ProductInfo productInfo, ProductInterface productInterface){
        if(productInfo.getProduct_status().equalsIgnoreCase(ProductInfo.Status.INACTIVE)){
            view.setText(view.getContext().getString(R.string.online));
            view.setOnClickListener(v -> productInterface.onOnlineProduct(productInfo));
        }else {
            view.setText(view.getContext().getString(R.string.inactive));
            view.setOnClickListener(v -> productInterface.onInactiveProduct(productInfo));
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
