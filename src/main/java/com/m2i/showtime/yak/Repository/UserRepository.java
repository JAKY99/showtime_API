package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Dto.UserSimpleDto;
import com.m2i.showtime.yak.Dto.UserWatchedMovieDto;
import com.m2i.showtime.yak.Entity.User;
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

    @Query("SELECT u FROM User u JOIN u.watchlistMovies w WHERE u.username = ?1 and w.tmdbId = ?2")
    Optional<UserSimpleDto> isMovieInMovieToWatch(String email, long tmdbId);
    @Query("SELECT u FROM User u JOIN u.role r  WHERE r.role='ADMIN'")
    Optional<User[]> findAllAdminUsers();

    @Query("SELECT u FROM User u JOIN u.watchedSeries w WHERE u.username = ?1 and w.tmdbId = ?2")
    Optional<UserSimpleDto> isEpisodeWatched(String email, long serieTmdbId, long seasonNumber, long episodeNumber);
//    Unifinished a reprendre plus tard


}
