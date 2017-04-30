package com.projects.thakur.apnaschool.Model;


public class TeachersDetails {

    private String id = "";
    private String name = "";
    private String join_date = "";
    private String designation = "";
    private String education_details = "";
    private String special_areas = "";

    public TeachersDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJoin_date() {
        return join_date;
    }

    public void setJoin_date(String join_date) {
        this.join_date = join_date;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEducation_details() {
        return education_details;
    }

    public void setEducation_details(String education_details) {
        this.education_details = education_details;
    }

    public String getSpecial_areas() {
        return special_areas;
    }

    public void setSpecial_areas(String special_areas) {
        this.special_areas = special_areas;
    }


}
