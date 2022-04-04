package com.example.SoftbinatorProject.repositories;

import com.example.SoftbinatorProject.dtos.DonationInfoDto;
import com.example.SoftbinatorProject.models.Donation;
import com.example.SoftbinatorProject.models.Fundraiser;
import com.example.SoftbinatorProject.models.Organization;
import com.example.SoftbinatorProject.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
public class DonationRepositoryTest {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;
    private final DonationRepository donationRepository;

    @Autowired
    public DonationRepositoryTest(UserRepository userRepository, OrganizationRepository organizationRepository, ProjectRepository projectRepository, DonationRepository donationRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
        this.donationRepository = donationRepository;
    }

    @AfterEach
    void tearDown() {
        organizationRepository.deleteAll();
    }

    @Test
    void checkIfRaisedAmountWorks() {
        User orgAdmin = User.builder()
                .username("username")
                .email("email@gmail.com")
                .build();
        userRepository.save(orgAdmin);


        Organization organization = Organization.builder()
                .user(orgAdmin)
                .build();

        organizationRepository.save(organization);

        Fundraiser fundraiser = Fundraiser.builder()
                .organization(organization)
                .build();

        projectRepository.save(fundraiser);

        Double amount1 = 100d;
        Double amount2 = 100d;

        Donation donation1 = Donation.builder()
                .user(orgAdmin)
                .fundraiser(fundraiser)
                .amount(amount1)
                .build();

        Donation donation2 = Donation.builder()
                .user(orgAdmin)
                .fundraiser(fundraiser)
                .amount(amount2)
                .build();

        donationRepository.save(donation1);
        donationRepository.save(donation2);
        //when
        Double raisedAmount = donationRepository.getRaisedAmount(fundraiser.getId());
        //then
        Assertions.assertEquals(amount1 + amount2, raisedAmount);
    }

    @Test
    void checkIfRaisedAmountWorksNoDonations() {
        User orgAdmin = User.builder()
                .username("username")
                .email("email@gmail.com")
                .build();
        userRepository.save(orgAdmin);


        Organization organization = Organization.builder()
                .user(orgAdmin)
                .build();

        organizationRepository.save(organization);

        Fundraiser fundraiser = Fundraiser.builder()
                .organization(organization)
                .build();

        projectRepository.save(fundraiser);

        //when
        Double raisedAmount = donationRepository.getRaisedAmount(fundraiser.getId());
        //then
        Assertions.assertEquals(0, raisedAmount);
    }

    @Test
    void checkIfgetDonationDtosWorks() {
        User orgAdmin = User.builder()
                .username("username")
                .email("email@gmail.com")
                .build();
        userRepository.save(orgAdmin);


        Organization organization = Organization.builder()
                .user(orgAdmin)
                .build();

        organizationRepository.save(organization);

        Fundraiser fundraiser = Fundraiser.builder()
                .organization(organization)
                .build();

        projectRepository.save(fundraiser);

        Donation donation1 = Donation.builder()
                .user(orgAdmin)
                .fundraiser(fundraiser)
                .build();

        Donation donation2 = Donation.builder()
                .user(orgAdmin)
                .fundraiser(fundraiser)
                .build();

        donationRepository.save(donation1);
        donationRepository.save(donation2);
        //when
        List<DonationInfoDto> donationInfoDtoList = donationRepository.getDonationDtos(fundraiser.getId());
        //then
        Assertions.assertEquals(0, donationInfoDtoList.size());

    }
}
