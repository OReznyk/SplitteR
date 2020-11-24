package com.splitter.Model;
import java.util.List;

public class Chat {

    String id, image, title, description;
    List<String> adminsIDs;
    List<String> participantsIDs;
    List<Message> msgList;

    public Chat() {
    }

    public Chat(String id, String image, String title, String description, List<String> adminsIDs, List<String> participantsIDs, List<Message> msgList) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.adminsIDs = adminsIDs;
        this.participantsIDs = participantsIDs;
        this.msgList = msgList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAdminsIDs() {
        return adminsIDs;
    }

    public void setAdminsIDs(List<String> adminsIDs) {
        this.adminsIDs = adminsIDs;
    }

    public List<String> getParticipantsIDs() {
        return participantsIDs;
    }

    public void setParticipantsIDs(List<String> participantsIDs) {
        this.participantsIDs = participantsIDs;
    }

    public List<Message> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<Message> msgList) {
        this.msgList = msgList;
    }

    public List<String> participantsIDs() {
        return null;
    }
}
