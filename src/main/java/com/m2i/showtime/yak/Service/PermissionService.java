package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Dto.Search.PageListResultDto;
import com.m2i.showtime.yak.Dto.Search.SearchParamsDto;
import com.m2i.showtime.yak.Entity.Permission;
import com.m2i.showtime.yak.Repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PageListResultDto getPermissions(SearchParamsDto searchParamsDto) {

        Page<Permission> page;
        Sort.Direction sortDirection;

        if (searchParamsDto.getSort()
                           .getSortField() == null) {

            page = permissionRepository.findAll(
                    PageRequest.of(searchParamsDto.getPageNumber(), searchParamsDto.getLimitRow(),
                                   Sort.by(Sort.Direction.ASC, "displayName")));
        } else {
            if (searchParamsDto.getSort()
                               .getSortOrder() == 1) {
                sortDirection = Sort.Direction.ASC;
            } else {
                sortDirection = Sort.Direction.DESC;
            }
            page = permissionRepository.findAll(
                    PageRequest.of(searchParamsDto.getPageNumber(), searchParamsDto.getLimitRow(),
                                   Sort.by(sortDirection, searchParamsDto.getSort()
                                                                         .getSortField())));
        }

        if (page.isEmpty()) {
            throw new IllegalStateException("page was not found");
        }

        return new PageListResultDto(page.toList(), page.getTotalElements());
    }

    public long addPermission(Permission permission) {
        try {

            return permissionRepository.save(permission)
                                       .getId();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Permission already exists.");
        }

    }

    ;
}
