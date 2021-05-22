package com.example.myapplication;

import java.util.Date;

public class UserGroups {

    String group_name;
    Integer total_cases;

    public UserGroups() {
    }

    public UserGroups(String group_name, Integer total_cases) {
        this.group_name = group_name;
        this.total_cases=total_cases;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Integer getTotal_cases() {
        return total_cases;
    }

    public void setTotal_cases(Integer total_cases) {
        this.total_cases = total_cases;
    }

    public void increase_cases(){
         this.total_cases++;
    }
}
