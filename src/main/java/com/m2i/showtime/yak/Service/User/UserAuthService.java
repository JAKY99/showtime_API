package com.m2i.showtime.yak.Service.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2i.showtime.yak.Dto.MessageAdminDto;
import com.m2i.showtime.yak.Dto.RegisterDto;
import com.m2i.showtime.yak.Dto.RegisterGoogleDto;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.RoleRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.Service.KafkaMessageGeneratorService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

import static com.m2i.showtime.yak.Security.Role.AppUserRole.USER;

@Service
public class UserAuthService implements UserDetailsService {

    private final UserRepository userRepository ;
    private final RoleRepository roleRepository;
    private final KafkaMessageGeneratorService kafkaMessageGeneratorService ;
    public UserAuthService(UserRepository userRepository, RoleRepository roleRepository, KafkaMessageGeneratorService kafkaMessageGeneratorService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
    }

    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    public int register(RegisterDto RegisterDto) throws JsonProcessingException {
        Optional<User> userOptional = userRepository.findUserByEmail(RegisterDto.getUsername());
        if(userOptional.isPresent()){
            throw new IllegalStateException("Email is already taken");
        }
        User userToCreate = new User();
        PasswordEncoder passwordEncoder = this.encoder();
        userToCreate.setFirstName(RegisterDto.getFirstname());
        userToCreate.setLastName(RegisterDto.getLastname());
        userToCreate.setUsername(RegisterDto.getUsername());
        userToCreate.setPassword(passwordEncoder.encode(RegisterDto.getPassword()));
        userToCreate = setAuthoritiesForNewUser(userToCreate);

        MessageAdminDto messageAdminDto = new MessageAdminDto("User " + userToCreate.getUsername() + " has been registered","info","basic");
        userRepository.save(userToCreate);
        this.kafkaMessageGeneratorService.generateMessageToAdmin(messageAdminDto);
        return 200;
    }
    public int registerGoogleSignin(RegisterGoogleDto RegisterGoogleDto) throws JsonProcessingException {
        Optional<User> userOptional = userRepository.findUserByEmail(RegisterGoogleDto.getUsername());
        if(userOptional.isPresent()){
            throw new IllegalStateException("Email is already taken");
        }
        User userToCreate = new User();
        PasswordEncoder passwordEncoder = this.encoder();
        userToCreate.setUsername(RegisterGoogleDto.getUsername());
        userToCreate.setPassword(passwordEncoder.encode(RegisterGoogleDto.getPassword()));
        userToCreate.setFirstName(RegisterGoogleDto.getFirstName());
        userToCreate.setLastName(RegisterGoogleDto.getLastName());
        userToCreate = setAuthoritiesForNewUser(userToCreate);

        MessageAdminDto messageAdminDto = new MessageAdminDto("User " + userToCreate.getUsername() + " has been registered","info","basic");
        userRepository.save(userToCreate);
        this.kafkaMessageGeneratorService.generateMessageToAdmin(messageAdminDto);
        return 200;
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
