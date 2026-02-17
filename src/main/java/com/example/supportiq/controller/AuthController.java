package com.example.supportiq.controller;

import com.example.supportiq.dto.LoginRequest;
import com.example.supportiq.dto.LoginResponse;
import com.example.supportiq.dto.RegisterRequest;
import com.example.supportiq.entity.User;
import com.example.supportiq.service.AuthenticationService;
import com.example.supportiq.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthController(AuthenticationService authenticationService,
                          UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse register(@RequestBody RegisterRequest request) {
        try {
            // Create the user
            User user = userService.createUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole(),
                    request.getOrganizationId()
            );

            // Generate token for the new user
            String token = authenticationService.login(request.getEmail(), request.getPassword());

            return new LoginResponse(
                    token,
                    user.getEmail(),
                    user.getRole().toString(),
                    user.getId(),
                    "User registered successfully"
            );

        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate and get token
            String token = authenticationService.login(request.getEmail(), request.getPassword());

            // Get user details
            User user = authenticationService.getUserByEmail(request.getEmail());

            LoginResponse response = new LoginResponse(
                    token,
                    user.getEmail(),
                    user.getRole().toString(),
                    user.getId(),
                    "Login successful"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, null, null, "Invalid credentials"));
        }
    }
}

