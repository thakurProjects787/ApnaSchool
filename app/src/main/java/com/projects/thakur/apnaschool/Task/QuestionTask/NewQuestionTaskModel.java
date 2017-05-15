package com.projects.thakur.apnaschool.Task.QuestionTask;



public class NewQuestionTaskModel {

    private String task_heading = "";
    private String task_last_date = "";
    private String task_details = "";

    private String task_stage = "";

    private String task_firbase_ID = "";


    public NewQuestionTaskModel() {
    }


    public String getTask_stage() {
        return task_stage;
    }

    public void setTask_stage(String task_stage) {
        this.task_stage = task_stage;
    }

    public String getTask_heading() {
        return task_heading;
    }

    public void setTask_heading(String task_heading) {
        this.task_heading = task_heading;
    }

    public String getTask_last_date() {
        return task_last_date;
    }

    public void setTask_last_date(String task_last_date) {
        this.task_last_date = task_last_date;
    }

    public String getTask_details() {
        return task_details;
    }

    public void setTask_details(String task_details) {
        this.task_details = task_details;
    }

    public String getTask_firbase_ID() {
        return task_firbase_ID;
    }

    public void setTask_firbase_ID(String task_firbase_ID) {
        this.task_firbase_ID = task_firbase_ID;
    }


}
