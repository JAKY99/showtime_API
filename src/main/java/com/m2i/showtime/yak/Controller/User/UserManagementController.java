package com.m2i.showtime.yak.Controller.User;

import com.m2i.showtime.yak.Dto.AddUserAGgridDto;
import com.m2i.showtime.yak.Dto.Search.PageListResultDto;
import com.m2i.showtime.yak.Dto.Search.SearchParamsDto;
import com.m2i.showtime.yak.Dto.UpdateUserDto;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Service.User.UserManagementService;
import org.json.JSONString;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.Objects;

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
    @PostMapping("aggrid/all")
    public Object[] getAllUsersAGgrid() {
        return  userManagementService.getAllUsersAggrid();
    }
    @PreAuthorize("hasAnyAuthority('user:manage_users')")
    @PostMapping(value = "aggrid/edit" , consumes = "application/json")
    public boolean getEditUserAGgrid(@RequestBody UpdateUserDto user)  {
      return  userManagementService.editUserAggrid(user);
    }
    @PreAuthorize("hasAnyAuthority('user:manage_users')")
    @PostMapping("aggrid/delete")
    public boolean getDeleteUsersAGgrid(@RequestBody Object[] user) {
        return true;
//        return  userManagementService.getAllUsersAggrid();
    }
    @PreAuthorize("hasAnyAuthority('user:manage_users')")
    @PostMapping
    public void registerNewUser(@RequestBody User user){
        userManagementService.registerNewUser(user);
    }
    @PreAuthorize("hasAnyAuthority('user:manage_users')")
    @PostMapping("aggrid/add")
    public void registerNewUserAgGrid(@RequestBody AddUserAGgridDto user){
        userManagementService.registerNewUserAgGrid(user);
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
