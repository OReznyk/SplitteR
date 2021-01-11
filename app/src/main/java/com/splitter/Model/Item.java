package com.splitter.Model;

public class Item {
    private String id, img;
    private String name, type, creator;
    private String price;

    public Item(){}
    public Item(String id, String img, String name, String type, String creator, String price) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.type = type;
        this.creator = creator;
        this.price = price;
    }
    public Item(Item other, String creatorID){
        this.id = other.getId();
        this.img = other.getImg();
        this.name = other.getName();
        this.type = other.getType();
        this.creator = creatorID;
        this.price = other.getPrice();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
