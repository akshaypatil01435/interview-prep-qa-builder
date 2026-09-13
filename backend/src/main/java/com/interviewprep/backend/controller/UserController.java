package com.interviewprep.backend.controller;

import com.interviewprep.backend.dto.user.UpdateProfileRequest;
import com.interviewprep.backend.dto.user.UserResponse;
import com.interviewprep.backend.entity.User;
import com.interviewprep.backend.exception.ResourceNotFoundException;
import com.interviewprep.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        return UserResponse.from(getUser(authentication));
    }

    @PutMapping("/me")
    public UserResponse updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        User user = getUser(authentication);
        user.setFullName(request.fullName().trim());

        return UserResponse.from(userRepository.save(user));
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
