package com.example.myapplication;

import java.util.Date;

public class UserGroups {

    String group_name;
    Date latest_infection;
    Integer total_cases;

    public UserGroups() {
    }

    public UserGroups(String group_name, Date latest_infection,Integer total_cases) {
        this.group_name = group_name;
        this.latest_infection = latest_infection;
        this.total_cases=total_cases;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Date getLatest_infection() {
        return latest_infection;
    }

    public void setLatest_infection(Date latest_infection) {
        this.latest_infection = latest_infection;
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
