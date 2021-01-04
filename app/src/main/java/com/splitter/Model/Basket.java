package com.splitter.Model;

import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class Basket {
    String basketID, groupID, title, expiringDate, expiringTime;
    Double totalPrice;
    List<String> adminsID;
    HashMap<Product, Integer> listToBye;
    FirebaseAuth fAuth;
    FirebaseUser fUser;

    public Basket(String basketID, String title, List<String> adminsID, HashMap<Product, Integer> listToBye) {
        this.basketID = basketID;
        this.title = title;
        this.expiringDate = "";
        this.expiringTime = "";
        this.adminsID = adminsID;
        this.listToBye = listToBye;
        this.totalPrice = 0.0;
    }

    public Basket(String basketID, String title, String expiringDate, String expiringTime, List<String> adminsID, HashMap<Product, Integer> listToBye) {
        this.basketID = basketID;
        this.title = title;
        this.expiringDate = expiringDate;
        this.expiringTime = expiringTime;
        this.adminsID = adminsID;
        this.listToBye = listToBye;
        this.totalPrice = 0.0;
    }

    public void delete(){
        DatabaseReference dbRefToBasket = FirebaseDatabase.getInstance().getReference("Baskets").child(basketID);
        dbRefToBasket.removeValue();
    }

    public void deleteProduct(String productID){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        if(adminsID.contains(fUser.getUid()) && listToBye.containsKey(productID)){
            DatabaseReference dbRefToGroup = FirebaseDatabase.getInstance().getReference("Baskets").child(basketID);
            listToBye.remove(productID);
            HashMap<String, Object> hashMap= new HashMap<>();
            hashMap.put("listToBye", listToBye);
            dbRefToGroup.setValue(hashMap);
        }
    }

    public void addProduct(Product product, int quantity){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        if(adminsID.contains(fUser.getUid())){
            DatabaseReference dbRefToGroup = FirebaseDatabase.getInstance().getReference("Baskets").child(basketID);
            listToBye.put(product, quantity);
            HashMap<String, Object> hashMap= new HashMap<>();
            hashMap.put("listToBye", listToBye);
            dbRefToGroup.setValue(hashMap);
        }
    }

    private void getTotPrice(){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Products");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalPrice = 0.0;
                //TODO: change to minimalize runtime!
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Product product = snapshot.getValue(Product.class);
                    if(listToBye.containsKey(product.getId())){
                        //ToDo: totalPrice += product.getPrice() * listToBye.get(product.getId());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public double getTotalPrice(){
        getTotPrice();
        return totalPrice;
    }
    public void changeProductQuantity(Product product, int quantity){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        if(adminsID.contains(fUser.getUid())){
            DatabaseReference dbRefToGroup = FirebaseDatabase.getInstance().getReference("Baskets").child(basketID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                listToBye.replace(product, quantity);
            }
            HashMap<String, Object> hashMap= new HashMap<>();
            hashMap.put("listToBye", listToBye);
            dbRefToGroup.setValue(hashMap);
        }
    }

    public List<String> getAdminsID() {
        return adminsID;
    }

    public void setAdminsID(List<String> adminsID) {
        this.adminsID = adminsID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
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
