package com.itech.showtimeAPI.admin;


import com.itech.showtimeAPI.repository.AdminRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public List<Admin> getAllAdmins() { return adminRepository.findAll(); }
}
