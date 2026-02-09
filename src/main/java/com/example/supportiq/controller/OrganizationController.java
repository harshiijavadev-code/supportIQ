package com.example.supportiq.controller;

import com.example.supportiq.entity.Organization;
import com.example.supportiq.service.OrganizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    // ✅ PROPER way (POST)
    @PostMapping
    public Organization createOrganization(@RequestBody Organization organization) {
        return organizationService.createOrganization(organization.getName());
    }

    // TEMPORARY: GET create (can remove later)
    @GetMapping("/create")
    public Organization createOrganizationViaGet(@RequestParam String name) {
        return organizationService.createOrganization(name);
    }

    @GetMapping
    public List<Organization> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }
}

