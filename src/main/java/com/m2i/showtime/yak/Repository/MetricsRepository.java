package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MetricsRepository extends JpaRepository<Metrics, Long> {

    @Query(value = "SELECT * FROM _metrics ORDER BY id DESC LIMIT 1", nativeQuery = true)
    List<Metrics> findLastMetrics();

    @Query(value = "SELECT setval('metric_sequence', 1 , false);", nativeQuery = true)
    void resetAutoIncrement();
}