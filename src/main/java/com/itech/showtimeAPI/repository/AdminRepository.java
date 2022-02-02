package com.itech.showtimeAPI.repository;

import java.util.Optional;

import com.itech.showtimeAPI.admin.Admin;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminRepository extends MongoRepository<Admin, String> {

    Optional<Admin> findAdminByEmail(String email);
}
