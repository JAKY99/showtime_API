package com.m2i.showtime.yak;

import com.amazonaws.services.appstream.model.Application;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes= Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
class YakApplicationTests {

	@Test
	void contextLoads() {
	}

}
