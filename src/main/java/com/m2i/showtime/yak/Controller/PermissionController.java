package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.Search.PageListResultDto;
import com.m2i.showtime.yak.Dto.Search.SearchParamsDto;
import com.m2i.showtime.yak.Entity.Permission;
import com.m2i.showtime.yak.Service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@RequestMapping("api/v1/permission")
public class PermissionController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PreAuthorize("hasAnyAuthority('user:manage_permission')")
    @PostMapping
    public PageListResultDto getPermissions(@RequestBody SearchParamsDto searchParamsDto) {
        return permissionService.getPermissions(searchParamsDto);
    }

    @PreAuthorize("hasAnyAuthority('user:manage_permission')")
    @PostMapping("/add")
    public long addPermission(@RequestBody Permission permission) {
        return permissionService.addPermission(permission);
    }
}
