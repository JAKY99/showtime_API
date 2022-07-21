package com.m2i.showtime.yak.Service.User;

import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class UserManagementService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void registerNewUser(User user) {

    }

    public void deleteUser(Long userId){
        if (!userRepository.existsById(userId)){
            throw new IllegalStateException("User does not exists");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Long userId, User modifiedUser) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(("user with id "+ userId + "does not exists")));

        if (modifiedUser.getFirstName() != null &&
                modifiedUser.getFirstName().length() > 0 &&
                !Objects.equals(user.getFirstName(), modifiedUser.getFirstName())) {
            user.setFirstName(modifiedUser.getFirstName());
        }

        if (modifiedUser.getLastName() != null &&
                modifiedUser.getLastName().length() > 0 &&
                !Objects.equals(user.getLastName(), modifiedUser.getLastName())) {
            user.setLastName(modifiedUser.getLastName());
        }

        if (modifiedUser.getCountry() != null &&
                modifiedUser.getCountry().length() > 0 &&
                !Objects.equals(user.getCountry(), modifiedUser.getCountry())) {
            user.setCountry(modifiedUser.getCountry());
        }

        if (modifiedUser.getUsername() != null &&
                modifiedUser.getUsername().length() > 0 &&
                !Objects.equals(user.getUsername(), modifiedUser.getUsername())) {
            if (userRepository.findUserByEmail(modifiedUser.getUsername()).isPresent()){
                throw new IllegalStateException("email taken");
            }
            user.setUsername(modifiedUser.getUsername());
        }
    }
}
