package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Permission;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.PermissionRepository;
import com.m2i.showtime.yak.Repository.RoleRepository;
import org.json.JSONArray;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository,
                       PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public void addRole(Role role){

        Optional<Role> roleOptional = roleRepository.findByRole(role.getRole());

        if (roleOptional.isPresent()){
            throw new IllegalStateException("role already exists");
        }

        roleRepository.save(role);
    }

    public void deleteRole(Long roleId) {

        if (!roleRepository.existsById(roleId)){
            throw new IllegalStateException("Role does not exists");
        }
        roleRepository.deleteById(roleId);
    }

    public List<RoleManagerDto> getRolesAgGrid() {
        List<RoleManagerDto> roleManagerDtoList = new ArrayList<>();
        List<Role> result = roleRepository.findAll();
        for (Role role : result) {
            RoleManagerDto roleManagerDto = new RoleManagerDto();
            roleManagerDto.setId(role.getId());
            roleManagerDto.setRole(role.getRole());
            roleManagerDto.setDisplay_name(role.getDisplay_name());
            roleManagerDto.setDescription(role.getDescription());
            Set<Permission> permissionList = role.getPermissions();
            JSONArray jsonArray = new JSONArray();
            for (Permission permission : permissionList) {
                jsonArray.put(permission.getPermission());
            }
            roleManagerDto.setPermissions(jsonArray.toString());
            roleManagerDtoList.add(roleManagerDto);
        }
        return roleManagerDtoList;
    }

    public ResponseApiAgGridDto editRolesAggrid(EditRoleManagerRequestDto editRoleManagerRequestDto) {
        ResponseApiAgGridDto response = new ResponseApiAgGridDto();
        Role roleToEdit = roleRepository.findById(editRoleManagerRequestDto.getId()).orElseThrow(() -> new IllegalStateException("Role does not exists"));
        String permissionList = editRoleManagerRequestDto.getPermissions();
        JSONArray jsonArray = new JSONArray(permissionList);
        Role newRole = new Role();
        newRole.setId(roleToEdit.getId());
        newRole.setRole(editRoleManagerRequestDto.getRole());
        newRole.setDescription(editRoleManagerRequestDto.getDescription());
        newRole.setDisplay_name(editRoleManagerRequestDto.getDisplay_name());

        Set<Permission> permissionToUpdate = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            String permissionName = jsonArray.getString(i);
            Permission permission = permissionRepository.findByPermission(permissionName).orElseThrow(() -> new IllegalStateException("Permission does not exists"));
            permissionToUpdate.add(permission);
        }
        newRole.setPermissions(permissionToUpdate);
        try{
            new ModelMapper().map(newRole,roleToEdit);
            roleRepository.save(roleToEdit);
            response.setSeverity("success");
            response.setTitle("Success");
            response.setDetails("Role modified successfully");
            response.setSticky(false);
            return response;
        }catch (Exception e){
            response.setSeverity("error");
            response.setTitle("Error");
            response.setDetails("An error occurred while modifying the role");
            response.setSticky(false);
            return response;
        }
    }

    public ResponseApiAgGridDto addRolesAggrid(AddRoleManagerRequestDto addRoleManagerRequestDto) {
        ResponseApiAgGridDto response = new ResponseApiAgGridDto();
        String permissionList = addRoleManagerRequestDto.getPermissions();
        JSONArray jsonArray = new JSONArray(permissionList);
        Role newRole = new Role();
        newRole.setRole(addRoleManagerRequestDto.getRole());
        newRole.setDescription(addRoleManagerRequestDto.getDescription());
        newRole.setDisplay_name(addRoleManagerRequestDto.getDisplay_name());

        Set<Permission> permissionToUpdate = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            String permissionName = jsonArray.getString(i);
            Permission permission = permissionRepository.findByPermission(permissionName).orElseThrow(() -> new IllegalStateException("Permission does not exists"));
            permissionToUpdate.add(permission);
        }
        newRole.setPermissions(permissionToUpdate);
        try{
            roleRepository.save(newRole);
            response.setSeverity("success");
            response.setTitle("Success");
            response.setDetails("Role added successfully");
            response.setSticky(false);
            return response;
        }catch (Exception e){
            response.setSeverity("error");
            response.setTitle("Error");
            response.setDetails("An error occurred while adding the role");
            response.setSticky(false);
            return response;
        }
    }

    public ResponseApiAgGridDto deleteRolesAggrid(DeleteRoleManagerRequestDto deleteRoleManagerRequestDto) {
        ResponseApiAgGridDto response = new ResponseApiAgGridDto();
        try{
            Role roleToDelete = roleRepository.findById(deleteRoleManagerRequestDto.getId()).orElseThrow(() -> new IllegalStateException("Role does not exists"));

            roleRepository.delete(roleToDelete);
            response.setSeverity("success");
            response.setTitle("Success");
            response.setDetails("Role deleted successfully");
            response.setSticky(false);
            return response;
        }catch (Exception e){
            response.setSeverity("error");
            response.setTitle("Error");
            response.setDetails("An error occurred while deleting the role");
            response.setSticky(false);
            return response;
        }
    }
}
