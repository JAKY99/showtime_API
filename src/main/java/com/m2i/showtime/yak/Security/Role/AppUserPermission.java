package com.m2i.showtime.yak.Security.Role;

public enum AppUserPermission {
    //---USER----------------------
    USER_READ("user:read"), //Can read users accounts
    USER_DELETE("user:delete"), //Can delete his account
    USER_EDIT("user:edit"), //Can edit his account

        USER_MANAGE_USERS("user:manage_users"), //Can edit others users
        USER_MANAGE_RANK("user:manage_rank"), //Can manage others ranks
        USER_MANAGE_TROPHY("user:manage_trophy"), //Can manage others trophies
        USER_MANAGE_PERMISSION("user:manage_permission"), //Can manage others permissions
        USER_MANAGE_WATCHED("user:manage_watched"), //Can manage others watched contents
    //---MOVIE----------------------
    MOVIE_READ("movie:read"), //Can read movies
    MOVIE_DELETE("movie:delete"),  //Can remove watched movies
    MOVIE_CREATE("movie:create"), //Can add movies to watched
        MOVIE_MANAGE("movie:manage");

    private final String permission;

    AppUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
