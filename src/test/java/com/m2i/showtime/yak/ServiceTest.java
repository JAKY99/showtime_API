package com.m2i.showtime.yak;
import com.amazonaws.services.appstream.model.Application;
import com.m2i.showtime.yak.Configuration.HazelcastConfig;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Permission;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Service.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceTest {
    @Autowired
    private KafkaMessageGeneratorService kafkaMessageGeneratorService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private CustomThreadService customThreadService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private HazelcastService hazelcastService;
    @Autowired
    private HazelcastConfig hazelcastConfig;
    @Autowired
    private VersionControleService versionControleService;
    @Mock
    HttpServletResponse HttpServletResponse;
    @Test
    public void testAllService() throws URISyntaxException, IOException, InterruptedException {
        KafkaMessageDto kafkaMessageDto = new KafkaMessageDto();
        kafkaMessageDto.setTopicName("test");
        kafkaMessageDto.setMessage("test");
        KafkaResponseDto response = kafkaMessageGeneratorService.sendMessage(kafkaMessageDto);
        assertEquals("Sending message to topic: testWith message : test" ,response.getResponseMessage());
        //--------------------------------
        getDataFromRedisDto redisCache = redisService.getRedisCacheData("https://api.themoviedb.org/3/movie/top_rated?api_key=268e205e4732543417f057b681731e09",HttpServletResponse);
        assertTrue(redisCache.getData() instanceof String && redisCache.getData() != null);
        //--------------------------------
        Permission permission = permissionService.getPermission(1L);
        assertEquals("movie:manage" ,permission.getPermission());
        //--------------------------------
        List<Role> roles = roleService.getRoles();
        assertTrue(roles instanceof List<Role> && roles.toArray().length > 0);
        //--------------------------------
        LoggerService loggerService = new LoggerService();
        Boolean logResult = loggerService.printTest("test");
        assertEquals(true ,logResult);
        //--------------------------------
        customThreadService.setName("test");
        String threadResponse =customThreadService.getName();
        assertEquals("test" ,threadResponse);
        //--------------------------------
        elasticsearchService.setElasticbaseUrl("test");
        assertEquals("test" ,elasticsearchService.getElasticbaseUrl());
        //--------------------------------
        versionControleService.addVersion("test");
        assertTrue(versionControleService.getVersion("test") instanceof VersionControlDto && versionControleService.getVersion("test") != null);
    }
}
