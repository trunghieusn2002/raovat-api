package com.raovat.api.config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.util.Collections;

@ControllerAdvice
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper mapper = new ObjectMapper();
        String errorJson = mapper.writeValueAsString(Collections.singletonMap("Message", "Unauthorized"));

        response.getWriter().write(errorJson);
    }
}
