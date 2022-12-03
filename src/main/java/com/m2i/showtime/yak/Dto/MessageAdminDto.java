package com.m2i.showtime.yak.Dto;

public class MessageAdminDto {
    private String message;
    private String severity;
    private String type;

    public MessageAdminDto(String message, String severity, String type) {
        this.message = message;
        this.severity = severity;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
