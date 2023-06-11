package com.m2i.showtime.yak.common.trophy;

public enum TrophyActionName {
    ADD_MOVIE_IN_WATCHED_LIST("add_movie_in_watched_list"),
    REMOVE_MOVIE_IN_WATCHED_LIST("remove_movie_in_watched_list"),
    ADD_SERIE_IN_WATCHED_LIST("add_serie_in_watched_list"),
    REMOVE_SERIE_IN_WATCHED_LIST("remove_serie_in_watched_list");
    private final String type;

    TrophyActionName(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

}
