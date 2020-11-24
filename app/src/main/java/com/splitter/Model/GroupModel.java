package com.splitter.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class GroupModel {
    String groupID, chatID, image, title, description;
    List<String> adminsIDs;
    List<String> participants;
    DatabaseReference dbRef;
    FirebaseDatabase fDb;
    FirebaseAuth fAuth;
    FirebaseUser fUser;

    public GroupModel() {
    }

    public GroupModel(String groupID, String chatID, String image, String title, String description, List<String> adminsIDs, List<String> participants) {
        this.groupID = groupID;
        this.chatID = chatID;
        this.image = image;
        this.title = title;
        this.description = description;
        this.adminsIDs = adminsIDs;
        this.participants = participants;
    }

    public void delete(){
        DatabaseReference dbRefToGroup = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);
        DatabaseReference dbRefToGroupChat = FirebaseDatabase.getInstance().getReference("Chats").child(chatID);
        dbRefToGroupChat.removeValue();
        dbRefToGroup.removeValue();
    }

    public void deleteParticipant(String uid){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        if(adminsIDs.contains(fUser.getUid()) && participants.contains(uid)){
            DatabaseReference dbRefToGroup = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);
            participants.remove(uid);
            HashMap<String, Object> hashMap= new HashMap<>();
            hashMap.put("participants", participants);
            dbRefToGroup.setValue(hashMap);
        }
    }

    public List<String> getAdminsIDs() {
        return adminsIDs;
    }

    public void setAdminsIDs(List<String> adminsIDs) {
        this.adminsIDs = adminsIDs;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
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

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

}
