package com.splitter.Model;

import android.net.Uri;

public class User {
    private String id;
    private String name;
    private String phone;
    private String email;
    private Uri avatarURI;
    private  Uri coverURI;

    public User(){}

    public User(String id, String name, String phone, String email, Uri avatarURI, Uri coverURI){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.avatarURI = avatarURI;
        this.coverURI = coverURI;
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

    public Uri getAvatarURI() {
        return avatarURI;
    }

    public void setAvatarURI(Uri avatarURI) {
        this.avatarURI = avatarURI;
    }

    public Uri getCoverURI() {
        return coverURI;
    }

    public void setCoverURI(Uri coverURI) { this.avatarURI = coverURI; }

    public String getId() {
        return id;
    }

}
