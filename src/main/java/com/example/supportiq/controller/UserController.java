package com.example.supportiq.controller;

import com.example.supportiq.dto.CreateUserRequest;
import com.example.supportiq.dto.UserResponse;
import com.example.supportiq.entity.User;
import com.example.supportiq.service.UserService;
import org.springframework.http.HttpStatus;
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
     * Create a new user
     * POST /api/users
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
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
     * Get all users (without passwords!)
     * GET /api/users
     */
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(userService::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific user by ID
     * GET /api/users/1
     */
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return userService.toResponse(user);
    }

    /**
     * Get users by organization
     * GET /api/users?organizationId=1
     */
    @GetMapping("/organization/{organizationId}")
    public List<UserResponse> getUsersByOrganization(@PathVariable Long organizationId) {
        return userService.getUsersByOrganization(organizationId)
                .stream()
                .map(userService::toResponse)
                .collect(Collectors.toList());
    }
}

