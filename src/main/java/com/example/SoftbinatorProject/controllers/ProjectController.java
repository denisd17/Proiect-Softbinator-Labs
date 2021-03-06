package com.example.SoftbinatorProject.controllers;

import com.example.SoftbinatorProject.dtos.*;
import com.example.SoftbinatorProject.services.DonationService;
import com.example.SoftbinatorProject.services.ProjectService;
import com.example.SoftbinatorProject.services.TicketService;
import com.example.SoftbinatorProject.utils.KeycloakHelper;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

import static com.example.SoftbinatorProject.utils.HttpStatusUtility.successResponse;

@RestController
@RequestMapping("/organizations/{id}/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final DonationService donationService;
    private final TicketService ticketService;

    @Autowired
    public ProjectController(ProjectService projectService, DonationService donationService, TicketService ticketService) {
        this.projectService = projectService;
        this.donationService = donationService;
        this.ticketService = ticketService;
    }

    @PostMapping("")
    public ResponseEntity<?> createProject(@PathVariable Long id, @RequestBody CreateProjectDto createProjectDto, Authentication authentication) {
        return new ResponseEntity<>(projectService.createProject(createProjectDto, id, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication)), HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProject(@PathVariable Long id, @PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getProject(id, projectId), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<ProjectInfoDto>> getProjects(@PathVariable Long id) {
        return new ResponseEntity<>(projectService.getProjects(id), HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id, @PathVariable Long projectId, Authentication authentication) {
        projectService.deleteProject(id, projectId, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication));
        return successResponse();
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @PathVariable Long projectId, @RequestBody UpdateProjectDto updateProjectDto, Authentication authentication) {
        return new ResponseEntity<>(projectService.updateProject(updateProjectDto, id, projectId, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication)), HttpStatus.OK);
    }

    @PostMapping("/{projectId}/donate")
    public ResponseEntity<?> donateToFundariser(@PathVariable Long id, @PathVariable Long projectId, @RequestBody DonationDto donationDto, Authentication authentication) throws FileNotFoundException, DocumentException {
        return new ResponseEntity<>(donationService.donate(id, projectId, Long.parseLong(KeycloakHelper.getUser(authentication)), donationDto), HttpStatus.OK);
    }

    @PostMapping("/{projectId}/purchase")
    public ResponseEntity<?> purchaseEventTickets(@PathVariable Long id, @PathVariable Long projectId, @RequestBody TicketDto ticketDto, Authentication authentication) throws FileNotFoundException, DocumentException {
        return new ResponseEntity<>(ticketService.purchase(id, projectId, Long.parseLong(KeycloakHelper.getUser(authentication)), ticketDto), HttpStatus.OK);
    }
}
