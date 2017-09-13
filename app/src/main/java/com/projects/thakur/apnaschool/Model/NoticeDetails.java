package com.projects.thakur.apnaschool.Model;


public class NoticeDetails {

    private String notice_ID = "";
    private String notice_titles = "";
    private String notice_date = "";
    private String notice_details = "";
    private String notice_department = "";
    private String notice_approved_by = "";
    private String notice_announced_by = "";
    private String attachmentsFilesName = "";
    private String notice_user_type = "";
    private String notice_user_state = "";
    private String notice_user_district = "";

    private String notice_firbase_ID = "";
    private String user_firebase_ID = "";

    public NoticeDetails() {
    }

    public String getUser_firebase_ID() {
        return user_firebase_ID;
    }

    public void setUser_firebase_ID(String user_firebase_ID) {
        this.user_firebase_ID = user_firebase_ID;
    }


    public String getNotice_ID() {
        return notice_ID;
    }

    public void setNotice_ID(String notice_ID) {
        this.notice_ID = notice_ID;
    }

    public String getNotice_user_type() {
        return notice_user_type;
    }

    public void setNotice_user_type(String notice_user_type) {
        this.notice_user_type = notice_user_type;
    }

    public String getNotice_user_state() {
        return notice_user_state;
    }

    public void setNotice_user_state(String notice_user_state) {
        this.notice_user_state = notice_user_state;
    }

    public String getNotice_user_district() {
        return notice_user_district;
    }

    public void setNotice_user_district(String notice_user_district) {
        this.notice_user_district = notice_user_district;
    }

    public String getAttachmentsFilesName() {
        return attachmentsFilesName;
    }

    public void setAttachmentsFilesName(String attachmentsFilesName) {
        this.attachmentsFilesName = attachmentsFilesName;
    }

    public String getNotice_approved_by() {
        return notice_approved_by;
    }

    public void setNotice_approved_by(String notice_approved_by) {
        this.notice_approved_by = notice_approved_by;
    }

    public String getNotice_announced_by() {
        return notice_announced_by;
    }

    public void setNotice_announced_by(String notice_announced_by) {
        this.notice_announced_by = notice_announced_by;
    }



    public String getNotice_department() {
        return notice_department;
    }

    public void setNotice_department(String notice_department) {
        this.notice_department = notice_department;
    }



    public String getNotice_titles() {
        return notice_titles;
    }

    public void setNotice_titles(String notice_titles) {
        this.notice_titles = notice_titles;
    }

    public String getNotice_date() {
        return notice_date;
    }

    public void setNotice_date(String notice_date) {
        this.notice_date = notice_date;
    }

    public String getNotice_details() {
        return notice_details;
    }

    public void setNotice_details(String notice_details) {
        this.notice_details = notice_details;
    }

    public String getNotice_firbase_ID() {
        return notice_firbase_ID;
    }

    public void setNotice_firbase_ID(String notice_firbase_ID) {
        this.notice_firbase_ID = notice_firbase_ID;
    }


}
