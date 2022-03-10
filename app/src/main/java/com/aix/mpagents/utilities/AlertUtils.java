package com.aix.mpagents.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ServiceInfo;


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

    public static void serviceAlert(Context context, ServiceInfo serviceInfo, DialogInterface.OnClickListener onclick, String status) {
        serviceAlert(context, serviceInfo,onclick,status,"");
    }

    public static void serviceAlert(Context context,
                                    ServiceInfo serviceInfo,
                                    DialogInterface.OnClickListener onclick,
                                    String status, String message) {

        new AlertDialog.Builder(context)
                .setTitle(serviceInfo.getService_name())
                .setMessage(message.isEmpty() ? "Are you sure you want to make this item " + status + "?" : message)
                .setPositiveButton("Ok", onclick)
                .setNegativeButton("Cancel", onclick)
                .show();
    }


    public static void duplicationAlert(Context context, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle("Duplicate")
                .setMessage("The product has duplicate. Do you want to update it?")
                .setPositiveButton("Ok", onClickListener)
                .setNegativeButton("Cancel", onClickListener)
                .show();
    }

    public static void addProductServiceExit(Context context, DialogInterface.OnClickListener onClickListener, String title) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage("Are you sure you want to leave this page? All the details will be cleared.")
                .setPositiveButton("Ok", onClickListener)
                .setNegativeButton("Cancel", onClickListener)
                .show();
    }
}
