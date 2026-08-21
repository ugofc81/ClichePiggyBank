package com.example.clichepiggybank.model;

public class Account {
    private String id;
    private String ownerId;
    private double balance;

    public Account(String id, String ownerId, double balance) {
        this.id = id;
        this.ownerId = ownerId;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
