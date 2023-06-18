package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Repository.TrophyRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.common.trophy.TrophyActionName;
import com.m2i.showtime.yak.common.trophy.list.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TrophyService {

    private final TrophyRepository trophyRepository;
    private final UserRepository userRepository;

    private final MovieWatcherBronzeTrophy movieWatcherBronzeTrophy;
    private final MovieWatcherSilverTrophy movieWatcherSilverTrophy;
    private final MovieWatchergGoldTrophy movieWatchergGoldTrophy;
    private final MovieWatcherPlatineTrophy movieWatcherPlatineTrophy;

    private final TvWatcherBronzeTrophy tvWatcherBronzeTrophy;
    private final TvWatcherSilverTrophy tvWatcherSilverTrophy;
    private final TvWatcherGoldTrophy tvWatcherGoldTrophy;
    private final TvWatcherPlatineTrophy tvWatcherPlatineTrophy;



    public TrophyService(TrophyRepository trophyRepository, UserRepository userRepository, MovieWatcherBronzeTrophy movieWatcherBronzeTrophy, MovieWatcherSilverTrophy movieWatcherSilverTrophy, MovieWatchergGoldTrophy movieWatchergGoldTrophy, MovieWatcherPlatineTrophy movieWatcherPlatineTrophy, TvWatcherBronzeTrophy tvWatcherBronzeTrophy, TvWatcherSilverTrophy tvWatcherSilverTrophy, TvWatcherGoldTrophy tvWatcherGoldTrophy, TvWatcherPlatineTrophy tvWatcherPlatineTrophy) {
        this.trophyRepository = trophyRepository;
        this.userRepository = userRepository;
        this.movieWatcherBronzeTrophy = movieWatcherBronzeTrophy;
        this.movieWatcherSilverTrophy = movieWatcherSilverTrophy;
        this.movieWatchergGoldTrophy = movieWatchergGoldTrophy;
        this.movieWatcherPlatineTrophy = movieWatcherPlatineTrophy;
        this.tvWatcherBronzeTrophy = tvWatcherBronzeTrophy;
        this.tvWatcherSilverTrophy = tvWatcherSilverTrophy;
        this.tvWatcherGoldTrophy = tvWatcherGoldTrophy;
        this.tvWatcherPlatineTrophy = tvWatcherPlatineTrophy;
    }

    @Async
    public void checkAllTrophys(String username, long elementId, TrophyActionName trophyActionName) {
        movieWatcherBronzeTrophy.checkTrophy(username, elementId,trophyActionName);
        movieWatcherSilverTrophy.checkTrophy(username, elementId,trophyActionName);
        movieWatchergGoldTrophy.checkTrophy(username, elementId,trophyActionName);
        movieWatcherPlatineTrophy.checkTrophy(username, elementId,trophyActionName);
        tvWatcherBronzeTrophy.checkTrophy(username, elementId,trophyActionName);
        tvWatcherSilverTrophy.checkTrophy(username, elementId,trophyActionName);
        tvWatcherGoldTrophy.checkTrophy(username, elementId,trophyActionName);
        tvWatcherPlatineTrophy.checkTrophy(username, elementId,trophyActionName);

    }
}
