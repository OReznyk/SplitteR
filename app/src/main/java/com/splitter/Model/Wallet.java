package com.splitter.Model;


import java.util.HashMap;
//ToDo add return money option; notification option & etc
public class Wallet {
    double cash;
    HashMap<String, Double> usersInDebt; // <userId, debtSum>
    HashMap<String, Double> usersToReturnMoney; // <userId, debt>

    public Wallet() {
        this.usersInDebt = new HashMap<String, Double>();
        this.usersToReturnMoney = new HashMap<String, Double>();
        this.cash = 0;
    }

    public Wallet(double cash, HashMap<String, Double> usersInDebt, HashMap<String, Double> usersToReturnMoney) {
        this.cash = cash;
        this.usersInDebt = usersInDebt;
        this.usersToReturnMoney = usersToReturnMoney;
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

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public HashMap<String, Double> getUsersInDebt() {
        return usersInDebt;
    }

    public void setUsersInDebt(HashMap<String, Double> usersInDebt) {
        this.usersInDebt = usersInDebt;
    }

    public HashMap<String, Double> getUsersToReturnMoney() {
        return usersToReturnMoney;
    }

    public void setUsersToReturnMoney(HashMap<String, Double> usersToReturnMoney) {
        this.usersToReturnMoney = usersToReturnMoney;
    }
}
