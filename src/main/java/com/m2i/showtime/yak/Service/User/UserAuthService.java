package com.m2i.showtime.yak.Service.User;

import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Dto.RegisterDto;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.RoleRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Optional;

import static com.m2i.showtime.yak.Security.Role.AppUserRole.USER;

@Service
public class UserAuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public UserAuthService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    public int register(RegisterDto RegisterDto) {
        Optional<User> userOptional = userRepository.findUserByEmail(RegisterDto.getUsername());
        if(userOptional.isPresent()){
            throw new IllegalStateException("Email is already taken");
        }
        User userToCreate = new User();
        PasswordEncoder passwordEncoder = this.encoder();
        userToCreate.setUsername(RegisterDto.getUsername());
        userToCreate.setPassword(passwordEncoder.encode(RegisterDto.getPassword()));
        userToCreate = setAuthoritiesForNewUser(userToCreate);

        KafkaMessageDto kafkaMessageDto = new KafkaMessageDto("User " + userToCreate.getUsername() + " has been registered","admin");
        userRepository.save(userToCreate);
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(kafkaMessageDto.getTopicName(), kafkaMessageDto.getMessage());
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Sent message=[" + kafkaMessageDto.getMessage() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=["
                        + kafkaMessageDto.getMessage() + "] due to : " + ex.getMessage());
            }
        });
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
