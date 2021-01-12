package com.splitter.Model;

public class MoneyFlow {
    String spent, paid, debt;

    public MoneyFlow() {
    }

    public MoneyFlow(String spent, String paid, String debt) {
        this.spent = spent;
        this.paid = paid;
        this.debt = debt;
    }

    public String getSpent() {
        return spent;
    }

    public void setSpent(String spent) {
        this.spent = spent;
    }

    public String getDebt() {
        return debt;
    }

    public void setDebt(String debt) {
        this.debt = debt;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }
}
