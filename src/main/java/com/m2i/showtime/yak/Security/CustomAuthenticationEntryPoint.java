package com.m2i.showtime.yak.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2i.showtime.yak.Service.User.LoginAttemptService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    @Autowired
    private LoginAttemptService loginAttemptService;
    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Create a custom response object
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("status", "403");
        errorResponse.put("message", "Unauthorized");
        if (loginAttemptService.isBlocked()) {
            response.setStatus(429);
            errorResponse.put("status", "429");
            errorResponse.put("message", "too many failed login attempts. Please try again later.");
        }
        // Set the response content type and write the JSON string to the response
        response.setContentType("application/json");
        response.getWriter().write(errorResponse.toString());
    }
}
