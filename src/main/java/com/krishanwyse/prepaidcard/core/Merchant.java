package com.krishanwyse.prepaidcard.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class Merchant {
    @NotNull
    @JsonProperty
    private long id;

    @NotNull
    @JsonProperty
    private String name;

    @NotNull
    @JsonProperty
    private double balance;

    public Merchant() {
    }

    public Merchant(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public Merchant(long id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
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

    public void setId(long id) {
        this.id = id;
    }
}
