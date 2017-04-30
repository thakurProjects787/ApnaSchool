package com.projects.thakur.apnaschool.AdminUser;

public class NewUserDetails {

    private String newuserID="";
    private String newEmailID="";
    private String newUserType="";
    private String joinDate="";


    public NewUserDetails()
    {
    }


    public String getNewuserID() {
        return newuserID;
    }
    public void setNewuserID(String newuserID) {
        this.newuserID = newuserID;
    }
    public String getNewEmailID() {
        return newEmailID;
    }
    public void setNewEmailID(String newEmailID) {
        this.newEmailID = newEmailID;
    }
    public String getNewUserType() {
        return newUserType;
    }
    public void setNewUserType(String newUserType) {
        this.newUserType = newUserType;
    }
    public String getJoinDate() {
        return joinDate;
    }
    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }
}
