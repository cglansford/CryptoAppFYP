package com.example.cryptoapp.models;

public class CurrencyRVModel {
    private String name;
    private String ticker;
    private double price;

    public CurrencyRVModel(String name, String ticker, double price) {
        this.name = name;
        this.ticker = ticker;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
