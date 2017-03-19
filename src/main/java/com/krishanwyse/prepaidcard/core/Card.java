package com.krishanwyse.prepaidcard.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class Card {
    @NotNull
    @JsonProperty
    private long id;

    @NotNull
    @JsonProperty
    private String name;

    @NotNull
    @JsonProperty
    private double balance;

    @NotNull
    @JsonProperty
    private double blocked;

    public Card() {
    }

    public Card(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public Card(long id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Card(long id, String name, double balance, double blocked) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.blocked = blocked;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public double getBlocked() {
        return blocked;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setBlocked(double blocked) {
        this.blocked = blocked;
    }
}
