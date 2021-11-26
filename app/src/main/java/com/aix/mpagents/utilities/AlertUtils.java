package com.aix.mpagents.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.aix.mpagents.models.ProductInfo;


public class AlertUtils {
    public static void productAlert(Context context,
                              ProductInfo productInfo,
                              DialogInterface.OnClickListener onclick,
                              String status, String message) {

        new AlertDialog.Builder(context)
                .setTitle(productInfo.getProduct_name())
                .setMessage(message.isEmpty() ? "Are you sure you want to make this item " + status + "?" : message)
                .setPositiveButton("Ok", onclick)
                .setNegativeButton("Cancel", onclick)
                .show();
    }
    public static void productAlert(Context context, ProductInfo productInfo, DialogInterface.OnClickListener onclick, String status) {
        productAlert(context, productInfo,onclick,status,"");
    }
}
