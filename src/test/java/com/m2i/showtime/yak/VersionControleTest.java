package com.m2i.showtime.yak;
import com.amazonaws.services.appstream.model.Application;
import com.m2i.showtime.yak.Entity.VersionControle;
import com.m2i.showtime.yak.Repository.VersionControleRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes= Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
class VersionControleTest {

    @Mock
    private VersionControleRepository versionControleRepository;
    @Test
     void testVersionControle() {
       VersionControle versionControle = new VersionControle();
        LocalDateTime version = new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        versionControle.setVersion(version);
        versionControle.setType("test");
        versionControleRepository.save(versionControle);
        assertEquals(version,versionControle.getVersion());
        assertEquals("test",versionControle.getType());
    }
}
