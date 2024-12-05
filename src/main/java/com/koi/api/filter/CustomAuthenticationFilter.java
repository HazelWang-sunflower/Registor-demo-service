package com.koi.api.filter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (!request.getContentType().equals("application/json")) {
            return null;
        }

        ServletInputStream inputStream = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            inputStream = request.getInputStream();
            Map<String, String> credentials = objectMapper.readValue(inputStream, Map.class);

            String username = credentials.get(USERNAME);
            String password = credentials.get(PASSWORD);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(username, password, null)
            );
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
