package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Dto.Search.SortingDto;
import com.m2i.showtime.yak.Entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @Query(value = "SELECT p FROM Permission p OFFSET ?1 LIMIT ?2", nativeQuery = true)
    Optional<List> getPermissionsList(int offset, int limit, SortingDto sort);
}
