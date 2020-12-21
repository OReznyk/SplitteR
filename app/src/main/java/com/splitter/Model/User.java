package com.splitter.Model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String avatar;
    private  String cover;
    private String typingTo;
    private String onlineStatus;
    List<String> chatsIDs;
    List<String>basketsIDs;
    List<String>friendsIDs;

    public User(){}

    public User(String id, String name, String phone, String email, String avatar, String cover, String typingTo, String onlineStatus) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
        this.cover = cover;
        this.typingTo = typingTo;
        this.onlineStatus = onlineStatus;
        chatsIDs = new ArrayList<>();
        basketsIDs = new ArrayList<>();
        friendsIDs = new ArrayList<>();
    }

    public User(String id, String name, String phone, String email, String avatar, String cover, String typingTo, String onlineStatus, List<String> chatsIDs, List<String> basketsIDs, List<String> friendsIDs) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
        this.cover = cover;
        this.typingTo = typingTo;
        this.onlineStatus = onlineStatus;
        this.chatsIDs = chatsIDs;
        this.basketsIDs = basketsIDs;
        this.friendsIDs = friendsIDs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public List<String> getChatsIDs() {
        return chatsIDs;
    }

    public void setChatsIDs(List<String> chatsIDs) {
        this.chatsIDs = chatsIDs;
    }

    public List<String> getBasketsIDs() {
        return basketsIDs;
    }

    public void setBasketsIDs(List<String> basketsIDs) {
        this.basketsIDs = basketsIDs;
    }

    public List<String> getFriendsIDs() {
        return friendsIDs;
    }

    public void setFriendsIDs(List<String> friendsIDs) {
        this.friendsIDs = friendsIDs;
    }
}
