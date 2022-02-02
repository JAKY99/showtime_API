package com.itech.showtimeAPI.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    //GETMAPPING

    @GetMapping
    public List<User> fetchAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value="/getUserById/{id}")
    public Optional<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }
    
    //POSTMAPPING

    @PostMapping(value="/insertUser")
    public User insertUser(@RequestBody User user) {
        userService.insertUser(user);
        return user;
    }
    


}
