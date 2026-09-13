package com.interviewprep.backend.service;

import com.interviewprep.backend.dto.auth.AuthResponse;
import com.interviewprep.backend.dto.auth.LoginRequest;
import com.interviewprep.backend.dto.auth.RegisterRequest;
import com.interviewprep.backend.entity.Role;
import com.interviewprep.backend.entity.User;
import com.interviewprep.backend.exception.ConflictException;
import com.interviewprep.backend.exception.InvalidCredentialsException;
import com.interviewprep.backend.repository.UserRepository;
import com.interviewprep.backend.security.JwtProperties;
import com.interviewprep.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private final JwtProperties jwtProperties = new JwtProperties(
            "test-jwt-secret-that-is-long-enough-for-hs256-signing", 3600);

    @Test
    void registerNormalizesEmailHashesPasswordAndAssignsUserRole() {
        AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil, jwtProperties);
        when(userRepository.findByEmail("candidate@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secure-password")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(7L);
            return user;
        });

        var response = authService.register(new RegisterRequest("  Candidate Name  ", " Candidate@Example.com ", "secure-password"));

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("candidate@example.com", savedUser.getEmail());
        assertEquals("Candidate Name", savedUser.getFullName());
        assertEquals("bcrypt-hash", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
        assertEquals("candidate@example.com", response.email());
        assertEquals("USER", response.role());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil, jwtProperties);
        when(userRepository.findByEmail("candidate@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(ConflictException.class, () ->
                authService.register(new RegisterRequest("Candidate", "candidate@example.com", "secure-password")));
    }

    @Test
    void loginReturnsBearerTokenAndSafeUserResponse() {
        AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil, jwtProperties);
        User user = new User();
        user.setId(9L);
        user.setEmail("candidate@example.com");
        user.setFullName("Candidate");
        user.setPassword("bcrypt-hash");
        user.setRole(Role.ADMIN);
        when(userRepository.findByEmail("candidate@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secure-password", "bcrypt-hash")).thenReturn(true);
        when(jwtUtil.generateToken("candidate@example.com")).thenReturn("signed-token");

        AuthResponse response = authService.login(new LoginRequest("CANDIDATE@example.com", "secure-password"));

        assertEquals("signed-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600, response.expiresIn());
        assertEquals("ADMIN", response.user().role());
        assertTrue(response.user().email().contains("@"));
    }

    @Test
    void loginDoesNotRevealWhetherTheEmailExists() {
        AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil, jwtProperties);
        when(userRepository.findByEmail(eq("missing@example.com"))).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () ->
                authService.login(new LoginRequest("missing@example.com", "secure-password")));
    }
}
