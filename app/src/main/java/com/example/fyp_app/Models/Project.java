package com.example.fyp_app.Models;

public class Project {

    public String project_name, project_description, date_created;
    public String project_id;

    public Project(){};

    public Project(String project_name, String project_description, String date_created) {
        this.project_name = project_name;
        this.project_description = project_description;
        this.date_created = date_created;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_description() {
        return project_description;
    }

    public void setProject_description(String project_description) {
        this.project_description = project_description;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
