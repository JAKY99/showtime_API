package com.m2i.showtime.yak.common.notification;

public enum NotificationStatus {
    UNREAD("unread"),
    READ("read");

    private final String status;

    NotificationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
