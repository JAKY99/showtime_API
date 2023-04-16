package com.m2i.showtime.yak.Controller.User;
import com.google.api.client.json.JsonFactory;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Jwt.JwtConfig;
import com.m2i.showtime.yak.Service.User.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "api/v1")
public class GoogleSigninController {

    @Value(value = "${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final UserService userService;


    public GoogleSigninController(JwtConfig jwtConfig, SecretKey secretKey , UserService userService) {
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
        this.userService = userService;
    }

    @PostMapping("/login/google")
    public  GoogleSigninReponseDto googleSignin(@RequestBody GoogleSigninDto GoogleSigninDto, HttpServletResponse response) throws GeneralSecurityException, IOException {
        HttpTransport transport = new NetHttpTransport();

        JsonFactory jsonFactory = new GsonFactory();
        GoogleSigninReponseDto googleSigninReponseDto = new GoogleSigninReponseDto();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();
        GoogleIdToken idToken = verifier.verify(GoogleSigninDto.getToken());
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);
            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");
            Optional<User> optionalUser = this.userService.findOneUserByEmailOrCreateIt(email);
            String token = Jwts.builder()
                    .setSubject(email)
                    .claim("authorities", optionalUser.get().getGrantedAuthorities())
                    .setIssuedAt(new Date())
                    .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                    .signWith(secretKey)
                    .compact();

            response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);



            return googleSigninReponseDto;
            // Use or store profile information
            // ...

        } else {
            googleSigninReponseDto.setEmail("ERROR");
            return googleSigninReponseDto;
        }
    }
}
