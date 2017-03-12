package com.krishanwyse.prepaidcard.core;

import java.sql.Timestamp;

public class StatementEntry {
    private String merchant;
    private double amount;
    private Timestamp timestamp;

    public StatementEntry() {
    }

    public StatementEntry(String merchant, double amount, Timestamp timestamp) {
        this.merchant = merchant;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getMerchant() {
        return merchant;
    }

    public double getAmount() {
        return amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
