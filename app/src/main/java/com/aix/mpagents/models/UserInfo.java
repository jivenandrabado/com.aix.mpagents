package com.aix.mpagents.models;

public class UserInfo {

    public String first_name;
    public String middle_name;
    public String last_name;
    public String email;
    public String mobile_no;
    public String userId;
    public int def_payment_type;


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Integer getDef_payment_type() {
        return def_payment_type;
    }

    public void setDef_payment_type(int def_payment_type) {
        this.def_payment_type = def_payment_type;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "first_name='" + first_name + '\'' +
                ", middle_name='" + middle_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", mobile_no='" + mobile_no + '\'' +
                ", userId='" + userId + '\'' +
                ", def_payment_type=" + def_payment_type +
                '}';
    }
}
