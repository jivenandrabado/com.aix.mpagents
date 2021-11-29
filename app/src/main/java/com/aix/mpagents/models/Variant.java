package com.aix.mpagents.models;

import java.util.Date;

public class Variant {
    public String variant_id;
    public String variant_name;
    public int stock;
    public Date date_created;

    public String getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(String variant_id) {
        this.variant_id = variant_id;
    }

    public String getVariant_name() {
        return variant_name;
    }

    public void setVariant_name(String variant_name) {
        this.variant_name = variant_name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }
}
