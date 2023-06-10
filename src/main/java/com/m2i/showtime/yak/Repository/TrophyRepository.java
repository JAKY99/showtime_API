package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Trophy;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.common.trophy.TrophyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface TrophyRepository extends JpaRepository<Trophy, Long> {
    @Query("SELECT t FROM Trophy t WHERE t.name = ?1 and t.type = ?2")
    Optional<Trophy> findByNameAndType(String name, TrophyType type);

    @Query("SELECT t , ut.dateCreated as dateCreated FROM Trophy t JOIN UserTrophy ut on ut.user=?1 WHERE t.name = ?1 ORDER BY ut.dateCreated DESC")
    Set<Trophy> findAllbyUser(User user);
}