package com.example.supportiq.controller;

import com.example.supportiq.dto.CreateUserRequest;
import com.example.supportiq.entity.User;
import com.example.supportiq.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest model) {
        return userService.createUser(
                model.getName(),
                model.getEmail(),
                model.getPassword(),
                model.getRole(),
                model.getOrganizationId()
        );
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}


