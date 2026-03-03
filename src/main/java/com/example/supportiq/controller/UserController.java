package com.example.supportiq.controller;

import com.example.supportiq.dto.CreateUserRequest;
import com.example.supportiq.dto.UserResponse;
import com.example.supportiq.entity.User;
import com.example.supportiq.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create user - Only Admins
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole(),
                request.getOrganizationId()
        );
        return userService.toResponse(user);
    }

    /**
     * Get all users - Agents and Admins only
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(userService::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID - All authenticated users
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return userService.toResponse(user);
    }

    /**
     * Get users by organization - Agents and Admins only
     */
    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public List<UserResponse> getUsersByOrganization(@PathVariable Long organizationId) {
        return userService.getUsersByOrganization(organizationId)
                .stream()
                .map(userService::toResponse)
                .collect(Collectors.toList());
    }
}