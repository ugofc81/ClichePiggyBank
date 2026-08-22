package com.example.clichepiggybank.model;

import java.util.Date;
import java.util.Set;

public class Sanction {
    private String id;
    private User reporter;
    private User receiver;
    private SanctionAmount amount;
    private String reason;
    private Date datetime;
    private Set<String> likedBy;
    private Integer likes;

    public Sanction(String id, User reporter, User receiver, SanctionAmount amount, String reason, Date datetime, Set<String> likedBy, Integer likes) {
        this.id = id;
        this.reporter = reporter;
        this.receiver = receiver;
        this.amount = amount;
        this.reason = reason;
        this.datetime = datetime;
        this.likedBy = likedBy;
        this.likes = likes;
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

    public SanctionAmount getAmount() {
        return amount;
    }

    public void setAmount(SanctionAmount amount) {
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

    public Set<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Set<String> likedBy) {
        this.likedBy = likedBy;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}
