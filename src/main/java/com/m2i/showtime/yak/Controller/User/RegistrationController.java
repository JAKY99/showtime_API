package com.m2i.showtime.yak.Controller.User;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Service.User.UserAuthService;
import com.m2i.showtime.yak.Service.User.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;

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
}
