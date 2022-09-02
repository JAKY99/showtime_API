package com.m2i.showtime.yak.Service.User;

import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.RoleRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.m2i.showtime.yak.Security.Role.AppUserRole.ADMIN;
import static com.m2i.showtime.yak.Security.Role.AppUserRole.USER;

@Service
public class UserAuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserAuthService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public void register(User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getUsername());
        if(userOptional.isPresent()){
            throw new IllegalStateException("Email is already taken");
        }

        user = setAuthoritiesForNewUser(user);

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email){
        User user = userRepository
                .findUserByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("email %s not found", email))
                );

        user = setAuthoritiesForUser(user);

        return user;
    }
    private User setAuthoritiesForNewUser(User user) {
        Optional<Role> ROLE_USER = roleRepository.findByRole(USER.name());
        user.setRole(ROLE_USER.get());

        user.setGrantedAuthorities(user.getGrantedAuthorities());

        return user;
    }

    private User setAuthoritiesForUser(User user) {
        user.setGrantedAuthorities(user.getGrantedAuthorities());
        return user;
    }

}
