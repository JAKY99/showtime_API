package com.itech.showtimeAPI.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {
    
    private final AdminService adminService;

    //GETMAPPING

    @GetMapping()
    public List<Admin> fetchAllAdmins() {

        return adminService.getAllAdmins();
    }

    @GetMapping(value="/getAdminById/{id}")
    public Optional<Admin> getAdminById(@PathVariable String id) {

        return adminService.getAdminById(id);
    }

    @GetMapping(value="/getAdminByEmail/{email}")
    public Optional<Admin> getAdminByEmail(@PathVariable String email) {

        return adminService.getAdminByEmail(email);
    }

    //POSTMAPPING
    
    @PostMapping(value="/insertAdmin")
    public Admin insertAdmin(@RequestBody Admin admin) {

        return adminService.insertAdmin(admin);
    }
    

}
