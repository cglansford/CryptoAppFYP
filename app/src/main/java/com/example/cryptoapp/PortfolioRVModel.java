package com.example.cryptoapp;

import java.io.Serializable;

public class PortfolioRVModel implements Serializable {
    private String name;
    private double holdingAmount;
    private double dollarTotal;


    public PortfolioRVModel(String name, double holdingAmount){
        this.name = name;
        this.holdingAmount = holdingAmount;
        this.dollarTotal = 0;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHoldingAmount() {
        return holdingAmount;
    }

    public void setHoldingAmount(double holdingAmount) {
        this.holdingAmount = holdingAmount;
    }

    public double getDollarTotal() {
        return dollarTotal;
    }

    public void setDollarTotal(double dollarTotal) {
        this.dollarTotal = dollarTotal;
    }
}
