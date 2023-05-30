package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Dto.UserSimpleDto;
import com.m2i.showtime.yak.Dto.UserWatchedMovieDto;
import com.m2i.showtime.yak.Entity.*;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT u FROM User u JOIN u.watchedSeries w WHERE u.username = ?1 and w.tmdbId = ?2")
    Optional<UserSimpleDto> isSerieWatched(String email, long tmdbId);
    @Query("SELECT u FROM User u JOIN u.favoriteMovies w WHERE u.username = ?1 and w.tmdbId = ?2")
    Optional<UserSimpleDto> isMovieInFavorite(String email, long tmdbId);
    @Query("SELECT u FROM User u JOIN u.favoriteSeries w WHERE u.username = ?1 and w.tmdbId = ?2")
    Optional<UserSimpleDto> isTvInFavorite(String email, long tmdbId);

    @Query("SELECT u FROM User u JOIN u.watchlistSeries w WHERE u.username = ?1 and w.tmdbId = ?2")
    Optional<UserSimpleDto> isTvInWatchlistSeries(String email, long tmdbId);
    @Query("SELECT u FROM User u WHERE  u.username = ?1")
    Optional<UserSimpleDto[]> getLastWatchedSeries(String username);
    @Query("SELECT u FROM User u JOIN u.watchlistMovies w WHERE u.username = ?1 and w.tmdbId = ?2")
    Optional<UserSimpleDto> isMovieInMovieToWatch(String email, long tmdbId);
    @Query("SELECT u FROM User u JOIN u.role r  WHERE r.role='ADMIN'")
    Optional<User[]> findAllAdminUsers();
    @Query("SELECT u FROM User u JOIN u.role r  " +
            "WHERE r.role not like 'ADMIN' " +
            "AND u.firstName LIKE %?1%" +
            "OR u.lastName LIKE %?1%" +
            "OR u.username LIKE %?1%" +
            "AND u.isDeleted = false")
    User[] searchUser(String searchText);
    @Query("SELECT u FROM User u JOIN u.role r  WHERE r.role not like 'ADMIN' AND u.isDeleted = false ORDER BY u.totalMovieWatchedNumber DESC")
    User[] getTopTen();

    @Query("SELECT u FROM UsersWatchedEpisode u WHERE u.user.username = ?1 and u.episode.imbd_id = ?2")
    Optional<UsersWatchedEpisode> isEpisodeWatched(String email , long episodeTmdbId);

    @Query("SELECT u FROM UsersWatchedSeason u WHERE u.user.username = ?1 and u.season.tmdbSeasonId = ?2")
    Optional<UsersWatchedSeason> getSeasonStatus(String email , long seasonTmdbId);


    @Query(value = "SELECT * FROM get_users_with_role_user()", nativeQuery = true)
    Optional<User[]> getUsersWithRoleUser();
}
