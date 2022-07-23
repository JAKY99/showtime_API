package com.m2i.showtime.yak.Security.Role;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.m2i.showtime.yak.Security.Role.AppUserPermission.*;

public enum AppUserRole {
    USER(
            Sets.newHashSet(
                    USER_READ,
                    USER_EDIT,
                    USER_DELETE,
                    MOVIE_READ
            )
    ),
    ADMIN(
            Sets.newHashSet(
                    USER_MANAGE_USERS,
                    USER_MANAGE_RANK,
                    USER_MANAGE_PERMISSION,
                    USER_MANAGE_TROPHY,
                    USER_MANAGE_WATCHED,
                    MOVIE_MANAGE
            )
    );

    private final Set<AppUserPermission> permissions;

    AppUserRole(Set<AppUserPermission> permissions) {
        this.permissions = permissions;
    }
    private Set<AppUserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }

}
