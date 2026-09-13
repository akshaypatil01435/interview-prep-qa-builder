package com.interviewprep.backend.service;

import com.interviewprep.backend.dto.auth.AuthResponse;
import com.interviewprep.backend.dto.auth.LoginRequest;
import com.interviewprep.backend.dto.auth.RegisterRequest;
import com.interviewprep.backend.dto.user.UserResponse;
import com.interviewprep.backend.entity.Role;
import com.interviewprep.backend.entity.User;
import com.interviewprep.backend.exception.ConflictException;
import com.interviewprep.backend.exception.InvalidCredentialsException;
import com.interviewprep.backend.repository.UserRepository;
import com.interviewprep.backend.security.JwtProperties;
import com.interviewprep.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("An account with this email already exists");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()), "Bearer", jwtProperties.expirationSeconds(), UserResponse.from(user));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
