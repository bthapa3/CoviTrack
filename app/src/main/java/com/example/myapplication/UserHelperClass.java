package com.example.myapplication;

import java.util.Vector;

public class UserHelperClass {

    String userID,full_name, email,phone_number;


    public UserHelperClass() {
    }



    public UserHelperClass(String userID, String full_name, String email, String phone_number) {
        this.full_name = full_name;
        this.email = email;
        this.phone_number = phone_number;
        this.userID=userID;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID=userID;
    }




}
