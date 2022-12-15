package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}