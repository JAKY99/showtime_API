package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/role")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("all")
    public List<Role> getRoles() {
        return roleService.getRoles();
    }

    @PostMapping
    public void insertRole(@RequestBody Role role){
        roleService.addRole(role);
    }

    @DeleteMapping(path = "{roleId}")
    public void deleteRole(@PathVariable("roleId") Long roleId){
        roleService.deleteRole(roleId);
    }

    @PostMapping("/aggrid/all")
    public List<RoleManagerDto> getRolesAggrid() {
        return roleService.getRolesAgGrid();
    }
    @PostMapping("/aggrid/edit")
    public ResponseApiAgGridDto editRolesAggrid(@RequestBody EditRoleManagerRequestDto editRoleManagerRequestDto) {
        return roleService.editRolesAggrid(editRoleManagerRequestDto);
    }

    @PostMapping("/aggrid/add")
    public ResponseApiAgGridDto addRolesAggrid(@RequestBody AddRoleManagerRequestDto addRoleManagerRequestDto) {
        return roleService.addRolesAggrid(addRoleManagerRequestDto);
    }


    @PostMapping("/aggrid/delete")
    public ResponseApiAgGridDto deleteRolesAggrid(@RequestBody DeleteRoleManagerRequestDto deleteRoleManagerRequestDto ) {
        return roleService.deleteRolesAggrid(deleteRoleManagerRequestDto);
    }
}
