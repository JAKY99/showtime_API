package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Repository.TrophyRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.common.trophy.list.MovieWatcherBronzeTrophy;
import com.m2i.showtime.yak.common.trophy.list.MovieWatcherPlatineTrophy;
import com.m2i.showtime.yak.common.trophy.list.MovieWatcherSilverTrophy;
import com.m2i.showtime.yak.common.trophy.list.MovieWatchergGoldTrophy;
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

    public TrophyService(TrophyRepository trophyRepository, UserRepository userRepository, MovieWatcherBronzeTrophy movieWatcherBronzeTrophy, MovieWatcherSilverTrophy movieWatcherSilverTrophy, MovieWatchergGoldTrophy movieWatchergGoldTrophy, MovieWatcherPlatineTrophy movieWatcherPlatineTrophy) {
        this.trophyRepository = trophyRepository;
        this.userRepository = userRepository;
        this.movieWatcherBronzeTrophy = movieWatcherBronzeTrophy;
        this.movieWatcherSilverTrophy = movieWatcherSilverTrophy;
        this.movieWatchergGoldTrophy = movieWatchergGoldTrophy;
        this.movieWatcherPlatineTrophy = movieWatcherPlatineTrophy;
    }
    @Async
    public void checkAllTrophys(String username, long elementId) {
        movieWatcherBronzeTrophy.checkTrophy(username, elementId);
        movieWatcherSilverTrophy.checkTrophy(username, elementId);
        movieWatchergGoldTrophy.checkTrophy(username, elementId);
        movieWatcherPlatineTrophy.checkTrophy(username, elementId);
    }
}
