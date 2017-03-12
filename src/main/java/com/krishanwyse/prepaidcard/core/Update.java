package com.krishanwyse.prepaidcard.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class Update {
    @NotNull
    @JsonProperty
    private double amount;
    private UpdateType type;

    public Update() {
    }

    public Update(double amount, UpdateType type) {
        this.amount = amount;
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public UpdateType getType() {
        return type;
    }
}
