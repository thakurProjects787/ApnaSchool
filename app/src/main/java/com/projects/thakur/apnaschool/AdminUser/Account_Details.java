package com.projects.thakur.apnaschool.AdminUser;


public class Account_Details {
    private String email_ID="";
    private String user_Type="";
    private String join_Date="";
    private String adminUserID = "";

    public Account_Details()
    {
    }

    public String getAdminUserID() {
        return adminUserID;
    }
    public void setAdminUserID(String adminUserID) {this.adminUserID = adminUserID;}

    public String getEmail_ID() {
        return email_ID;
    }
    public void setEmail_ID(String email_ID) {
        this.email_ID = email_ID;
    }
    public String getUser_Type() {
        return user_Type;
    }
    public void setUser_Type(String user_Type) {
        this.user_Type = user_Type;
    }
    public String getJoinDate() {
        return join_Date;
    }
    public void setJoinDate(String join_Date) {
        this.join_Date = join_Date;
    }
}
