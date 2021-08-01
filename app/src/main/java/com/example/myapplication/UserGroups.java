package com.example.myapplication;

import java.util.Date;

/**/
/*
 *  CLASS DESCRIPTION:
 *    UserGroups class helps to store the data related to user groups.
 *
 *  PURPOSE:
 *    Allows setting and getting of the User groups name based on need.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/
public class UserGroups {

    private String m_groupname;

    public UserGroups() {
    }


    public UserGroups(String a_groupname) {
        this.m_groupname = a_groupname;

    }

    public String getGroup_name() {
        return m_groupname;
    }

    public void setGroup_name(String a_groupname) {
        this.m_groupname = a_groupname;
    }

}
