package com.m2i.showtime.yak;
import com.amazonaws.services.appstream.model.Application;
import com.m2i.showtime.yak.Configuration.HazelcastConfig;
import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Dto.getImageFromRedisDto;
import com.m2i.showtime.yak.Entity.Permission;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Service.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes= Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceTest {
    @Mock
    private KafkaMessageGeneratorService kafkaMessageGeneratorService;
    @Mock
    private RedisService redisService;
    @Mock
    private PermissionService permissionService;
    @Mock
    private RoleService roleService;
    @Mock
    private CustomThreadService customThreadService;
    @Mock
    private ElasticsearchService elasticsearchService;
    @Mock
    private HazelcastService hazelcastService;
    @Mock
    private HazelcastConfig hazelcastConfig;
    @Mock
    private KafkaListenerService kafkaListenerService;
    @Mock
    private VersionControleService versionControleService;
    @Test
    public void testAllService() {
        KafkaMessageDto kafkaMessageDto = new KafkaMessageDto();
        kafkaMessageDto.setTopicName("test");
        kafkaMessageDto.setMessage("test");
        String response = kafkaMessageGeneratorService.sendMessage(kafkaMessageDto);
        assertEquals(null ,response);
        //--------------------------------
        getImageFromRedisDto redisCache = redisService.getRedisCache("test");
        assertEquals(null ,redisCache);
        //--------------------------------
        Permission permission = permissionService.getPermission(1L);
        assertEquals(null ,permission);
        //--------------------------------
        List<Role> roles = roleService.getRoles();
        assertEquals( new ArrayList<>(),roles);
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
        assertEquals(null ,elasticsearchService.getElasticbaseUrl());
        //--------------------------------
        hazelcastService.setHazelcastConfig(hazelcastConfig);
        assertEquals(null ,hazelcastService.getHazelcastConfig());
        //--------------------------------
        kafkaListenerService.setEnv("test");
        assertEquals(null ,kafkaListenerService.getEnv());
        //--------------------------------
        versionControleService.addVersion("test");
        assertEquals(null ,versionControleService.getVersion("test"));


    }
}
