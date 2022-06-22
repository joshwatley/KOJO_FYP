package com.example.fyp_app.Models;

public class Groups {

    public String name, desc, admin_ID, date_created, headings, groupid;

    public Groups(){};

    public Groups(String group_name, String group_desc, String group_admin_ID, String date_created, String headings, String groupid) {
        this.name = group_name;
        this.desc = group_desc;
        this.admin_ID = group_admin_ID;
        this.date_created = date_created;
        this.headings = headings;
        this.groupid = groupid;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAdmin_ID() {
        return admin_ID;
    }

    public void setAdmin_ID(String admin_ID) {
        this.admin_ID = admin_ID;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getHeadings() {
        return headings;
    }

    public void setHeadings(String headings) {
        this.headings = headings;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }
}
