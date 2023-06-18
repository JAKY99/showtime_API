package com.m2i.showtime.yak;
import com.amazonaws.services.appstream.model.Application;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class DtoTest {
    @Mock
    private RoleRepository roleRepository;
    @Test
    public void testAllDto() {

         AddUserAGgridDto addUserAGgridDto = new AddUserAGgridDto();
         addUserAGgridDto.setFirstName("test");
         addUserAGgridDto.setLastName("test");
         addUserAGgridDto.setCountry("test");
         addUserAGgridDto.setPassword("test");
         assertEquals("test",addUserAGgridDto.getFirstName());
         assertEquals("test",addUserAGgridDto.getLastName());
         assertEquals("test",addUserAGgridDto.getCountry());
         assertEquals("test",addUserAGgridDto.getPassword());
         //---------------------------------------------------------------------------------
         fetchRangeDto fetchRangeDto = new fetchRangeDto();
         fetchRangeDto.setCurrentLength(1);
         fetchRangeDto.setUserMail("test");
         assertEquals("test",fetchRangeDto.getUserMail());
         assertEquals(1,fetchRangeDto.getCurrentLength());
         //---------------------------------------------------------------------------------
         fetchRangeListDto fetchRangeListDto = new fetchRangeListDto();
         fetchRangeListDto.setTmdbIdList(new long[]{1,2,3});
         assertEquals(3,fetchRangeListDto.getTmdbIdList().length);
         //---------------------------------------------------------------------------------
         getDataFromHazelcastDto getDataFromHazelcastDto = new getDataFromHazelcastDto();
         getDataFromHazelcastDto.setData("test");
         assertEquals("test",getDataFromHazelcastDto.getData());
         //---------------------------------------------------------------------------------
         getDataFromRedisDto getDataFromRedisDto = new getDataFromRedisDto();
         getDataFromRedisDto.setData("test");
         assertEquals("test",getDataFromRedisDto.getData());
         //---------------------------------------------------------------------------------
         getImageFromHazelcastDto getImageFromHazelcastDto = new getImageFromHazelcastDto();
         getImageFromHazelcastDto.setUrlApi("test");
         assertEquals("test",getImageFromHazelcastDto.getUrlApi());
         //---------------------------------------------------------------------------------
         getImageFromRedisDto getImageFromRedisDto = new getImageFromRedisDto();
         getImageFromRedisDto.setUrlApi("test");
         assertEquals("test",getImageFromRedisDto.getUrlApi());
         //---------------------------------------------------------------------------------
         HealthCheckStatus healthCheckStatus = new HealthCheckStatus("test");
         assertEquals("test",healthCheckStatus.status());
         //---------------------------------------------------------------------------------
         InsertMovieBulkElasticDto insertMovieBulkElasticDto = new InsertMovieBulkElasticDto();
         insertMovieBulkElasticDto.setUserId(1);
         insertMovieBulkElasticDto.setMovieIds(new int[]{1,2,3});
         assertEquals(1,insertMovieBulkElasticDto.getUserId());
         assertEquals(3,insertMovieBulkElasticDto.getMovieIds().length);
         //---------------------------------------------------------------------------------
         KafkaMessageDto kafkaMessageDto = new KafkaMessageDto();
         kafkaMessageDto.setTopicName("test");
         kafkaMessageDto.setMessage("test");
         assertEquals("test",kafkaMessageDto.getTopicName());
         assertEquals("test",kafkaMessageDto.getMessage());
         //---------------------------------------------------------------------------------
         lastWatchedMoviesDto lastWatchedMoviesDto = new lastWatchedMoviesDto();
         lastWatchedMoviesDto.setTmdbId(1);
         assertEquals(1,lastWatchedMoviesDto.getTmdbId());
         //---------------------------------------------------------------------------------
         MessageAdminDto messageAdminDto = new MessageAdminDto();
         messageAdminDto.setType("test");
         messageAdminDto.setMessage("test");
         assertEquals("test",messageAdminDto.getType());
         assertEquals("test",messageAdminDto.getMessage());
         //---------------------------------------------------------------------------------
         ProfileLazyUserDtoAvatar profileLazyUserDtoAvatar = new ProfileLazyUserDtoAvatar();
         profileLazyUserDtoAvatar.setFullName("test");
         profileLazyUserDtoAvatar.setProfilePicture("test");
         profileLazyUserDtoAvatar.setBackgroundPicture("test");
         assertEquals("test",profileLazyUserDtoAvatar.getFullName());
         assertEquals("test",profileLazyUserDtoAvatar.getProfilePicture());
         assertEquals("test",profileLazyUserDtoAvatar.getBackgroundPicture());
         //---------------------------------------------------------------------------------
         ProfileLazyUserDtoHeader profileLazyUserDtoHeader = new ProfileLazyUserDtoHeader();
         profileLazyUserDtoHeader.setNumberOfWatchedMovies(1);
         profileLazyUserDtoHeader.setNumberOfWatchedSeries(1);
         profileLazyUserDtoHeader.setTotalTimeWatchedMovies("test");
         profileLazyUserDtoHeader.setTotalTimeWatchedSeries("test");
         assertEquals(1,profileLazyUserDtoHeader.getNumberOfWatchedMovies());
         assertEquals(1,profileLazyUserDtoHeader.getNumberOfWatchedSeries());
         assertEquals("test",profileLazyUserDtoHeader.getTotalTimeWatchedMovies());
         assertEquals("test",profileLazyUserDtoHeader.getTotalTimeWatchedSeries());
         //---------------------------------------------------------------------------------
         ProfileLazyUserDtoLastWatchedMovies profileLazyUserDtoLastWatchedMovies = new ProfileLazyUserDtoLastWatchedMovies();
         profileLazyUserDtoLastWatchedMovies.setTotalFavoritesMovies(1);
         profileLazyUserDtoLastWatchedMovies.setTotalFavoritesMovies(1);
         profileLazyUserDtoLastWatchedMovies.setFavoritesMovies(new long[]{1,2,3});
         profileLazyUserDtoLastWatchedMovies.setLastWatchedMovies(new long[]{1,2,3});
         assertEquals(1,profileLazyUserDtoLastWatchedMovies.getTotalFavoritesMovies());
         assertEquals(1,profileLazyUserDtoLastWatchedMovies.getTotalFavoritesMovies());
         assertEquals(3,profileLazyUserDtoLastWatchedMovies.getFavoritesMovies().length);
         assertEquals(3,profileLazyUserDtoLastWatchedMovies.getLastWatchedMovies().length);
         //---------------------------------------------------------------------------------
         ProfileLazyUserDtoSocialInfos profileLazyUserDtoSocialInfos = new ProfileLazyUserDtoSocialInfos();
         profileLazyUserDtoSocialInfos.setCommentsCounter(1L);
         profileLazyUserDtoSocialInfos.setFollowersCounter(1L);
         profileLazyUserDtoSocialInfos.setFollowingsCounter(1L);
         assertEquals(1L,profileLazyUserDtoSocialInfos.getCommentsCounter().longValue());
         assertEquals(1L,profileLazyUserDtoSocialInfos.getFollowersCounter().longValue());
         assertEquals(1L,profileLazyUserDtoSocialInfos.getFollowingsCounter().longValue());
         //---------------------------------------------------------------------------------
         RedisCacheDto redisCacheDto = new RedisCacheDto();
         redisCacheDto.setUrlApi("test");
         assertEquals("test",redisCacheDto.getUrlApi());
         //---------------------------------------------------------------------------------
         RedisPictureDto redisPictureDto = new RedisPictureDto();
         redisPictureDto.setBackdrop_path("test");
         redisPictureDto.setPoster_path("test");
         assertEquals("test",redisPictureDto.getBackdrop_path());
         assertEquals("test",redisPictureDto.getPoster_path());
         //---------------------------------------------------------------------------------
         RegisterDto registerDto = new RegisterDto();
         registerDto.setUsername("test");
         registerDto.setPassword("test");
         assertEquals("test",registerDto.getUsername());
         assertEquals("test",registerDto.getPassword());
         //---------------------------------------------------------------------------------
         ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
         resetPasswordDto.setUsername("test");
         resetPasswordDto.setPassword("test");
         resetPasswordDto.setPasswordConfirm("test");
         assertEquals("test",resetPasswordDto.getUsername());
         assertEquals("test",resetPasswordDto.getPassword());
         assertEquals("test",resetPasswordDto.getPasswordConfirm());
         //---------------------------------------------------------------------------------
         ResetPasswordMailingDto resetPasswordMailingDto = new ResetPasswordMailingDto();
         resetPasswordMailingDto.setUsername("test");
         assertEquals("test",resetPasswordMailingDto.getUsername());
          //---------------------------------------------------------------------------------
         ResetPasswordUseDto resetPasswordUseDto = new ResetPasswordUseDto();
         resetPasswordUseDto.setToken("test");
         resetPasswordUseDto.setPassword("test");
         assertEquals("test",resetPasswordUseDto.getToken());
         assertEquals("test",resetPasswordUseDto.getPassword());
         //---------------------------------------------------------------------------------
         ResponseApiAgGridDto responseApiAgGridDto = new ResponseApiAgGridDto();
         responseApiAgGridDto.setTitle("test");
         responseApiAgGridDto.setDetails("test");
         responseApiAgGridDto.setSeverity("test");
         responseApiAgGridDto.setSticky(true);
         assertEquals("test",responseApiAgGridDto.getTitle());
         assertEquals("test",responseApiAgGridDto.getDetails());
         assertEquals("test",responseApiAgGridDto.getSeverity());
         assertEquals(true,responseApiAgGridDto.isSticky());
         //---------------------------------------------------------------------------------
         RunInsertBulkDto runInsertBulkDto = new RunInsertBulkDto();
         runInsertBulkDto.setPage(1);
         runInsertBulkDto.setCurrentUrl("test");
         runInsertBulkDto.setElasticbaseUrl("test");
         runInsertBulkDto.setUrlMovieUserElastic("test");
         assertEquals(1,runInsertBulkDto.getPage());
         assertEquals("test",runInsertBulkDto.getCurrentUrl());
         assertEquals("test",runInsertBulkDto.getElasticbaseUrl());
         assertEquals("test",runInsertBulkDto.getUrlMovieUserElastic());
         //---------------------------------------------------------------------------------
         RunInsertFromIdDto runInsertFromIdDto = new RunInsertFromIdDto();
         runInsertFromIdDto.setApiKey("test");
         runInsertFromIdDto.setElasticbaseUrl("test");
         runInsertFromIdDto.setElementId(1);
         runInsertFromIdDto.setIndexUrlToUse(1);
         runInsertFromIdDto.setDateStrFormatted("test");
         assertEquals("test",runInsertFromIdDto.getApiKey());
         assertEquals("test",runInsertFromIdDto.getElasticbaseUrl());
         assertEquals(1,runInsertFromIdDto.getElementId());
         assertEquals(1,runInsertFromIdDto.getIndexUrlToUse());
         assertEquals("test",runInsertFromIdDto.getDateStrFormatted());
         //---------------------------------------------------------------------------------
         RunInsertRedisCacheDto runInsertRedisCacheDto = new RunInsertRedisCacheDto();
         runInsertRedisCacheDto.setUrlApi("test");
         assertEquals("test",runInsertRedisCacheDto.getUrlApi());











    }

}
