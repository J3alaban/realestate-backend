package com.realestate.backend.entities;

public enum SubscriptionPlan {

    FREE(3),
    BASIC(10),
    PRO(20),
    PREMIUM(Integer.MAX_VALUE);

    private final int maxListings;

    SubscriptionPlan(int maxListings) {
        this.maxListings = maxListings;
    }

    public int getMaxListings() {
        return maxListings;
    }
}