package com.splitter.Model;

import java.util.HashMap;

public class BasketItem {
    String id, quantity;
    HashMap<String, String> buyersAndQuantities;
    Item item;

    public BasketItem() {
    }

    public BasketItem(String id, String quantity, HashMap<String, String> buyersAndQuantities, Item item) {
        this.id = id;
        this.quantity = quantity;
        this.buyersAndQuantities = buyersAndQuantities;
        this.item = item;
    }
    public BasketItem(Item item) {
        this.id = "";
        this.quantity = "";
        buyersAndQuantities = new HashMap<>();
        this.item = item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public HashMap<String, String> getBuyersAndQuantities() {
        return buyersAndQuantities;
    }

    public void setBuyersAndQuantities(HashMap<String, String> buyersAndQuantities) {
        this.buyersAndQuantities = buyersAndQuantities;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
