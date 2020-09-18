package com.example.chitchat.models;

import java.util.Date;

public class ChatModel {
    private String messageText;
    private String messageUser;
    private String userName;
    private long messageTime;
    public ChatModel() {
    }

    public ChatModel(String messageText, String messageUser,String userName) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.userName = userName;
        messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
