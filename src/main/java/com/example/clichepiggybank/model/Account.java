package com.example.clichepiggybank.model;

import java.util.UUID;

public class Account {
    private UUID id;
    private UUID ownerId;
    private double balance;

    public Account(UUID id, UUID ownerId, double balance) {
        this.id = id;
        this.ownerId = ownerId;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
