package com.m2i.showtime.yak.Controller.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Episode;
import com.m2i.showtime.yak.Entity.Notification;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Entity.UsersWatchedSeries;
import com.m2i.showtime.yak.Enum.Status;
import com.m2i.showtime.yak.Jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.m2i.showtime.yak.Service.User.UserAuthService;
import com.m2i.showtime.yak.Service.User.UserService;
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
    public boolean isMovieInWatchlist(Authentication authentication, @RequestBody UserWatchedMovieDto userWatchedMovieDto) {
        String username = userService.getUserFromJwt(authentication);
        userWatchedMovieDto.setUserMail(username);
        return userService.isMovieInWatchlist(userWatchedMovieDto);
    }

    @PostMapping("/isMovieInMovieToWatchlist")
    public boolean isMovieInMovieToWatchlist(Authentication authentication,@RequestBody UserWatchedMovieAddDto userWatchedMovieAddDto) {
        String username = userService.getUserFromJwt(authentication);
        userWatchedMovieAddDto.setUserMail(username);
        return userService.isMovieInMovieToWatchlist(userWatchedMovieAddDto);
    }

    @PostMapping("/isMovieInFavoritelist")
    public boolean isMovieInFavoritelist(Authentication authentication,@RequestBody UserWatchedMovieAddDto userWatchedMovieAddDto) {
        String username = userService.getUserFromJwt(authentication);
        userWatchedMovieAddDto.setUserMail(username);
        return userService.isMovieInFavoritelist(userWatchedMovieAddDto);
    }

    @PostMapping("/isTvInFavoritelist")
    public boolean isTvInFavoritelist(@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) {
        return userService.isTvInFavoritelist(userWatchedSerieAddDto);
    }

    @PostMapping("/isTvInWatchlistSeries")
    public boolean isTvInWatchlistSeries(@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) {
        return userService.isTvInWatchlistSeries(userWatchedSerieAddDto);
    }

    @PostMapping("/addMovieInWatchlist")
    public boolean addMovieInWatchlist(Authentication authentication,
            @RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        String username = userService.getUserFromJwt(authentication);
        UserWatchedMovieAddDto.setUserMail(username);
        return userService.addMovieInWatchlist(UserWatchedMovieAddDto);
    }

    @PostMapping("/toggleMovieInFavoritelist")
    public boolean toggleMovieInFavoritelist(Authentication authentication,@RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) {
        String username = userService.getUserFromJwt(authentication);
        UserWatchedMovieAddDto.setUserMail(username);
        return userService.toggleMovieInFavoritelist(UserWatchedMovieAddDto);
    }

    @PostMapping("/toggleTvInFavoritelist")
    public boolean toggleTvInFavoritelist(@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) throws IOException, URISyntaxException, InterruptedException {
        return userService.toggleTvInFavoritelist(userWatchedSerieAddDto);
    }

    @PostMapping("/toggleTvInWatchlist")
    public boolean toggleTvInWatchlist(@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) throws IOException, URISyntaxException, InterruptedException {
        return userService.toggleTvInWatchlist(userWatchedSerieAddDto);
    }

    @PostMapping("/toggleMovieInMovieToWatchlist")
    public boolean toggleMovieInMovieToWatchlist(Authentication authentication,@RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) {
        String username = userService.getUserFromJwt(authentication);
        UserWatchedMovieAddDto.setUserMail(username);
        return userService.toggleMovieInMovieToWatchlist(UserWatchedMovieAddDto);
    }

    @PostMapping("/removeMovieInWatchlist")
    public boolean removeMovieInWatchlist(Authentication authentication,
            @RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        String username = userService.getUserFromJwt(authentication);
        UserWatchedMovieAddDto.setUserMail(username);
        userService.removeMovieInWatchlist(UserWatchedMovieAddDto);
        return true;
    }

    @PostMapping("/lastWatchedMoviesRange")
    public fetchRangeListDto lastWatchedMoviesRange(Authentication authentication,@RequestBody fetchRangeDto fetchRangeDto) {
        String username = userService.getUserFromJwt(authentication);
        fetchRangeDto.setUserMail(username);
        return userService.lastWatchedMoviesRange(fetchRangeDto);

    }

    @PostMapping("/favoritesMoviesRange")
    public fetchRangeListDto favoritesMoviesRange(Authentication authentication,@RequestBody fetchRangeDto fetchRangeDto) {
        String username = userService.getUserFromJwt(authentication);
        fetchRangeDto.setUserMail(username);
        return userService.favoritesMoviesRange(fetchRangeDto);
    }

    @PostMapping("/watchlistMoviesRange")
    public fetchRangeListDto watchlistMoviesRange(Authentication authentication,@RequestBody fetchRangeDto fetchRangeDto) {
        String username = userService.getUserFromJwt(authentication);
        fetchRangeDto.setUserMail(username);
        return userService.watchlistMoviesRange(fetchRangeDto);
    }

    @PostMapping("/increaseWatchedNumber")
    public boolean increaseWatchedNumber(Authentication authentication,@RequestBody UserWatchedMovieAddDto UserWatchedMovieAddDto) {
        String username = userService.getUserFromJwt(authentication);
        UserWatchedMovieAddDto.setUserMail(username);
        userService.increaseWatchedNumber(UserWatchedMovieAddDto);
        return true;
    }

    @PostMapping("/uploadProfilePicture")
    public UploadPictureDtoResponse uploadProfilePic(Authentication authentication,
            @RequestParam("email") String email, @RequestParam("file") MultipartFile file) throws IOException {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.uploadProfilePic(email, file);
    }

    @PostMapping("/uploadBackgroundPicture")
    public UploadBackgroundDtoResponse uploadBackgroundPic(Authentication authentication,
            @RequestParam("email") String email, @RequestParam("file") MultipartFile file) throws IOException {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.uploadBackgroundPic(email, file);
    }

    @PostMapping("profile/lazy/header")
    public ProfileLazyUserDtoHeader getProfileHeader(Authentication authentication,
            @RequestBody String email) throws URISyntaxException, IOException, InterruptedException {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.getProfileHeaderData(email);
    }

    @PostMapping("profile/lazy/avatar")
    public ProfileLazyUserDtoAvatar getProfileAvatar(Authentication authentication,@RequestBody String email) {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.getProfileAvatar(email);
    }

    @PostMapping("profile/lazy/socialInfos")
    public ProfileLazyUserDtoSocialInfos getProfileSocialInfos(Authentication authentication,@RequestBody String email) {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.getProfileSocialInfos(email);
    }

    @PostMapping("profile/lazy/socialInfos/followingStatus")
    public SocialFollowingResponseDto getProfileSocialInfosFollowing(Authentication authentication,
            @RequestBody SocialFollowingRequestDto information) {
        String username = userService.getUserFromJwt(authentication);
        information.setUsernameRequester(username);
        return userService.getFollowingStatus(information);
    }

    @PostMapping("profile/lazy/lastWatchedMovies")
    public ProfileLazyUserDtoLastWatchedMovies getProfileLastWatchedMovies(Authentication authentication,@RequestBody String email) {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.getProfileLastWatchedMoviesData(email);
    }

    @PostMapping("/addSerieInWatchlist")
    public boolean addSerieInWatchlist(Authentication authentication,@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        String username = userService.getUserFromJwt(authentication);
        userWatchedSerieAddDto.setUserMail(username);
        return userService.addSerieInWatchlist(userWatchedSerieAddDto);
    }

    @PostMapping("/addSeasonInWatchlist")
    public Status addSeasonInWatchlist(Authentication authentication,@RequestBody UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        String username = userService.getUserFromJwt(authentication);
        userWatchedTvSeasonAddDto.setUserMail(username);
        return userService.addSeasonInWatchlist(userWatchedTvSeasonAddDto);
    }

    @PostMapping("/addEpisodeInWatchlist")
    public boolean addEpisodeInWatchlist(Authentication authentication,@RequestBody UserWatchedTvEpisodeAddDto userWatchedTvEpisodeAddDto) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        String username = userService.getUserFromJwt(authentication);
        userWatchedTvEpisodeAddDto.setUserMail(username);
        return userService.addEpisodeInWatchlist(userWatchedTvEpisodeAddDto);
    }

    @PostMapping("/isSerieInWatchlist")
    public StatusDto isTvInWatchlist(Authentication authentication,@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) throws URISyntaxException, IOException, InterruptedException {
        String username = userService.getUserFromJwt(authentication);
        userWatchedSerieAddDto.setUserMail(username);
        return userService.isTvInWatchlist(userWatchedSerieAddDto);
    }

    @PostMapping("/nbEpisodesWatchedInSerie")
    public Long getNbEpisodesWatchedForSeason(Authentication authentication,@RequestBody UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto) throws URISyntaxException, IOException, InterruptedException {
        String username = userService.getUserFromJwt(authentication);
        userWatchedTvSeasonAddDto.setUserMail(username);
        return userService.getNbEpisodesWatchedForSeason(userWatchedTvSeasonAddDto.getTvSeasonid(), userWatchedTvSeasonAddDto.getUserMail());
    }

    @PostMapping("/isEpisodeInWatchlist")
    public boolean isEpisodeInWatchlist(Authentication authentication,@RequestBody UserWatchedEpisodeDto userWatchedEpisodeDto) {
        String username = userService.getUserFromJwt(authentication);
        userWatchedEpisodeDto.setUserMail(username);
        return userService.isEpisodeInWatchlist(userWatchedEpisodeDto);
    }

    @PostMapping("/isSeasonInWatchlist")
    public Status isSeasonInWatchlist(Authentication authentication,@RequestBody UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto) {
        String username = userService.getUserFromJwt(authentication);
        userWatchedTvSeasonAddDto.setUserMail(username);
        return userService.isSeasonInWatchlist(userWatchedTvSeasonAddDto);
    }

    @PostMapping("/getLastSeenEpisode")
    public Episode getLastSeenEpisode(Authentication authentication,@RequestBody UserWatchedSerieAddDto userWatchedSerieAddDto) {
        String username = userService.getUserFromJwt(authentication);
        userWatchedSerieAddDto.setUserMail(username);
        return userService.getLastSeenEpisode(userWatchedSerieAddDto);
    }

    @PostMapping("/fetchTvWatching")
    public ArrayList<Long> fetchTvWatching(Authentication authentication,@RequestBody UserMailDto userMailDto) {
        String username = userService.getUserFromJwt(authentication);
        userMailDto.setUserMail(username);
        return userService.fetchTvWatching(userMailDto);
    }



    @PostMapping("/fetchTvWatched")
    public ArrayList<Long> fetchTvWatched(Authentication authentication,@RequestBody UserMailDto userMailDto) {
        String username = userService.getUserFromJwt(authentication);
        userMailDto.setUserMail(username);
        return userService.fetchTvWatched(userMailDto);
    }

    @PostMapping("/fetchLastTvSeriesWatched")
    public ArrayList<Long> fetchLastTvSeriesWatched(@RequestBody UserMailDto userMailDto) {
        return userService.fetchLastTvSeriesWatched(userMailDto);
    }
    @PostMapping("/fetchfavoritesSeries")
    public ArrayList<Long> fetchfavoritesSeries(@RequestBody UserMailDto userMailDto) {
        return userService.fetchfavoritesSeries(userMailDto);
    }

    @PostMapping("/fetchTvWatchlist")
    public ArrayList<Long> fetchTvWatchlist(@RequestBody UserMailDto userMailDto) {
        return userService.fetchTvWatchlist(userMailDto);
    }

    @GetMapping("/refresh")
    public void refreshToken(
            HttpServletResponse response, @RequestHeader HashMap refreshToken) throws ServletException, IOException {
        JwtUsernameAndPasswordAuthenticationFilter.refreshToken(
                response, refreshToken.get("refresh")
                                      .toString());

    }

    @PostMapping("social/info")
    public SocialInfoDto getSocialPageInfo(Authentication authentication,@RequestBody String email) {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.getSocialPageInfo(email);
    }

    @PostMapping("social/search/user")
    public SocialSearchResponseDto[] searchUser(Authentication authentication,@RequestBody String searchText) {
        String username = userService.getUserFromJwt(authentication);
        searchText = username;
        return userService.searchUser(searchText);
    }

    @PostMapping("social/search/user/detail")
    public SocialInfoDto getUserSearchDetail(Authentication authentication,@RequestBody String username) {
        String usernameFromJWT = userService.getUserFromJwt(authentication);
        username = usernameFromJWT;
        return userService.getSocialDetail(username);
    }

    @PostMapping("social/topten")
    public SocialTopTenUserDto[] getTopTen(Authentication authentication,@RequestBody String searchText) {
        String username = userService.getUserFromJwt(authentication);
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
    public SocialFollowingResponseDto actionFollowUser(Authentication authentication,@RequestBody SocialFollowingRequestDto information) {
        String username = userService.getUserFromJwt(authentication);
        information.setUsernameRequester(username);
        return userService.actionFollowUser(information);
    }

    @PostMapping("profile/lazy/socialAction/unfollowUser")
    public SocialFollowingResponseDto actionUnfollowUser(Authentication authentication,@RequestBody SocialFollowingRequestDto information) {
        String username = userService.getUserFromJwt(authentication);
        information.setUsernameRequester(username);
        return userService.actionUnfollowUser(information);
    }

    @PostMapping("get/notifications")
    public Set<Notification> getUserNotifications(Authentication authentication,@RequestBody String email) {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.getUserNotification(email);
    }

    @PostMapping("update/notifications")
    public boolean updateUserNotifications(Authentication authentication,@RequestBody String email) {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.updateUserNotification(email);
    }

    @PostMapping("/tempForCrop/uploadProfilePicture")
    public UploadPictureDtoResponse uploadProfilePicTempForCrop(Authentication authentication,
            @RequestParam("email") String email, @RequestParam("file") MultipartFile file) throws IOException {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.uploadProfilePicTempForCrop(email, file);
    }

    @PostMapping("/tempForCrop/uploadBackgroundPicture")
    public UploadBackgroundDtoResponse uploadBackgroundPicTempForCrop(Authentication authentication,
            @RequestParam("email") String email, @RequestParam("file") MultipartFile file) throws IOException {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.uploadBackgroundPicTempForCrop(email, file);
    }

    @PostMapping("profile/lazy/tempForCrop")
    public ProfileLazyUserDtoAvatar getProfileAvatarTempForCrop(Authentication authentication,@RequestBody String email) {
        String username = userService.getUserFromJwt(authentication);
        email = username;
        return userService.getTempForCropUrl(email);
    }
}
