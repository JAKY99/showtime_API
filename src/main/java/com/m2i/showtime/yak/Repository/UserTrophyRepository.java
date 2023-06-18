package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.UserTrophy;
import com.m2i.showtime.yak.Entity.UserTrophyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserTrophyRepository extends JpaRepository<UserTrophy, UserTrophyId> {

    @Query("SELECT ut FROM UserTrophy ut WHERE ut.user.username = ?1 AND ut.trophy.name = ?2")
    public UserTrophy findByUserUsernameAndTrophyName(String username, String trophyName);
}