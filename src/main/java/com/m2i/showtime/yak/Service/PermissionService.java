package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Dto.Search.PageListResultDto;
import com.m2i.showtime.yak.Dto.Search.SearchParamsDto;
import com.m2i.showtime.yak.Entity.Permission;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
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
        Optional<Permission> permissionOptional = permissionRepository.findByPermission(permission.getPermission());

        if (permissionOptional.isPresent()) {
            throw new IllegalStateException("Permission already exists");
        }

        return permissionRepository.save(permission)
                                   .getId();
    }

    public Permission getPermission(Long id) {
        Optional<Permission> permission = permissionRepository.findById(id);

        return permission.orElseThrow(() -> {
            throw new IllegalStateException("Permission not found");
        });

    }

    @Transactional
    public boolean editPermission(Permission modifiedPermission) {
        boolean isModified = false;
        Permission permission = permissionRepository.findById(modifiedPermission.getId())
                                                    .orElseThrow(() -> new IllegalStateException(
                                                            ("user with id " + modifiedPermission.getId() + "does not exists")));

        if (modifiedPermission.getPermission() != null && modifiedPermission.getPermission()
                                                                            .length() > 0 && !Objects.equals(
                permission.getPermission(), modifiedPermission.getPermission())) {
            permission.setPermission(modifiedPermission.getPermission());
            isModified = true;
        }

        if (modifiedPermission.getDisplayName() != null && modifiedPermission.getDisplayName()
                                                                             .length() > 0 && !Objects.equals(
                permission.getDisplayName(), modifiedPermission.getDisplayName())) {
            permission.setDisplayName(modifiedPermission.getDisplayName());
            isModified = true;
        }

        if (!Objects.equals(permission.getDescription(), modifiedPermission.getDescription())) {
            permission.setDescription(modifiedPermission.getDescription());
            isModified = true;
        }

        return isModified;
    }

    public void deletePermission(Long id) {
        Optional<Permission> permissionOptional = permissionRepository.findById(id);

        if (!permissionOptional.isPresent()) {
            throw new IllegalStateException("Permission doesn't exists");
        }

        permissionRepository.delete(permissionOptional.get());
    }

    public List<Permission> getPermissionsAggrid() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions;
    }
}
