package com.m2i.showtime.yak.Controller.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Episode;
import com.m2i.showtime.yak.Entity.Serie;
import com.m2i.showtime.yak.Entity.Notification;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Entity.UsersWatchedSeries;
import com.m2i.showtime.yak.Enum.Status;
import com.m2i.showtime.yak.Jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.m2i.showtime.yak.Service.User.UserAuthService;
import com.m2i.showtime.yak.Service.User.UserService;
import org.apache.kafka.common.metrics.Stat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.Set;


@RestController
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
@RequestMapping(path = "api/v1/user")
public class UserController {
    private final UserService userService;
    private final UserAuthService userAuthService;

    public UserController(UserService userService, UserAuthService userAuthService) {
        this.userService = userService;
        this.userAuthService = userAuthService;
    }

    @PreAuthorize("hasAnyAuthority('user:read', 'user:manage_users')")
    @GetMapping("{userId}")
    public Optional<UserSimpleDto> getUser(@PathVariable("userId") Long userId) {
        return userService.getUser(userId);
    }

    @PreAuthorize("hasAnyAuthority('user:read', 'user:manage_users')")
    @PostMapping("/loggedin")
    public Optional<UserSimpleDto> getUser(@RequestBody String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterDto RegisterDto) throws JsonProcessingException {
        userAuthService.register(RegisterDto);
    }

    @PreAuthorize("hasAnyAuthority('user:edit', 'user:manage_users')")
    @PutMapping("{userId}")
    public void updateUser(
            @PathVariable("userId") Long userId, @RequestBody User modifiedUser) {
        userService.updateUser(userId, modifiedUser);
    }

    @PreAuthorize("hasAnyAuthority('user:edit')")
    @PutMapping("/edit-account")
    public void editAccount(
            @RequestBody EditAccountInfosDto modifiedUser, Authentication authentication) {
        Optional<UserSimpleDto> userSimpleDto = userService.getUserByEmail(authentication.getPrincipal()
                                                                                         .toString());
        long IdUser = userSimpleDto.orElseThrow(() -> {
                                       throw new IllegalStateException("User not found.");
                                   })
                                   .getId();
        userService.editAccountInfos(IdUser, modifiedUser);
    }

