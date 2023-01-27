package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Entity.VersionControle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.TypedQuery;
import java.util.Optional;

public interface VersionControleRepository extends JpaRepository<VersionControle, Long> {

    @Query("SELECT v FROM VersionControle v WHERE v.type = ?1 ORDER BY v.version DESC")
    Page<VersionControle> findByType(String type, Pageable pageable);
}