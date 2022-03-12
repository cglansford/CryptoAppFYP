package com.example.cryptoapp;

import java.io.Serializable;

public class PortfolioRVModel implements Serializable {
    private String name;
    private double holdingAmount;


    public PortfolioRVModel(String name, double holdingAmount){
        this.name = name;
        this.holdingAmount = holdingAmount;

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


}
