package com.splitter.Model;

public class User {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String image;
    private  String cover;



    public User(){}

    public User(String id, String name, String phone, String email, String image, String
            cover){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.image = image;
        this.cover = cover;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) { this.image = cover; }

    public String getId() {
        return id;
    }

}
