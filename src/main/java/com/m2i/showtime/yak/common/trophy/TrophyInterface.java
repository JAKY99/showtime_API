package com.m2i.showtime.yak.common.trophy;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public interface TrophyInterface {
    public void createTrophyIfNotExist();

    public void updateTrophyIfNecessary();
    public void checkTrophy(String username, long elementId, TrophyActionName trophyActionName);
    public void NotifyUser(String username, long elementId);
    public String getName();
    public String getDescription();
    public String getImage();
    @Enumerated(EnumType.STRING)
    public TrophyType getTrophyType();



}
