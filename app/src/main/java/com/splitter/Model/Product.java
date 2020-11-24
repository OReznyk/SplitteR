package com.splitter.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Product {
    private String id, img;
    private String name, buyer;
    private double price;


    public Product() {
    }

    public Product(String name,double price) {
        this.img = "";
        this.name = name;
        this.price = price;
        this.buyer = "";

    }

    public Product(String id, String img, String name, String buyer, double price) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.buyer = buyer;
        this.price = price;
    }

    public void delete(){
        DatabaseReference dbRefToProduct = FirebaseDatabase.getInstance().getReference("Products").child(id);
       dbRefToProduct.removeValue();
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
