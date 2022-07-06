package com.example.fyp_app.Models;

public class File {

    public String file_title, file_link, date_created, file_id, group_id, userid;
    public File(){};

    public File(String file_title, String file_link, String date_created, String file_id, String group_id, String userid) {
        this.file_title = file_title;
        this.file_link = file_link;
        this.date_created = date_created;
        this.file_id = file_id;
        this.group_id = group_id;
        this.userid = userid;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getFile_title() {
        return file_title;
    }

    public void setFile_title(String file_title) {
        this.file_title = file_title;
    }

    public String getFile_link() {
        return file_link;
    }

    public void setFile_link(String file_link) {
        this.file_link = file_link;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
