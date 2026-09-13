package com.interviewprep.backend.security;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank(message = "JWT_SECRET must be configured")
        @Size(min = 32, message = "JWT_SECRET must be at least 32 characters")
        String secret,
        @Min(value = 60, message = "JWT expiration must be at least 60 seconds")
        long expirationSeconds
) {
}
