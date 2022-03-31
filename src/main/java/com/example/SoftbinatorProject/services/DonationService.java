package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.DonationDto;
import com.example.SoftbinatorProject.models.Donation;
import com.example.SoftbinatorProject.models.Fundraiser;
import com.example.SoftbinatorProject.models.Project;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.DonationRepository;
import com.example.SoftbinatorProject.repositories.OrganizationRepository;
import com.example.SoftbinatorProject.repositories.ProjectRepository;
import com.example.SoftbinatorProject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    @Autowired
    public DonationService(DonationRepository donationRepository, ProjectRepository projectRepository, OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.donationRepository = donationRepository;
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    public DonationDto donate(Long orgId, Long projectId, Long uid, DonationDto donationDto) {

        User user = userRepository.getById(uid);
        //TODO: check or else throw
        Project project = projectRepository.findById(projectId, orgId).orElseThrow();

        if(donationDto.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid donation amount!");
        }
        else if(user.getMoneyBalance() - donationDto.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have sufficient funds!");
        }
        else if(project.getDecriminatorValue().equals("event")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only donate to fundraisers!");
        }
        else {
            //TODO: check if project exists
            Fundraiser fundraiser = (Fundraiser) project;
            Donation donation = Donation.builder()
                    .amount(donationDto.getAmount())
                    .fundraiser(fundraiser)
                    .user(user)
                    .build();

            donationRepository.save(donation);

            // Actualizare fonduri utilizator
            Double newBalance = user.getMoneyBalance() - donation.getAmount();
            user.setMoneyBalance(newBalance);
            userRepository.save(user);

            return DonationDto.builder()
                    .amount(donation.getAmount())
                    .projectId(fundraiser.getId())
                    .projectName(fundraiser.getName())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .build();
        }

    }
}
