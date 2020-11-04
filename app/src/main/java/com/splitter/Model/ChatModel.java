package com.splitter.Model;

public class ChatModel{
    String msg, receiver, sender, timeStamp;
    boolean isSeen;

    public ChatModel() {
    }

    public ChatModel(String msg, String receiver, String sender, String timeStamp, boolean isSeen) {
        this.msg = msg;
        this.receiver = receiver;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.isSeen = isSeen;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean seen) {
        isSeen = seen;
    }
}