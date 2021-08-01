package com.example.myapplication;

import java.util.Vector;

/**/
/*
 *  CLASS DESCRIPTION:
 *    User class helps to store the data related to users.
 *
 *  PURPOSE:
 *      Allows setting and getting of the User's information like userID, name,
 *      email, phone ,groups and usertype.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/
public class Users {

    private String m_userID,m_full_name, m_email,m_phone_number,m_group1,m_group2,m_group3,m_group4,m_usertype;
    private Integer m_total_groups;
    private Boolean m_infected, m_transferrisk;

    //default constructor
    public Users() {
    }

    //constructor for user class.
    public Users(String a_userID, String a_full_name, String a_email, String a_phone_number, String a_group1, String a_group2, String a_group3, String a_group4, Integer a_total_groups,String a_usertype,Boolean a_infected,Boolean a_transferrisk) {
        this.m_userID = a_userID;
        this.m_full_name = a_full_name;
        this.m_email = a_email;
        this.m_phone_number = a_phone_number;
        this.m_group1 = a_group1;
        this.m_group2 = a_group2;
        this.m_group3 = a_group3;
        this.m_group4 = a_group4;
        this.m_total_groups = a_total_groups;
        this.m_usertype=a_usertype;
        this.m_infected=a_infected;
        this.m_transferrisk=a_transferrisk;
    }

    //getter and setter for each methods.
    public String getGroup1() {
        return m_group1;
    }

    public void setGroup1(String a_group1) {
        this.m_group1 = a_group1;
    }

    public String getUsertype() {
        return m_usertype;
    }


    public void setUsertype(String a_usertype) {
        this.m_usertype = a_usertype;
    }

    public String getGroup2() {
        return m_group2;
    }

    public void setGroup2(String a_group2) {
        this.m_group2 = a_group2;
    }

    public String getGroup3() {
        return m_group3;
    }

    public void setGroup3(String a_group3) {
        this.m_group3 = a_group3;
    }

    public String getGroup4() {
        return m_group4;
    }

    public void setGroup4(String a_group4) {
        this.m_group4 = a_group4;
    }

    public Integer getTotal_groups() {
        return m_total_groups;
    }

    public void setTotal_groups(Integer a_total_groups) {
        this.m_total_groups = a_total_groups;
    }

    public String getFull_name() {
        return m_full_name;
    }

    public void setFull_name(String a_full_name) {
        this.m_full_name = a_full_name;
    }

    public String getEmail() {
        return m_email;
    }

    public void setEmail(String a_email) {
        this.m_email = a_email;
    }

    public String getPhone_number() {
        return m_phone_number;
    }

    public void setPhone_number(String a_phone_number) {
        this.m_phone_number = a_phone_number;
    }

    public String getUserID() {
        return m_userID;
    }

    public void setUserID(String a_userID) {
        this.m_userID= a_userID;
    }

    public Boolean getInfected() {
        return m_infected;
    }

    public void setInfected(Boolean a_infected) {
        this.m_infected = a_infected;
    }

    public Boolean getTransferrisk() {
        return m_transferrisk;
    }

    public void setTransferrisk(Boolean a_tranferrisk) {
        this.m_transferrisk = a_tranferrisk;
    }

}