package com.example.fyp_app.Models;

public class GroupChats {

    private String sender;
    private String sendername;
    private String group;
    private String message;

    public GroupChats(){}

    public GroupChats(String sender, String sendername, String group, String message) {
        this.sender = sender;
        this.sendername = sendername;
        this.group = group;
        this.message = message;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
