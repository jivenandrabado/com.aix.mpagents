package com.aix.mpagents.models;

import com.google.type.LatLng;

import java.util.Date;

public class ShopAddress {

    public String shop_street;
    public String shop_barangay;
    public String shop_citymun;
    public String shop_blkbldgnumber;
    public LatLng latLng;
    public String address_id;
    public boolean is_deleted;
    public Date date_deleted;
    public boolean is_business;

    public String getShop_street() {
        return shop_street;
    }

    public void setShop_street(String shop_street) {
        this.shop_street = shop_street;
    }

    public String getShop_barangay() {
        return shop_barangay;
    }

    public void setShop_barangay(String shop_barangay) {
        this.shop_barangay = shop_barangay;
    }

    public String getShop_citymun() {
        return shop_citymun;
    }

    public void setShop_citymun(String shop_citymun) {
        this.shop_citymun = shop_citymun;
    }

    public String getShop_blkbldgnumber() {
        return shop_blkbldgnumber;
    }

    public void setShop_blkbldgnumber(String shop_blkbldgnumber) {
        this.shop_blkbldgnumber = shop_blkbldgnumber;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Date getDate_deleted() {
        return date_deleted;
    }

    public void setDate_deleted(Date date_deleted) {
        this.date_deleted = date_deleted;
    }

    public boolean isIs_business() {
        return is_business;
    }

    public void setIs_business(boolean is_business) {
        this.is_business = is_business;
    }
}
