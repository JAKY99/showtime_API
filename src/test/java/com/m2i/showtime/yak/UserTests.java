package com.m2i.showtime.yak;

import com.amazonaws.services.appstream.model.Application;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.Service.User.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes= Application.class)
@RunWith(SpringJUnit4ClassRunner.class)

public class UserTests {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    public void testAdduser() {
        User user = new User("test", "test");
        User savedUser = userService.addUser(user);
        assertEquals("test", savedUser.getUsername());
    }
}
