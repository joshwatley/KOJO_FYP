package com.example.fyp_app.Models;

public class TaskGroups {
    private String groupid, taskid, userid;

    public TaskGroups(){}

    public TaskGroups(String groupid, String taskid) {
        this.groupid = groupid;
        this.taskid = taskid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }
}
