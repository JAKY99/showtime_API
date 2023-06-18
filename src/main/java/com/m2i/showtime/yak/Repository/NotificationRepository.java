package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Dto.NotificationAgGridDto;
import com.m2i.showtime.yak.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = "SELECT n.* ,u.email as \"receiverName\"  FROM notification n INNER JOIN _user_notifications un on n.id=un.notifications_id INNER JOIN _user u on u.id=un.user_id", nativeQuery = true)
    List<Map<String, Object>> findAllNotificationAndAssociatedReciever();
}