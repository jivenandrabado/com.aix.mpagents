package com.aix.mpagents.models;

import java.util.Date;

public class AccountInfo {

    public String shop_name;
    public String logo;
    public String shop_email;
    public String shop_id;
    public Date date_created;
    public boolean is_agent = false;
    public boolean is_corporate = false;
    public boolean is_individual = false;
    public boolean is_mdm = false;
    public String mobile_no = "";
    public String contact_person = "";
    public String gov_id_no_primary = "";
    public String gov_id_no_secondary = "";
    public String gov_id_type_primary = "";
    public String gov_id_type_secondary = "";
    public String gov_id_primary = "";
    public String gov_id_secondary = "";

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_email() {
        return shop_email;
    }

    public void setShop_email(String shop_email) {
        this.shop_email = shop_email;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isIs_agent() {
        return is_agent;
    }

    public void setIs_agent(boolean is_agent) {
        this.is_agent = is_agent;
    }

    public boolean isIs_corporate() {
        return is_corporate;
    }

    public void setIs_corporate(boolean is_corporate) {
        this.is_corporate = is_corporate;
    }

    public boolean isIs_individual() {
        return is_individual;
    }

    public void setIs_individual(boolean is_individual) {
        this.is_individual = is_individual;
    }

    public boolean isIs_mdm() {
        return is_mdm;
    }

    public void setIs_mdm(boolean is_mdm) {
        this.is_mdm = is_mdm;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getGov_id_no_primary() {
        return gov_id_no_primary;
    }

    public void setGov_id_no_primary(String gov_id_no_primary) {
        this.gov_id_no_primary = gov_id_no_primary;
    }

    public String getGov_id_no_secondary() {
        return gov_id_no_secondary;
    }

    public void setGov_id_no_secondary(String gov_id_no_secondary) {
        this.gov_id_no_secondary = gov_id_no_secondary;
    }

    public String getGov_id_type_primary() {
        return gov_id_type_primary;
    }

    public void setGov_id_type_primary(String gov_id_type_primary) {
        this.gov_id_type_primary = gov_id_type_primary;
    }

    public String getGov_id_type_secondary() {
        return gov_id_type_secondary;
    }

    public void setGov_id_type_secondary(String gov_id_type_secondary) {
        this.gov_id_type_secondary = gov_id_type_secondary;
    }

    public String getGov_id_primary() {
        return gov_id_primary;
    }

    public void setGov_id_primary(String gov_id_primary) {
        this.gov_id_primary = gov_id_primary;
    }

    public String getGov_id_secondary() {
        return gov_id_secondary;
    }

    public void setGov_id_secondary(String gov_id_secondary) {
        this.gov_id_secondary = gov_id_secondary;
    }
}
