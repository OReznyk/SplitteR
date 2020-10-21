package com.splitter.Model;


import java.util.HashMap;
//ToDo add return money option; notification option & etc
public class Wallet {
    double cash;
    HashMap<String, Double> usersInDebt; // <userId, debt>
    HashMap<String, Double> usersToReturnMoney; // <userId, debt>

    public Wallet() {
        this.usersInDebt = new HashMap<String, Double>();
        this.usersToReturnMoney = new HashMap<String, Double>();
        this.cash = 0;
    }

    public boolean addMoney(double money){
        //ToDo change to moneyMovement with - option. check if this.cash < 0
        if(money>0){
            //ToDo update firebase wallet
            cash += money;
            return true;
        }
        return false;
    }
}
