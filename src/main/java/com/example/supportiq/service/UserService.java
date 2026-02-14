package com.example.supportiq.service;

import com.example.supportiq.dto.UserResponse;
import com.example.supportiq.entity.Organization;
import com.example.supportiq.entity.User;
import com.example.supportiq.entity.enums.UserRole;
import com.example.supportiq.repository.OrganizationRepository;
import com.example.supportiq.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       OrganizationRepository organizationRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String name,
                           String email,
                           String password,
                           UserRole role,
                           Long organizationId) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setOrganization(organization);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public List<User> getUsersByOrganization(Long organizationId) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getOrganization().getId().equals(organizationId))
                .toList();
    }

    public UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setOrganizationId(user.getOrganization().getId());
        return dto;
    }
}