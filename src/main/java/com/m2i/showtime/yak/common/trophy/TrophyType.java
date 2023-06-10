package com.m2i.showtime.yak.common.trophy;

public enum TrophyType {
    BRONZE("bronze"),
    SILVER("silver"),
    GOLD("gold"),
    PLATINUM("platinum")
    ;
    private final String type;

    TrophyType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
