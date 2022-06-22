package com.example.fyp_app.Models;

public class Task {

    public String task_title, task_content, date_created, priority, header, date_due, task_id, userid;
    public Task(){};

    public Task(String task_title, String task_content, String date_created, String priority, String header, String date_due, String task_id, String userid) {
        this.task_title = task_title;
        this.task_content = task_content;
        this.date_created = date_created;
        this.priority = priority;
        this.header = header;
        this.date_due = date_due;
        this.task_id = task_id;
        this.userid = userid;
    }



    public String getDate_due() {
        return date_due;
    }

    public void setDate_due(String date_due) {
        this.date_due = date_due;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getTask_content() {
        return task_content;
    }

    public void setTask_content(String task_content) {
        this.task_content = task_content;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
