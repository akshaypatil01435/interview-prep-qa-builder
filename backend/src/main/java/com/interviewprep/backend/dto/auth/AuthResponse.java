package com.interviewprep.backend.dto.auth;

import com.interviewprep.backend.dto.user.UserResponse;

public record AuthResponse(String token, String tokenType, long expiresIn, UserResponse user) {
}
