package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileLazyUserDtoAvatar {
    private String profilePicture="";
    private String backgroundPicture="";
    private String fullName="";
    private String firstName = "";
    private String lastName = "";
    private boolean notification_system_status;
}
