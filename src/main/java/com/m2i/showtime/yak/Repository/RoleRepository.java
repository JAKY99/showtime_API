package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.role = ?1")
    Optional<Role> findByRole(String role);
}
