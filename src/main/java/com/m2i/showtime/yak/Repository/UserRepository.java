package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Dto.UserSimpleDto;
import com.m2i.showtime.yak.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

}
