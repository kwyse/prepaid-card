package com.krishanwyse.prepaidcard.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class BlockedCard {
    @NotNull
    @JsonProperty
    private long id;

    @NotNull
    @JsonProperty
    private double balance;

    @NotNull
    @JsonProperty
    private double blocked;

    @NotNull
    @JsonProperty
    private String name;

    public BlockedCard() {
    }

    public BlockedCard(long id, String name, double balance, double blocked) {
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
}
