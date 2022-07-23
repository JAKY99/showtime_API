package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
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
}
