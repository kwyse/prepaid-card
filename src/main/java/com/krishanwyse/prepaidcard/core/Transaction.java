package com.krishanwyse.prepaidcard.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class Transaction {
    @NotNull
    @JsonProperty
    private long id;

    @NotNull
    @JsonProperty
    private long card;

    @NotNull
    @JsonProperty
    private long merchant;

    @NotNull
    @JsonProperty
    private double remaining;

    @NotNull
    @JsonProperty
    private double captured;

    public Transaction() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public Transaction(long merchant, double remaining, double captured) {
        this.merchant = merchant;
        this.remaining = remaining;
        this.captured = captured;
    }

    public Transaction(long id, long card, long merchant, double remaining, double captured) {
        this.id = id;
        this.card = card;
        this.merchant = merchant;
        this.remaining = remaining;
        this.captured = captured;
    }

    public long getId() {
        return id;
    }

    public long getCard() {
        return card;
    }

    public long getMerchant() {
        return merchant;
    }

    public double getRemaining() {
        return remaining;
    }

    public double getCaptured() {
        return captured;
    }

    public void setCard(long card) {
        this.card = card;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public void setCaptured(double captured) {
        this.captured = captured;
    }
}
