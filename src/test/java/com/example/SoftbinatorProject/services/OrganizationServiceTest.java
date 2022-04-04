package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.models.Organization;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.OrganizationRepository;
import com.example.SoftbinatorProject.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
public class OrganizationServiceTest {
    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @Test
    void getOrganizations() {
        //Given
        OrganizationInfoDto firstOrganization = OrganizationInfoDto.builder().id(1L).name("organization1").build();
        OrganizationInfoDto secondOrganization = OrganizationInfoDto.builder().id(2L).name("organization2").build();
        List<OrganizationInfoDto> organizatonList = List.of(firstOrganization, secondOrganization);
        when(organizationRepository.getOrganizationDtos()).thenReturn(organizatonList);

        //when
        List<OrganizationInfoDto> result = organizationService.getOrganizations();

        //then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(organizatonList, result);
        verify(organizationRepository).getOrganizationDtos();
    }

    @Test
    void updateOrganization() {
        //Given
        User user = User.builder().id(1L).build();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ORG_ADMIN");
        OrganizationInfoDto organizationDto = OrganizationInfoDto.builder().id(2L).name("organization1").build();
        Organization organization = Organization.builder().id(2L).user(user).name("organization1").build();
        when(organizationRepository.findById(organization.getId())).thenReturn(java.util.Optional.of(organization));
        when(organizationRepository.save(any(Organization.class))).thenReturn(organization);

        //when
        OrganizationInfoDto result = organizationService.updateOrganization(organizationDto, organization.getId(), user.getId(), roles);

        //then
        Assertions.assertEquals(organizationDto, result);
        verify(organizationRepository).findById(any(Long.class));
        verify(organizationRepository).save(any(Organization.class));
    }

    @Test
    void deleteOrganization() {
        //Given
        User user = User.builder()
                .id(1L)
                .organizations(new ArrayList<>())
                .build();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ORG_ADMIN");
        Organization organizationToDelete = Organization.builder()
                .id(2L)
                .user(user)
                .name("organization1")
                .moderators(new ArrayList<>())
                .build();
        user.getOrganizations().add(organizationToDelete);
        when(organizationRepository.findById(2L)).thenReturn(Optional.ofNullable(organizationToDelete)).thenReturn(Optional.empty());

        //when
        organizationService.deleteOrganization(organizationToDelete.getId(), user.getId(), roles);
        Optional<Organization> result = organizationRepository.findById(organizationToDelete.getId());

        //then
        Assertions.assertTrue(result.isEmpty());
        verify(organizationRepository).delete(any(Organization.class));

    }
}
