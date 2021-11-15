package com.aix.mpagents.models;

import java.util.Date;

public class Order {

    public String order_id;
    public double order_quantity;
    public double order_amount;
    public Date order_date;
    public String order_status;
    public Date order_date_topack;
    public Date order_date_toship;
    public Date order_date_delivered;
    public Date order_date_completed;

    public String customer_id;
    public String customer_name;
    public String contact_number;
    public String contact_email;

    public String delivery_address;

    public String owner_id;
    public String owner_name;

    public String product_id;
    public String product_name;
    public double product_price;
    public String preview_image;
    public boolean product_is_agent = true;


    public String mop_id;
    public String mop_option;


    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public String getPreview_image() {
        return preview_image;
    }

    public void setPreview_image(String preview_image) {
        this.preview_image = preview_image;
    }

    public double getOrder_quantity() {
        return order_quantity;
    }

    public void setOrder_quantity(double order_quantity) {
        this.order_quantity = order_quantity;
    }

    public double getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(double amount) {
        this.order_amount = amount;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getMop_id() {
        return mop_id;
    }

    public void setMop_id(String mop_id) {
        this.mop_id = mop_id;
    }

    public String getMop_option() {
        return mop_option;
    }

    public void setMop_option(String mop_option) {
        this.mop_option = mop_option;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public Date getOrder_date_topack() {
        return order_date_topack;
    }

    public void setOrder_date_topack(Date order_date_topack) {
        this.order_date_topack = order_date_topack;
    }

    public Date getOrder_date_toship() {
        return order_date_toship;
    }

    public void setOrder_date_toship(Date order_date_toship) {
        this.order_date_toship = order_date_toship;
    }

    public Date getOrder_date_delivered() {
        return order_date_delivered;
    }

    public void setOrder_date_delivered(Date order_date_delivered) {
        this.order_date_delivered = order_date_delivered;
    }

    public Date getOrder_date_completed() {
        return order_date_completed;
    }

    public void setOrder_date_completed(Date order_date_completed) {
        this.order_date_completed = order_date_completed;
    }
}
