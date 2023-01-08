package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Dto.UserSimpleDto;
import com.m2i.showtime.yak.Dto.UserWatchedMovieDto;
import com.m2i.showtime.yak.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.isDeleted = false")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.isDeleted = false")
    Optional<UserSimpleDto> findSimpleUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = ?1")
    Optional<UserSimpleDto> findSimpleUserById(Long userId);

    @Query("SELECT u FROM User u JOIN u.watchedMovies w WHERE u.username = ?1 and w.tmdbId = ?2")
    Optional<UserSimpleDto> isMovieWatched(String email, long tmdbId);
    @Query("SELECT u FROM User u JOIN u.role r  WHERE r.role='ADMIN'")
    Optional<User[]> findAllAdminUsers();

}
