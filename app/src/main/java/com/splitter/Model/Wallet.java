package com.splitter.Model;

import java.util.HashMap;

public class Wallet {
    HashMap<String, MoneyFlow>usersMoneyFlow;
    String totalCosts;

    public Wallet() {
    }

    public Wallet(HashMap<String, MoneyFlow> usersMoneyFlow, String totalCosts) {
        this.usersMoneyFlow = usersMoneyFlow;
        this.totalCosts = totalCosts;
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
