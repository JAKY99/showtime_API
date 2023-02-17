package com.m2i.showtime.yak.Enum;

public enum Status {
    NOTSEEN("Not Seen"),
    WATCHING("Watching"),
    SEEN("Seen");

    private final String status;

    Status(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
}
