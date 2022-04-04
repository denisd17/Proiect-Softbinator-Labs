package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.dtos.OrganizationInfoDto;
import com.example.SoftbinatorProject.models.Organization;
import com.example.SoftbinatorProject.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class OrganizationRepositoryTest {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrganizationRepositoryTest(OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    @AfterEach
    void tearDown() {
        organizationRepository.deleteAll();
    }

    @Test
    void checkGetDtoByIdReturnsOrganization() {
        //given
        User orgAdmin = User.builder()
                .username("username")
                .email("email@gmail.com")
                .build();
        userRepository.save(orgAdmin);


        Organization organization = Organization.builder()
                .user(orgAdmin)
                .build();

        organizationRepository.save(organization);
        //when
        Optional<OrganizationInfoDto> organizationInfoDto = organizationRepository.getOrganizationDtoById(organization.getId());
        //then
        Assertions.assertTrue(organizationInfoDto.isPresent());
        Assertions.assertEquals(organization.getId(), organizationInfoDto.get().getId());
    }

    @Test
    void checkGetAllDtosReturnsAll() {
        //given
        User orgAdmin = User.builder()
                .username("username")
                .email("email@gmail.com")
                .build();
        userRepository.save(orgAdmin);

        Organization organization1 = Organization.builder()
                .user(orgAdmin)
                .build();

        Organization organization2 = Organization.builder()
                .user(orgAdmin)
                .build();

        organizationRepository.save(organization1);
        organizationRepository.save(organization2);
        //when
        List<OrganizationInfoDto> organizationInfoDtos = organizationRepository.getOrganizationDtos();
        //then
        Assertions.assertEquals(2, organizationInfoDtos.size());
    }

    @Test
    void checkCheckIfOrganizationExistsWorks() {
        //given
        User orgAdmin = User.builder()
                .username("username")
                .email("email@gmail.com")
                .build();
        userRepository.save(orgAdmin);


        Organization organization = Organization.builder()
                .user(orgAdmin)
                .build();

        organizationRepository.save(organization);
        //when
        Optional<Integer> result = organizationRepository.checkIfOrganizationExists(organization.getId());
        //then
        Assertions.assertTrue(result.isPresent());

    }
}
