package com.example.clichepiggybank.model;

import java.util.Date;

public class Sanction {
    private String id;
    private User reporter;
    private User receiver;
    private double amount;
    private String reason;
    private Date datetime;

    public Sanction(String id, User reporter, User receiver, double amount, String reason, Date datetime) {
        this.id = id;
        this.reporter = reporter;
        this.receiver = receiver;
        this.amount = amount;
        this.reason = reason;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}
