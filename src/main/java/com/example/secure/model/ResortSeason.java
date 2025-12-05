package com.example.secure.model;

public enum ResortSeason {
    SUMMER("Літній"),
    WINTER("Зимовий"),
    ALL_SEASON("Універсальний");

    private final String displayName;

    ResortSeason(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
