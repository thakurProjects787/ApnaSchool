package com.projects.thakur.apnaschool.Task.VirtualTask;



public class VirtualResultTaskModel {

    public String name;
    public String url;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public VirtualResultTaskModel() {
    }

    public VirtualResultTaskModel(String name, String url) {
        this.name = name;
        this.url= url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
