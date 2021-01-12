package com.splitter.Model;

import java.util.HashMap;
import java.util.List;

public class Wallet {
    String id;
    List<String> admins;
    HashMap<String, MoneyFlow>usersMoneyFlow;
    String totalCosts;

    public Wallet() {
    }

    public Wallet(String id, List<String> admins, HashMap<String, MoneyFlow> usersMoneyFlow, String totalCosts) {
        this.id = id;
        this.admins = admins;
        this.usersMoneyFlow = usersMoneyFlow;
        this.totalCosts = totalCosts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public HashMap<String, MoneyFlow> getUsersMoneyFlow() {
        return usersMoneyFlow;
    }

    public void setUsersMoneyFlow(HashMap<String, MoneyFlow> usersMoneyFlow) {
        this.usersMoneyFlow = usersMoneyFlow;
    }


    public String getTotalCosts() {
        return totalCosts;
    }

    public void setTotalCosts(String totalCosts) {
        this.totalCosts = totalCosts;
    }
}
