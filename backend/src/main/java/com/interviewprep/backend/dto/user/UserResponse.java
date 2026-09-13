package com.interviewprep.backend.dto.user;

import com.interviewprep.backend.entity.User;

public record UserResponse(Long id, String email, String fullName, String role) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
