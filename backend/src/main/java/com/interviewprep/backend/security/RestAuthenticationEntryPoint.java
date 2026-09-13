package com.interviewprep.backend.security;

import com.interviewprep.backend.dto.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import tools.jackson.databind.json.JsonMapper;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final JsonMapper jsonMapper;

    public RestAuthenticationEntryPoint(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        jsonMapper.writeValue(response.getOutputStream(), new ApiError(
                Instant.now(), HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
                "Authentication is required or the access token is invalid", request.getRequestURI(), Map.of()));
    }
}
