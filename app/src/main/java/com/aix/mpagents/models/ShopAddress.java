package com.aix.mpagents.models;

import com.google.type.LatLng;

import java.util.Date;

public class ShopAddress {


    public LatLng latLng;
    public String address_id;
    public String address_name;
    public boolean is_deleted;
    public Date date_deleted;
    public boolean is_business;

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
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
