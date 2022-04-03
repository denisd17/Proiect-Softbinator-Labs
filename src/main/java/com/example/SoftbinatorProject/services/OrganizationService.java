package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.ModeratorInfoDto;
import com.example.SoftbinatorProject.dtos.OrganizationDto;
import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.models.Organization;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.OrganizationRepository;
import com.example.SoftbinatorProject.repositories.ProjectRepository;
import com.example.SoftbinatorProject.repositories.UserRepository;
import com.example.SoftbinatorProject.utils.AccessUtility;
import org.aspectj.weaver.ast.Or;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final ProjectRepository projectRepository;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository, KeycloakAdminService keycloakAdminService, ProjectRepository projectRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.projectRepository = projectRepository;
    }

    public OrganizationInfoDto createOrganization(OrganizationDto organizationDto, Long uid) {
        Organization newOrganization = Organization.builder()
                .name(organizationDto.getName())
                .description(organizationDto.getDescription())
                .user(userRepository.getById(uid))
                .build();

        //User user = userRepository.getById(uid);
        //if(!user.getRole().equals("admin"))
        //    user.setRole("org_admin");
        //userRepository.save(user);

        organizationRepository.save(newOrganization);

        keycloakAdminService.addRole("ROLE_ORG_ADMIN", uid);
        User user = userRepository.getById(uid);
        System.out.println(user.getOrganizations().size());
        return OrganizationInfoDto.builder()
                .id(newOrganization.getId())
                .name(newOrganization.getName())
                .description(newOrganization.getDescription())
                .organizationOwner(newOrganization.getUser().getUsername())
                .build();
    }

    public OrganizationInfoDto getOrganization(Long id) {
        OrganizationInfoDto organizationInfoDto = organizationRepository.getOrganizationDtoById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist"));

        organizationInfoDto.setProjects(projectRepository.getProjectsDtoByOrgId(id));

        return organizationInfoDto;
    }

    public List<OrganizationInfoDto> getOrganizations() {

        return organizationRepository.getOrganizationDtos();
    }

    public OrganizationInfoDto updateOrganization(OrganizationInfoDto organizationInfoDto, Long id, Long uid, Set<String> roles) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist"));

        if(organization.getUser().getId().equals(uid) && roles.contains("ROLE_ORG_ADMIN") || roles.contains("ROLE_ADMIN")) {
            if(organizationInfoDto.getName() != null)
                organization.setName(organizationInfoDto.getName());
            if(organizationInfoDto.getDescription() != null)
                organization.setDescription(organizationInfoDto.getDescription());

            organizationRepository.save(organization);

            return OrganizationInfoDto.builder()
                    .id(organization.getId())
                    .name(organization.getName())
                    .description(organization.getDescription())
                    .organizationOwner(organization.getUser().getUsername())
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
    }

    public void deleteOrganization(Long id, Long uid, Set<String> roles) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist"));

        // Verificam ca userul care incearca stergerea organizatiei sa fie
        // administratorul organizatiei respective sau administrator
        if(AccessUtility.isAdmin(roles) || AccessUtility.isOrgAdmin(organization, uid)) {

            // Se revoca rolul de moderator fiecarui utilizator ce nu mai e moderator in nici o alta organizatie
            for(User u : organization.getModerators()) {
                if(u.getModeratedOrganizations().size() == 1) {
                    keycloakAdminService.removeRole("ROLE_ORG_MODERATOR", u.getId());
                    //// Daca moderatorul nu era admin pe aplicatie sau admin al altei organizatii, devine user
                    //if(u.getRole().equals("org_moderator")){
                    //    u.setRole("user");
                    //    userRepository.save(u);
                    //}
                }
                //TODO: de revazut many-to-many deletion
                u.getModeratedOrganizations().remove(organization);
            }

            User user = organization.getUser();
            organizationRepository.delete(organization);
            System.out.println(user.getId());
            // In cazul in care nu mai exista alte organizatii administrate de user, administratorului i se revoca rolul de ORG_ADMIN
            if(user.getOrganizations().size() == 0){
                keycloakAdminService.removeRole("ROLE_ORG_ADMIN", user.getId());
                // Daca utilizatorul nu e admin pe aplicatie si nu mai modereaza nici o alta organizatie
                // devine user
                //if(user.getRole().equals("org_admin") && user.getModeratedOrganizations().size() == 0) {
                //    user.setRole("user");
                //    userRepository.save(user);
                //}
                // Daca utilizatorul nu e admin pe aplicatie, dar mai modereaza alte organizatii
                // devine org_moderator
                //else if(user.getRole().equals("org_admin") && user.getModeratedOrganizations().size() > 0) {
                //    user.setRole("org_moderator");
                //    userRepository.save(user);
                //}
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
        }


    }
    public List<ModeratorInfoDto> getModeratorList(Long id, Long uid, Set<String> roles) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist"));

        // Verificam ca userul care incearca adaugarea moderatorului sa fie
        // administratorul organizatiei respective sau administrator
        if(organization.getUser().getId().equals(uid) && roles.contains("ROLE_ORG_ADMIN") || roles.contains("ROLE_ADMIN")) {

            return getModeratorListUtil(organization);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
    }

    //TODO: Fix lista de moderatori corecta la adaugare / stergere
    public List<ModeratorInfoDto> addModerator(Long id, Long moderatorId, Long uid, Set<String> roles) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist"));

        // Verificam ca userul care incearca adaugarea moderatorului sa fie
        // administratorul organizatiei respective sau administrator
        if(AccessUtility.isAdmin(roles) || AccessUtility.isOrgAdmin(organization, uid)) {
            //TODO: check user
            User user = userRepository.getById(moderatorId);

            if(organization.getModerators().contains(user)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a moderator of the organization");
            }
            else {
                user.getModeratedOrganizations().add(organization);
                userRepository.save(user);

                organization.getModerators().add(user);
                organizationRepository.save(organization);

                keycloakAdminService.addRole("ROLE_ORG_MODERATOR", moderatorId);
                //if(user.getRole().equals("user")) {
                //    user.setRole("org_moderator");
                //    userRepository.save(user);
                //}
                return getModeratorListUtil(organization);
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
    }

    public List<ModeratorInfoDto> removeModerator(Long id, Long moderatorId, Long uid, Set<String> roles) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist"));

        // Verificam ca userul care incearca adaugarea moderatorului sa fie
        // administratorul organizatiei respective sau administrator
        if(AccessUtility.isAdmin(roles) || AccessUtility.isOrgAdmin(organization, uid)) {
            //TODO: check user
            User user = userRepository.getById(moderatorId);

            if(!organization.getModerators().contains(user)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a moderator of the organization");
            }
            else {
                user.getModeratedOrganizations().remove(organization);
                userRepository.save(user);

                organization.getModerators().remove(user);
                organizationRepository.save(organization);

                // Daca moderatorul nu mai este moderator al unei alte organizatii, i se revoca rolul de ORG_MODERATOR
                if(user.getModeratedOrganizations().size() == 0) {
                    keycloakAdminService.removeRole("ROLE_ORG_MODERATOR", moderatorId);
                //    if(user.getRole().equals("org_moderator")) {
                //        user.setRole("user");
                //        userRepository.save(user);
                //    }
                }

                return getModeratorListUtil(organization);
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
    }


    private List<ModeratorInfoDto> getModeratorListUtil(Organization organization) {
        List<ModeratorInfoDto> organizationModerators = new ArrayList<>();

        for(User u : organization.getModerators()) {
            organizationModerators.add(ModeratorInfoDto.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .email(u.getEmail())
                    .build());
        }

        return organizationModerators;
    }

}
