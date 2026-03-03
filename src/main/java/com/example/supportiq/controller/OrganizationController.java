package com.example.supportiq.controller;

import com.example.supportiq.dto.CreateOrganizationRequest;
import com.example.supportiq.entity.Organization;
import com.example.supportiq.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Create organization - Only Admins
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Organization createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
        return organizationService.createOrganization(request.getName());
    }

    /**
     * Get all organizations - Admins only
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Organization> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }
}