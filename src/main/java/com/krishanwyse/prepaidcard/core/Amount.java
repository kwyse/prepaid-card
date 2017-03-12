package com.krishanwyse.prepaidcard.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class Amount {
    @NotNull
    @JsonProperty
    private double amount;

    public Amount() {
    }

    public Amount(double amount) {
        this.amount = amount;
    }

    public double get() {
        return amount;
    }
}
