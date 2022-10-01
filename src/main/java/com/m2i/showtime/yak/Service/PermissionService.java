package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Dto.Search.PageListResultDto;
import com.m2i.showtime.yak.Dto.Search.SearchParamsDto;
import com.m2i.showtime.yak.Repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PageListResultDto getPermissions(SearchParamsDto searchParamsDto) {

        return permissionRepository.getPermissionsList(searchParamsDto.getLimitRow(), searchParamsDto.getFirst(),
                                                       searchParamsDto.getSort(), searchParamsDto.getFilters());
    }
}
