package com.aix.mpagents.models;

import java.util.Date;

public class ProductType {

    public String product_type_id;
    public Date date_created;
    public Date date_updated;
    public boolean is_enabled;
    public String name;

    public String getProduct_type_id() {
        return product_type_id;
    }

    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public Date getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(Date date_updated) {
        this.date_updated = date_updated;
    }

    public boolean isIs_enabled() {
        return is_enabled;
    }

    public void setIs_enabled(boolean is_enabled) {
        this.is_enabled = is_enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
