package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.*;
import com.example.SoftbinatorProject.models.*;
import com.example.SoftbinatorProject.repositories.DonationRepository;
import com.example.SoftbinatorProject.repositories.OrganizationRepository;
import com.example.SoftbinatorProject.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final DonationRepository donationRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, OrganizationRepository organizationRepository, DonationRepository donationRepository) {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.donationRepository = donationRepository;
    }

    //TODO: Calcul goal si bilete vandute
    public ProjectInfoDto createProject(CreateProjectDto createProjectDto, Long orgId, Long uid, Set<String> roles) {
        Organization organization = organizationRepository.getById(orgId);

        if(organization.getUser().getId().equals(uid) && roles.contains("ROLE_ORG_ADMIN") || roles.contains("ROLE_ADMIN")) {
            if(createProjectDto.getType().equals("event")) {
                Event newEvent = Event.builder()
                        .name(createProjectDto.getName())
                        .description(createProjectDto.getDescription())
                        .ticketAmount(createProjectDto.getTicketAmount())
                        .ticketPrice(createProjectDto.getTicketPrice())
                        .organization(organization)
                        .build();

                projectRepository.save(newEvent);

                return EventInfoDto.builder()
                        .id(newEvent.getId())
                        .name(newEvent.getName())
                        .description(newEvent.getDescription())
                        .ticketAmount(newEvent.getTicketAmount())
                        .ticketPrice(newEvent.getTicketPrice())
                        .ticketsSold(0)
                        .build();
            }
            else if(createProjectDto.getType().equals("fundraiser")) {
                Fundraiser newFundraiser = Fundraiser.builder()
                        .name(createProjectDto.getName())
                        .description(createProjectDto.getDescription())
                        .goal(createProjectDto.getGoal())
                        .organization(organization)
                        .build();

                projectRepository.save(newFundraiser);

                return FundraiserInfoDto.builder()
                        .id(newFundraiser.getId())
                        .name(newFundraiser.getName())
                        .description(newFundraiser.getDescription())
                        .goal(newFundraiser.getGoal())
                        .moneyRaised(0d)
                        .build();
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type!");
            }


        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
    }

    /*
    public EventInfoDto createEvent(EventInfoDto eventInfoDto, Long id) {
        // TODO: Check user rights to org
        // TODO: Check if org exists
        Organization organization = organizationRepository.getById(id);

        Event newEvent = Event.builder()
                .name(eventInfoDto.getName())
                .description(eventInfoDto.getDescription())
                .ticketAmount(eventInfoDto.getTicketAmount())
                .ticketPrice(eventInfoDto.getTicketPrice())
                .organization(organization)
                .build();

        projectRepository.save(newEvent);

        return EventInfoDto.builder()
                .id(newEvent.getId())
                .name(newEvent.getName())
                .description(newEvent.getDescription())
                .ticketAmount(newEvent.getTicketAmount())
                .ticketPrice(newEvent.getTicketPrice())
                .build();
    }

    public FundraiserInfoDto createFundraiser(FundraiserInfoDto fundraiserInfoDto, Long id) {
        //TODO: Check user rights to org
        //TODO: Check if org exists
        Organization organization = organizationRepository.getById(id);

        Fundraiser newFundraiser = Fundraiser.builder()
                .name(fundraiserInfoDto.getName())
                .description(fundraiserInfoDto.getDescription())
                .goal(fundraiserInfoDto.getGoal())
                .organization(organization)
                .build();

        projectRepository.save(newFundraiser);

        return FundraiserInfoDto.builder()
                .id(newFundraiser.getId())
                .name(newFundraiser.getName())
                .description(newFundraiser.getDescription())
                .goal(newFundraiser.getGoal())
                .moneyRaised(0d)
                .build();
    }
    */
    public ProjectInfoDto getProject(Long orgId, Long id) {
        //TODO: Organization check and project check separate from one another
        Project project = projectRepository.findById(id, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or organization do not exist!"));

        if(project.getDecriminatorValue().equals("event")) {
            Event event = (Event) project;

            return EventInfoDto.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .description(event.getDescription())
                    .ticketAmount(event.getTicketAmount())
                    .ticketPrice(event.getTicketPrice())
                    .ticketsSold(0)
                    .build();
        }
        else {
            Fundraiser fundraiser = (Fundraiser) project;

            return FundraiserInfoDto.builder()
                    .id(fundraiser.getId())
                    .name(fundraiser.getName())
                    .description(fundraiser.getDescription())
                    .goal(fundraiser.getGoal())
                    .moneyRaised(donationRepository.getRaisedAmount(fundraiser.getId()))
                    .build();
        }
    }

    public List<ProjectInfoDto> getProjects(Long orgId) {
        //TODO: Organization check and project check separate from one another
        List<Project> projects = projectRepository.findAllByOrganizationId(orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or organization do not exist!"));

        List<ProjectInfoDto> projectInfoDtos = new ArrayList<>();

        for(Project p : projects) {
            if(p.getDecriminatorValue().equals("event")) {
                Event event = (Event) p;

                projectInfoDtos.add(EventInfoDto.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .description(event.getDescription())
                        .ticketAmount(event.getTicketAmount())
                        .ticketPrice(event.getTicketPrice())
                        .ticketsSold(0)
                        .build());
            }
            else {
                Fundraiser fundraiser = (Fundraiser) p;

                projectInfoDtos.add(FundraiserInfoDto.builder()
                        .id(fundraiser.getId())
                        .name(fundraiser.getName())
                        .description(fundraiser.getDescription())
                        .goal(fundraiser.getGoal())
                        .moneyRaised(donationRepository.getRaisedAmount(fundraiser.getId()))
                        .build());
            }
        }

        return projectInfoDtos;
    }

    public void deleteProject(Long orgId, Long id, Long uid, Set<String> roles) {
        //TODO: Check if organization and project exist
        Organization organization = organizationRepository.getById(orgId);

        if(organization.getUser().getId().equals(uid) && roles.contains("ROLE_ORG_ADMIN") || roles.contains("ROLE_ADMIN")) {
            Project project = projectRepository.getById(id);
            projectRepository.delete(project);
        }
    }

    public ProjectInfoDto updateProject(UpdateProjectDto updateProjectDto, Long orgId, Long id, Long uid, Set<String> roles) {
        //TODO: Check if organization and project exist
        Organization organization = organizationRepository.getById(orgId);

        if(organization.getUser().getId().equals(uid) && roles.contains("ROLE_ORG_ADMIN") || roles.contains("ROLE_ADMIN")) {
            Project project = projectRepository.findById(id, orgId).orElseThrow();

            project.setName(updateProjectDto.getName());
            project.setDescription(updateProjectDto.getDescription());

            if(project.getDecriminatorValue().equals("event")) {
                Event event = (Event) project;
                //TODO: Check is amount is valid number
                event.setTicketAmount(updateProjectDto.getTicketAmount());
                projectRepository.save(event);

                return EventInfoDto.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .description(event.getDescription())
                        .ticketAmount(event.getTicketAmount())
                        .ticketPrice(event.getTicketPrice())
                        .ticketsSold(0)
                        .build();
            }
            else {
                Fundraiser fundraiser = (Fundraiser) project;
                projectRepository.save(fundraiser);

                return FundraiserInfoDto.builder()
                        .id(fundraiser.getId())
                        .name(fundraiser.getName())
                        .description(fundraiser.getDescription())
                        .goal(fundraiser.getGoal())
                        .moneyRaised(donationRepository.getRaisedAmount(fundraiser.getId()))
                        .build();
            }

        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
    }
}