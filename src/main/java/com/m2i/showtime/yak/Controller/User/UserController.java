package com.m2i.showtime.yak.Controller.User;

import com.m2i.showtime.yak.Controller.Dto.UserSimpleDto;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Service.User.UserAuthService;
import com.m2i.showtime.yak.Service.User.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public Optional<UserSimpleDto> getUser(@PathVariable("userId") Long userId){
        return userService.getUser(userId);
    }

    @PostMapping
    public void register(@RequestBody User user){
        userAuthService.register(user);
    }

    @PreAuthorize("hasAnyAuthority('user:edit', 'user:manage_users')")
    @PutMapping("{userId}")
    public void updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody User modifiedUser
        ) {
        userService.updateUser(userId, modifiedUser);
    }

    @PreAuthorize("hasAnyAuthority('user:delete', 'user:manage_users')")
    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") Long userId){
        userService.deleteUser(userId);
    }
}
