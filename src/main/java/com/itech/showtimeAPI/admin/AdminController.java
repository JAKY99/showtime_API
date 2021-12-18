package com.itech.showtimeAPI.admin;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public List<Admin>  fetchAllAdmins() { return adminService.getAllAdmins(); }
}
