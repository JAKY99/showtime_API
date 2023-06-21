package com.m2i.showtime.yak;
import com.m2i.showtime.yak.Dto.RegisterDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
           Optional<User> userTest =userRepository
                        .findUserByEmail("johndoe@random.com");
            if (userTest.isPresent()) {
                userRepository.delete(userTest.get());
            }
    }
    @Test
    public void testRegisterAndLogin() throws IOException, InterruptedException, URISyntaxException, JSONException {
        // Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Prepare the request body using RegisterDto
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstname("John");
        registerDto.setLastname("Doe");
        registerDto.setUsername("johndoe@random.com");
        registerDto.setPassword("password123");

        // Serialize RegisterDto to JSON
        String requestBody = new ObjectMapper().writeValueAsString(registerDto);
        String apiUrl = "http://localhost:8082/api/v1/registration/user";
        URI encodedUrl = new URI(apiUrl);

        // Prepare the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(encodedUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            // Send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the response status code
            int statusCode = response.statusCode();
            System.out.println("Response Status Code registering: " + statusCode);
            assertEquals(200, statusCode);
        }catch (Exception e){
            System.out.println(e);
        }

        JSONObject data = new JSONObject();
        data.put("username",registerDto.getUsername());
        data.put("password",registerDto.getPassword());
        requestBody = data.toString();
        // Serialize RegisterDto to JSON
        apiUrl = "http://localhost:8082/login";
        encodedUrl = new URI(apiUrl);
        // Prepare the request
        request = HttpRequest.newBuilder()
                .uri(encodedUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            // Send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Check the response status code
            int statusCode = response.statusCode();
            System.out.println("Response Status Code login : " + statusCode);
            assertEquals(200, statusCode);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    // ...
}


