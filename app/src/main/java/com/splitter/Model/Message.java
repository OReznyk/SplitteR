package com.splitter.Model;

public class Message {
    String id, msg, receiver, sender, timeStamp;
    boolean seen;

    public Message() {
    }

    public Message(String id, String msg, String receiver, String sender, String timeStamp, boolean seen) {
        this.id = id;
        this.msg = msg;
        this.receiver = receiver;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.seen = seen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}