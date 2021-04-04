package com.example.myapplication;

import java.util.Vector;

public class Users {

    String userID,full_name, email,phone_number,group1,group2,group3,group4,usertype;
    Integer total_groups;
    Boolean infected, transferrisk;



    public Users() {
    }

    public Users(String userID, String full_name, String email, String phone_number, String group1, String group2, String group3, String group4, Integer total_groups,String usertype,Boolean infected,Boolean transferrisk) {
        this.userID = userID;
        this.full_name = full_name;
        this.email = email;
        this.phone_number = phone_number;
        this.group1 = group1;
        this.group2 = group2;
        this.group3 = group3;
        this.group4 = group4;
        this.total_groups = total_groups;
        this.usertype=usertype;
        this.infected=infected;
        this.transferrisk=transferrisk;
    }


    public String getGroup1() {
        return group1;
    }

    public void setGroup1(String group1) {
        this.group1 = group1;
    }

    public String getUsertype() {
        return usertype;
    }


    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getGroup2() {
        return group2;
    }

    public void setGroup2(String group2) {
        this.group2 = group2;
    }

    public String getGroup3() {
        return group3;
    }

    public void setGroup3(String group3) {
        this.group3 = group3;
    }

    public String getGroup4() {
        return group4;
    }

    public void setGroup4(String group4) {
        this.group4 = group4;
    }

    public Integer getTotal_groups() {
        return total_groups;
    }

    public void setTotal_groups(Integer total_groups) {
        this.total_groups = total_groups;
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

    public Boolean getInfected() {
        return infected;
    }

    public void setInfected(Boolean infected) {
        this.infected = infected;
    }
    public Boolean getTransferrisk() {
        return transferrisk;
    }

    public void setTransferrisk(Boolean tranferrisk) {
        this.transferrisk = tranferrisk;
    }

}