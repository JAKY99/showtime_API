package com.m2i.showtime.yak.Dto;

import com.m2i.showtime.yak.common.notification.NotificationStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class NotificationAgGridDto {
    private Long id;
    private String message;
    private String severity;
    private String type;

    private NotificationStatus status = NotificationStatus.UNREAD;
    private Date dateCreated = new Date();
    private Date dateRead;

    private String receiverName;
}
