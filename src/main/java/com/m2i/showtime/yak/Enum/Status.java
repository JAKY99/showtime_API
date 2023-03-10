package com.m2i.showtime.yak.Enum;

public enum Status {
    NOTSEEN("Not Seen"),
    WATCHING("Watching"),
    SEEN("Seen");

    private final String value;

    private Status(String value) {
        this.value = value;
    }

    public  String getValue() {
        return this.value;
    }
}
