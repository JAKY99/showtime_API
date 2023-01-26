package com.m2i.showtime.yak;

import com.amazonaws.services.appstream.model.Application;
import com.m2i.showtime.yak.Entity.Notification;
import com.m2i.showtime.yak.Repository.NotificationRepository;
import com.m2i.showtime.yak.common.notification.NotificationStatus;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class NotificationTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Test
    public void testNotification() {
       Notification notification = new Notification();
         notification.setMessage("test");
            notification.setSeverity("test");
            notification.setType("test");
            notification.setStatus(NotificationStatus.UNREAD);
            Date dateCreated = new Date();
            notification.setDateCreated(dateCreated);
        notificationRepository.save(notification);
        assertEquals("test",notification.getMessage());
        assertEquals("test",notification.getSeverity());
        assertEquals("test",notification.getType());
        assertEquals(NotificationStatus.UNREAD,notification.getStatus());
        assertEquals(dateCreated,notification.getDateCreated());
    }
}
