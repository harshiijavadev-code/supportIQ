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

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // ✅ hashing here only
        user.setRole(role);
        user.setOrganization(organization);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ STEP 2: Entity → DTO mapping
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
