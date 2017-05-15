package com.projects.thakur.apnaschool.Model;


public class DailyMDMStatus {

    private String mdmStudentsDetails = "";
    private String mdmRiceStockDetails = "";
    private String mdmtodayMenu = "";
    private String school_details = "";


    public DailyMDMStatus() {
    }


    public String getSchool_details() {
        return school_details;
    }

    public void setSchool_details(String school_details) {
        this.school_details = school_details;
    }

    public String getMdmStudentsDetails() {
        return mdmStudentsDetails;
    }

    public void setMdmStudentsDetails(String mdmStudentsDetails) {
        this.mdmStudentsDetails = mdmStudentsDetails;
    }

    public String getMdmRiceStockDetails() {
        return mdmRiceStockDetails;
    }

    public void setMdmRiceStockDetails(String mdmRiceStockDetails) {
        this.mdmRiceStockDetails = mdmRiceStockDetails;
    }

    public String getMdmtodayMenu() {
        return mdmtodayMenu;
    }

    public void setMdmtodayMenu(String mdmtodayMenu) {
        this.mdmtodayMenu = mdmtodayMenu;
    }


}