    @PreAuthorize("hasAnyAuthority('user:delete', 'user:manage_users')")
    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }


    @PostMapping("/isMovieInWatchlist")
    public boolean isMovieInWatchlist(@RequestBody UserWatchedMovieDto userWatchedMovieDto) {
        return userService.isMovieInWatchlist(userWatchedMovieDto);
    }

    @PostMapping("/isMovieInMovieToWatchlist")
    public boolean isMovieInMovieToWatchlist(@RequestBody UserWatchedMovieAddDto userWatchedMovieAddDto) {
        return userService.isMovieInMovieToWatchlist(userWatchedMovieAddDto);
    }

    @PostMapping("/isMovieInFavoritelist")
    public boolean isMovieInFavoritelist(@RequestBody UserWatchedMovieAddDto userWatchedMovieAddDto) {
        return userService.isMovieInFavoritelist(userWatchedMovieAddDto);
    }

    @PostMapping("/addMovieInWatchlist")
    public boolean addMovieInWatchlist(
            @RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        return userService.addMovieInWatchlist(UserWatchedMovieAddDto);
    }

    @PostMapping("/toggleMovieInFavoritelist")
    public boolean toggleMovieInFavoritelist(@RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) {
        return userService.toggleMovieInFavoritelist(UserWatchedMovieAddDto);
    }

    @PostMapping("/toggleMovieInMovieToWatchlist")
    public boolean toggleMovieInMovieToWatchlist(@RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) {
        return userService.toggleMovieInMovieToWatchlist(UserWatchedMovieAddDto);
    }

    @PostMapping("/removeMovieInWatchlist")
    public boolean removeMovieInWatchlist(
            @RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        userService.removeMovieInWatchlist(UserWatchedMovieAddDto);
        return true;
    }

    @PostMapping("/lastWatchedMoviesRange")
    public fetchRangeListDto lastWatchedMoviesRange(@RequestBody fetchRangeDto fetchRangeDto) {
        return userService.lastWatchedMoviesRange(fetchRangeDto);

    }

    @PostMapping("/favoritesMoviesRange")
    public fetchRangeListDto favoritesMoviesRange(@RequestBody fetchRangeDto fetchRangeDto) {
        return userService.favoritesMoviesRange(fetchRangeDto);
    }

    @PostMapping("/watchlistMoviesRange")
    public fetchRangeListDto watchlistMoviesRange(@RequestBody fetchRangeDto fetchRangeDto) {
        return userService.watchlistMoviesRange(fetchRangeDto);
    }

    @PostMapping("/increaseWatchedNumber")
    public boolean increaseWatchedNumber(@RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) {
        userService.increaseWatchedNumber(UserWatchedMovieAddDto);
        return true;
    }

    @PostMapping("/uploadProfilePicture")
    public UploadPictureDtoResponse uploadProfilePic(
            @RequestParam("email") String email, @RequestParam("file") MultipartFile file) throws IOException {
        return userService.uploadProfilePic(email, file);
    }

    @PostMapping("/uploadBackgroundPicture")
    public UploadBackgroundDtoResponse uploadBackgroundPic(
            @RequestParam("email") String email, @RequestParam("file") MultipartFile file) throws IOException {
        return userService.uploadBackgroundPic(email, file);
    }

    @PostMapping("profile/lazy/header")
    public ProfileLazyUserDtoHeader getProfileHeader(
            @RequestBody String email) throws URISyntaxException, IOException, InterruptedException {
        return userService.getProfileHeaderData(email);
    }

    @PostMapping("profile/lazy/avatar")
    public ProfileLazyUserDtoAvatar getProfileAvatar(@RequestBody String email) {
        return userService.getProfileAvatar(email);
    }

    @PostMapping("profile/lazy/socialInfos")
    public ProfileLazyUserDtoSocialInfos getProfileSocialInfos(@RequestBody String email) {
        return userService.getProfileSocialInfos(email);
    }

    @PostMapping("profile/lazy/socialInfos/followingStatus")
    public SocialFollowingResponseDto getProfileSocialInfosFollowing(
            @RequestBody SocialFollowingRequestDto information) {
        return userService.getFollowingStatus(information);
    }

    @PostMapping("profile/lazy/lastWatchedMovies")
    public ProfileLazyUserDtoLastWatchedMovies getProfileLastWatchedMovies(@RequestBody String email) {
        return userService.getProfileLastWatchedMoviesData(email);
    }

    @PostMapping("/addSerieInWatchlist")
    public boolean addSerieInWatchlist(@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        return userService.addSerieInWatchlist(userWatchedSerieAddDto);
    }

    @PostMapping("/addSeasonInWatchlist")
    public Status addSeasonInWatchlist(@RequestBody UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        return userService.addSeasonInWatchlist(userWatchedTvSeasonAddDto);
    }

    @PostMapping("/addEpisodeInWatchlist")
    public boolean addEpisodeInWatchlist(@RequestBody UserWatchedTvEpisodeAddDto userWatchedTvEpisodeAddDto) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        return userService.addEpisodeInWatchlist(userWatchedTvEpisodeAddDto);
    }

    @PostMapping("/isSerieInWatchlist")
    public StatusDto isTvInWatchlist(@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) throws URISyntaxException, IOException, InterruptedException {
        return userService.isTvInWatchlist(userWatchedSerieAddDto);
    }

    @PostMapping("/nbEpisodesWatchedInSerie")
    public Long getNbEpisodesWatchedForSeason(@RequestBody UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto) throws URISyntaxException, IOException, InterruptedException {
        return userService.getNbEpisodesWatchedForSeason(userWatchedTvSeasonAddDto.getTvSeasonid(), userWatchedTvSeasonAddDto.getUserMail());
    }

    @PostMapping("/isEpisodeInWatchlist")
    public boolean isEpisodeInWatchlist(@RequestBody UserWatchedEpisodeDto userWatchedEpisodeDto) {
        return userService.isEpisodeInWatchlist(userWatchedEpisodeDto);
    }

    @PostMapping("/isSeasonInWatchlist")
    public Status isSeasonInWatchlist(@RequestBody UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto) {
        return userService.isSeasonInWatchlist(userWatchedTvSeasonAddDto);
    }

    @PostMapping("/getLastSeenEpisode")
    public Episode getLastSeenEpisode(@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) {
        return userService.getLastSeenEpisode(userWatchedSerieAddDto);
    }

    @PostMapping("/fetchTvWatching")
    public ArrayList<Long> fetchTvWatching(@RequestBody UserMailDto userMailDto) {
        return userService.fetchTvWatching(userMailDto);
    }

    @PostMapping("/fetchTvWatched")
    public ArrayList<Long> fetchTvWatched(@RequestBody UserMailDto userMailDto) {
        return userService.fetchTvWatched(userMailDto);
    }



    @GetMapping("/refresh")
    public void refreshToken(
            HttpServletResponse response, @RequestHeader HashMap refreshToken) throws ServletException, IOException {
        JwtUsernameAndPasswordAuthenticationFilter.refreshToken(
                response, refreshToken.get("refresh")
                                      .toString());

    }

    @PostMapping("social/info")
    public SocialInfoDto getSocialPageInfo(@RequestBody String email) {
        return userService.getSocialPageInfo(email);
    }

    @PostMapping("social/search/user")
    public SocialSearchResponseDto[] searchUser(@RequestBody String searchText) {
        return userService.searchUser(searchText);
    }

    @PostMapping("social/search/user/detail")
    public SocialInfoDto getUserSearchDetail(@RequestBody String username) {
        return userService.getSocialDetail(username);
    }

    @PostMapping("social/topten")
    public SocialTopTenUserDto[] getTopTen(@RequestBody String searchText) {
        return userService.getTopTenUsers();
    }

    @PutMapping("exclude/actor/{IdActor}")
    public void excludeActor(@PathVariable("IdActor") Long IdActor, Authentication authentication) {
        Optional<UserSimpleDto> userSimpleDto = userService.getUserByEmail(authentication.getPrincipal()
                                                                                         .toString());
        long IdUser = userSimpleDto.orElseThrow(() -> {
                                       throw new IllegalStateException("User not found.");
                                   })
                                   .getId();
        userService.excludeActor(IdActor, IdUser);
    }

    @PutMapping("exclude/genre/{IdGenre}")
    public void excludeGenre(@PathVariable("IdGenre") Long IdGenre, Authentication authentication) {
        Optional<UserSimpleDto> userSimpleDto = userService.getUserByEmail(authentication.getPrincipal()
                                                                                         .toString());
        long IdUser = userSimpleDto.orElseThrow(() -> {
                                       throw new IllegalStateException("User not found.");
                                   })
                                   .getId();
        userService.excludeGenre(IdGenre, IdUser);
    }

    @PostMapping("profile/lazy/socialAction/followUser")
    public SocialFollowingResponseDto actionFollowUser(@RequestBody SocialFollowingRequestDto information) {
        return userService.actionFollowUser(information);
    }

    @PostMapping("profile/lazy/socialAction/unfollowUser")
    public SocialFollowingResponseDto actionUnfollowUser(@RequestBody SocialFollowingRequestDto information) {
        return userService.actionUnfollowUser(information);
    }

    @PostMapping("get/notifications")
    public Set<Notification> getUserNotifications(@RequestBody String email) {
        return userService.getUserNotification(email);
    }

    @PostMapping("update/notifications")
    public boolean updateUserNotifications(@RequestBody String email) {
        return userService.updateUserNotification(email);
    }

    @PostMapping("/tempForCrop/uploadProfilePicture")
    public UploadPictureDtoResponse uploadProfilePicTempForCrop(
            @RequestParam("email") String email, @RequestParam("file") MultipartFile file) throws IOException {
        return userService.uploadProfilePicTempForCrop(email, file);
    }

    @PostMapping("/tempForCrop/uploadBackgroundPicture")
    public UploadBackgroundDtoResponse uploadBackgroundPicTempForCrop(
            @RequestParam("email") String email, @RequestParam("file") MultipartFile file) throws IOException {
        return userService.uploadBackgroundPicTempForCrop(email, file);
    }

    @PostMapping("profile/lazy/tempForCrop")
    public ProfileLazyUserDtoAvatar getProfileAvatarTempForCrop(@RequestBody String email) {
        return userService.getTempForCropUrl(email);
    }
}
