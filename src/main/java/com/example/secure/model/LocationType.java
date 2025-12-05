package com.example.secure.model;

public enum LocationType {
    MOUNTAINS("Гори (Гірська місцевість)"),
    SEASIDE("Море / Водойом (Пляжна зона)"),
    NEUTRAL("Рівнина / Місто (Немає гір і моря)");

    private final String title;

    LocationType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}