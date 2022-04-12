package com.itech.showtimeAPI.admin;

import java.util.List;
import java.util.Optional;

import com.itech.showtimeAPI.repository.AdminRepository;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AdminService {
    
    private final AdminRepository adminRepository;

    public List<Admin> getAllAdmins() {

        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminById(String id) {

        return adminRepository.findById(id);
    }

    public Optional<Admin> getAdminByEmail(String email) {

        return adminRepository.findAdminByEmail(email);
    }

    public Admin insertAdmin(Admin admin) {
        return adminRepository.insert(admin);
    }

}
