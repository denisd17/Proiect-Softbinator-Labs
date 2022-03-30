package com.example.SoftbinatorProject.controllers;

import com.example.SoftbinatorProject.dtos.OrganizationDto;
import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.services.OrganizationService;
import com.example.SoftbinatorProject.utils.KeycloakHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.SoftbinatorProject.utils.HttpStatusUtility.successResponse;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService){
        this.organizationService = organizationService;
    }

    @PostMapping("/create-organization")
    public ResponseEntity<?> createOrganization(@RequestBody OrganizationDto organizationDto, Authentication authentication) {
        return new ResponseEntity<>(organizationService.createOrganization(organizationDto, Long.parseLong(KeycloakHelper.getUser(authentication))), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrganization(@PathVariable Long id) {
        return new ResponseEntity<>(organizationService.getOrganization(id), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<OrganizationInfoDto>> getOrganizations() {
        return new ResponseEntity<>(organizationService.getOrganizations(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrganization(@PathVariable Long id, Authentication authentication) {
        organizationService.deleteOrganization(id, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication));
        return successResponse();
    }

    @PutMapping("/{id}/addModerator/{moderatorId}")
    public ResponseEntity<?> addModerator(@PathVariable Long id, @PathVariable Long moderatorId, Authentication authentication) {
        return new ResponseEntity<>(organizationService.addModerator(id, moderatorId, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication)), HttpStatus.OK);
    }

    @PutMapping("/{id}/removeModerator/{moderatorId}")
    public ResponseEntity<?> removeModerator(@PathVariable Long id, @PathVariable Long moderatorId, Authentication authentication) {
        return new ResponseEntity<>(organizationService.removeModerator(id, moderatorId, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication)), HttpStatus.OK);
    }

    @GetMapping("/{id}/moderators")
    public ResponseEntity<?> getModeratorList(@PathVariable Long id, Authentication authentication) {
        return new ResponseEntity<>(organizationService.getModeratorList(id, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication)), HttpStatus.OK);
    }
}
