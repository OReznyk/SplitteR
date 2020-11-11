package com.splitter.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatModel{
    String id, msg, receiver, sender, timeStamp, typingTo;
    boolean isSeen;
    DatabaseReference dbRef;
    FirebaseDatabase fDb;

    public ChatModel() {
        dbRef = fDb.getReference("Chats");
        DatabaseReference newChatID = dbRef.push();
        this.id = newChatID.toString();
        this.msg = "";
        this.receiver = "";
        this.sender = "";
        this.timeStamp = "";
        this.typingTo = "none";
        this.isSeen = false;
    }

    public ChatModel(String id, String msg, String receiver, String sender, String timeStamp, String typingTo, boolean isSeen) {
        this.id = id;
        this.msg = msg;
        this.receiver = receiver;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.typingTo = typingTo;
        this.isSeen = isSeen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
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

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}