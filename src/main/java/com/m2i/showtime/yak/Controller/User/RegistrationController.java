package com.m2i.showtime.yak.Controller.User;

import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Service.User.UserAuthService;
import com.m2i.showtime.yak.Service.User.UserService;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping(path = "api/v1/registration")
public class RegistrationController {


    private final UserService userService;
    private final UserAuthService userAuthService;


    public RegistrationController(UserService userService, UserAuthService userAuthService) {
        this.userService = userService;
        this.userAuthService = userAuthService;
    }

    @PostMapping("/user")
    public int register(@RequestBody RegisterDto RegisterDto) {

        return userAuthService.register(RegisterDto);
    }
    @PostMapping("/reset")
    public int resetPasswordMailing(@RequestBody ResetPasswordMailingDto ResetPasswordMailing) throws MessagingException {

        return userService.sendEmailReset(ResetPasswordMailing);
    }

    @GetMapping("/checkreset/{token}")
    public boolean resetPasswordMailing(@PathVariable("token") String token) throws MessagingException {

        return userService.checkToken(token);
    }
    @PostMapping("/reset/password")
    public int resetPassword(@RequestBody ResetPasswordUseDto resetPasswordUseDto) throws MessagingException {
        return userService.changeUserPassword(resetPasswordUseDto);
    }
}
