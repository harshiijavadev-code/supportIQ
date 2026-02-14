package com.example.supportiq.controller;

import com.example.supportiq.entity.Organization;

import com.example.supportiq.dto.CreateOrganizationRequest;
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

    @PostMapping
    public Organization createOrganization(@RequestBody CreateOrganizationRequest request) {
        return organizationService.createOrganization(request.getName());
    }

    @GetMapping
    public List<Organization> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }
}