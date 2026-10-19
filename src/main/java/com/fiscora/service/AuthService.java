package com.fiscora.service;

import com.fiscora.dto.auth.AuthResponse;
import com.fiscora.dto.auth.LoginRequest;
import com.fiscora.dto.auth.RegisterRequest;
import com.fiscora.entity.User;
import com.fiscora.enums.Role;
import com.fiscora.exception.EmailAlreadyExistsException;
import com.fiscora.exception.UsernameAlreadyExistsException;
import com.fiscora.repository.UserRepository;
import com.fiscora.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException on a wrong username or password;
        // GlobalExceptionHandler turns that into a 401.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = (User) authentication.getPrincipal();

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }
}