package com.aix.mpagents.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class AccountInfo {

//    agent_id
//
//    name: string
//    profile_pic: string
//    mobile_no:String
//
//    is_active: boolean
//    date_created: Date
//    date_updated: Date
//
//    gov_id_primary:String
//    gov_id_secondary:String
    
    public String agent_id;
    public String first_name = "";
    public String middle_name = "";
    public String last_name = "";

    public String profile_pic = "";
    public String mobile_no = "";
    public String email;

    public Date date_created;
    public Date date_updated;

    public String gov_id_no_primary = "";
    public String gov_id_no_secondary = "";
    public String gov_id_primary = "";
    public String gov_id_secondary = "";
    public String gov_id_type_primary = "";
    public String gov_id_type_secondary = "";
    public String agent_status = "";


    public String getGov_id_type_secondary() {
        return gov_id_type_secondary;
    }

    public String getAgent_status() {
        return agent_status;
    }

    public void setAgent_status(String agent_status) {
        this.agent_status = agent_status;
    }

    public void setGov_id_type_secondary(String gov_id_type_secondary) {
        this.gov_id_type_secondary = gov_id_type_secondary;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
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



    public String getGov_id_no_secondary() {
        return gov_id_no_secondary;
    }

    public void setGov_id_no_secondary(String gov_id_no_secondary) {
        this.gov_id_no_secondary = gov_id_no_secondary;
    }

    public String getGov_id_secondary() {
        return gov_id_secondary;
    }

    public void setGov_id_secondary(String gov_id_secondary) {
        this.gov_id_secondary = gov_id_secondary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGov_id_no_primary() {
        return gov_id_no_primary;
    }

    public void setGov_id_no_primary(String gov_id_no_primary) {
        this.gov_id_no_primary = gov_id_no_primary;
    }

    public String getGov_id_type_primary() {
        return gov_id_type_primary;
    }

    public void setGov_id_type_primary(String gov_id_type_primary) {
        this.gov_id_type_primary = gov_id_type_primary;
    }

    public String getGov_id_primary() {
        return gov_id_primary;
    }

    public void setGov_id_primary(String gov_id_primary) {
        this.gov_id_primary = gov_id_primary;
    }

    public String getFullName(){
        return getFirst_name() + " " + getMiddle_name() + " " + getLast_name();
    }

}
