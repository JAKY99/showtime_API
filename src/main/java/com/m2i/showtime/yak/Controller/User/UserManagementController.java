package com.m2i.showtime.yak.Controller.User;

import com.m2i.showtime.yak.Dto.Search.PageListResultDto;
import com.m2i.showtime.yak.Dto.Search.SearchParamsDto;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Service.User.UserManagementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@RequestMapping("management/api/v1/user")
public class UserManagementController {
    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PreAuthorize("hasAnyAuthority('user:manage_users')")
    @PostMapping("all")
    public PageListResultDto getAllUsers(@RequestBody SearchParamsDto searchParamsDto) {
        return userManagementService.getAllUsers(searchParamsDto);
    }

    @PreAuthorize("hasAnyAuthority('user:manage_users')")
    @PostMapping
    public void registerNewUser(@RequestBody User user){
        userManagementService.registerNewUser(user);
    }

    @PreAuthorize("hasAnyAuthority('user:manage_users')")
    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") Long userId){
        userManagementService.deleteUser(userId);
    }

    @PreAuthorize("hasAnyAuthority('user:manage_users')")
    @PutMapping("{userId}")
    public void updateUser(@PathVariable("userId") Long userId, @RequestBody User modifiedUser){
        userManagementService.updateUser(userId, modifiedUser);
    }
}
