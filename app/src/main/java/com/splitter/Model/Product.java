package com.splitter.Model;

import android.net.Uri;

public class Product {
    private Uri img;
    private String name;
    private double quantity, priceTotal;


    public Product() {
    }
    public Product(String name, double quantity, double priceTotal) {
        this.img = null;
        this.name = name;
        this.quantity = quantity;
        this.priceTotal = priceTotal;
    }

    public Product(Uri img, String name, double quantity, double priceTotal) {
        this.img = img;
        this.name = name;
        this.quantity = quantity;
        this.priceTotal = priceTotal;
    }

    public double averagePrice(){
        double averagePrice = priceTotal / quantity;
        return averagePrice;
    }

    public Uri getImg() {
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(double priceTotal) {
        this.priceTotal = priceTotal;
    }
}
