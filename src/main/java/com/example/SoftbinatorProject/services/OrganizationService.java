package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.ModeratorInfoDto;
import com.example.SoftbinatorProject.dtos.OrganizationDto;
import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.models.Organization;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.OrganizationRepository;
import com.example.SoftbinatorProject.repositories.UserRepository;
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

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository, KeycloakAdminService keycloakAdminService) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    public OrganizationInfoDto createOrganization(OrganizationDto organizationDto, Long uid) {
        Organization newOrganization = Organization.builder()
                .name(organizationDto.getName())
                .description(organizationDto.getDescription())
                .user(userRepository.getById(uid))
                .build();

        organizationRepository.save(newOrganization);

        keycloakAdminService.addRole("ROLE_ORG_ADMIN", uid);

        return OrganizationInfoDto.builder()
                .name(newOrganization.getName())
                .description(newOrganization.getDescription())
                .organizationOwner(newOrganization.getUser().getUsername())
                .build();
    }

    /*public OrganizationInfoDto getOrganization(Long id) {

    }

    public List<OrganizationInfoDto> getOrganizations() {

    }

    public OrganizationInfoDto updateOrganization(OrganizationInfoDto organizationInfoDto, Long id, Long uid) {

    }
*/
    public void deleteOrganization(Long id, Long uid, Set<String> roles) {
        //TODO: Organization does not exist check
        Organization organization = organizationRepository.getById(id);

        // Verificam ca userul care incearca stergerea organizatiei sa fie
        // administratorul organizatiei respective sau administrator
        if(organization.getUser().getId().equals(uid) && roles.contains("ROLE_ORG_ADMIN") || roles.contains("ROLE_ADMIN")) {

            // Se revoca rolul de moderator fiecarui utilizator ce nu mai e moderator in nici o alta organizatie
            for(User u : organization.getModerators()) {
                if(u.getModeratedOrganizations().size() == 1) {
                    keycloakAdminService.removeRole("ROLE_ORG_MODERATOR", u.getId());
                }
                //TODO: de revazut many-to-many deletion
                u.getModeratedOrganizations().remove(organization);
            }

            organizationRepository.delete(organization);
            User user = userRepository.getById(uid);

            // In cazul in care nu mai exista alte organizatii administrate de user, administratorului i se revoca rolul de ORG_ADMIN
            if(user.getOrganizations().size() == 0){
                keycloakAdminService.removeRole("ROLE_ORG_ADMIN", uid);
            }
        }

    }
    public List<ModeratorInfoDto> getModeratorList(Long id, Long uid, Set<String> roles) {
        //TODO: Organization does not exist check
        Organization organization = organizationRepository.getById(id);

        // Verificam ca userul care incearca adaugarea moderatorului sa fie
        // administratorul organizatiei respective sau administrator
        if(organization.getUser().getId().equals(uid) && roles.contains("ROLE_ORG_ADMIN") || roles.contains("ROLE_ADMIN")) {

            return getModeratorListUtil(organization);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
    }


    public List<ModeratorInfoDto> addModerator(Long id, Long moderatorId, Long uid, Set<String> roles) {
        //TODO: Organization does not exist check
        Organization organization = organizationRepository.getById(id);

        // Verificam ca userul care incearca adaugarea moderatorului sa fie
        // administratorul organizatiei respective sau administrator
        if(organization.getUser().getId().equals(uid) && roles.contains("ROLE_ORG_ADMIN") || roles.contains("ROLE_ADMIN")) {
            //TODO: check user
            User user = userRepository.getById(moderatorId);

            if(organization.getModerators().contains(user)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a moderator of the organization");
            }
            else {
                user.getModeratedOrganizations().add(organization);
                userRepository.save(user);
                keycloakAdminService.addRole("ROLE_ORG_MODERATOR", moderatorId);
                return getModeratorListUtil(organization);
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
    }

    public List<ModeratorInfoDto> removeModerator(Long id, Long moderatorId, Long uid, Set<String> roles) {
        //TODO: Organization does not exist check
        Organization organization = organizationRepository.getById(id);

        // Verificam ca userul care incearca adaugarea moderatorului sa fie
        // administratorul organizatiei respective sau administrator
        if(organization.getUser().getId().equals(uid) && roles.contains("ROLE_ORG_ADMIN") || roles.contains("ROLE_ADMIN")) {
            //TODO: check user
            User user = userRepository.getById(moderatorId);

            if(!organization.getModerators().contains(user)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a moderator of the organization");
            }
            else {
                user.getModeratedOrganizations().remove(organization);
                userRepository.save(user);

                // Daca moderatorul nu mai este moderator al unei alte organizatii, i se revoca rolul de ORG_MODERATOR
                if(user.getModeratedOrganizations().size() == 0) {
                    keycloakAdminService.removeRole("ROLE_ORG_MODERATOR", moderatorId);
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
