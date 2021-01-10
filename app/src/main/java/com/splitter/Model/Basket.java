package com.splitter.Model;

import java.util.HashMap;
import java.util.List;

public class Basket {
    String basketID, creatorID, title, expiringDate, expiringTime, totalPrice;
    List<String> adminsID;
    HashMap<Product, Integer> listToBye;

    public Basket() {
    }

    public Basket(String basketID, String creatorID, String title, List<String> adminsID, HashMap<Product, Integer> listToBye) {
        this.basketID = basketID;
        this.creatorID = creatorID;
        this.title = title;
        this.expiringDate = "";
        this.expiringTime = "";
        this.adminsID = adminsID;
        this.listToBye = listToBye;
        this.totalPrice = "0";
    }

    public Basket(String basketID, String creatorID, String title, String expiringDate, String expiringTime, String totalPrice, List<String> adminsID, HashMap<Product, Integer> listToBye) {
        this.basketID = basketID;
        this.creatorID = creatorID;
        this.title = title;
        this.expiringDate = expiringDate;
        this.expiringTime = expiringTime;
        this.totalPrice = totalPrice;
        this.adminsID = adminsID;
        this.listToBye = listToBye;
    }

    public String getTotalPrice(){
        return totalPrice;
    }

    public List<String> getAdminsID() {
        return adminsID;
    }

    public void setAdminsID(List<String> adminsID) {
        this.adminsID = adminsID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getBasketID() {
        return basketID;
    }

    public void setBasketID(String basketID) {
        this.basketID = basketID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExpiringDate() {
        return expiringDate;
    }

    public void setExpiringDate(String expiringDate) {
        this.expiringDate = expiringDate;
    }

    public String getExpiringTime() {
        return expiringTime;
    }

    public void setExpiringTime(String expiringTime) {
        this.expiringTime = expiringTime;
    }

    public HashMap<Product, Integer> getListToBye() {
        return listToBye;
    }

    public void setListToBye(HashMap<Product, Integer> listToBye) {
        this.listToBye = listToBye;
    }
}
