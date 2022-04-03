package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.*;
import com.example.SoftbinatorProject.models.*;
import com.example.SoftbinatorProject.repositories.*;
import com.example.SoftbinatorProject.utils.AccessUtility;
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
    private final TicketRepository ticketRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, OrganizationRepository organizationRepository, DonationRepository donationRepository, TicketRepository ticketRepository, PostRepository postRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.donationRepository = donationRepository;
        this.ticketRepository = ticketRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public ProjectInfoDto createProject(CreateProjectDto createProjectDto, Long orgId, Long uid, Set<String> roles) {
        //TODO: organization exists
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist!"));
        User user = userRepository.getById(uid);

        if(AccessUtility.isAdmin(roles) || AccessUtility.isOrgAdminOrMod(organization, user)) {
            if(createProjectDto.getType().equals("event")) {
                Event newEvent = Event.builder()
                        .name(createProjectDto.getName())
                        .description(createProjectDto.getDescription())
                        .ticketAmount(createProjectDto.getTicketAmount())
                        .ticketPrice(createProjectDto.getTicketPrice())
                        .organization(organization)
                        .extraAmount(0d)
                        .extraTicketsSold(0)
                        .build();

                projectRepository.save(newEvent);

                return EventInfoDto.builder()
                        .id(newEvent.getId())
                        .name(newEvent.getName())
                        .description(newEvent.getDescription())
                        .ticketAmount(newEvent.getTicketAmount())
                        .ticketPrice(newEvent.getTicketPrice())
                        .ticketsSold(0)
                        .type(newEvent.getDecriminatorValue())
                        .moneyRaised(0d)
                        .build();
            }
            else if(createProjectDto.getType().equals("fundraiser")) {
                Fundraiser newFundraiser = Fundraiser.builder()
                        .name(createProjectDto.getName())
                        .description(createProjectDto.getDescription())
                        .goal(createProjectDto.getGoal())
                        .organization(organization)
                        .extra(0d)
                        .build();

                projectRepository.save(newFundraiser);

                return FundraiserInfoDto.builder()
                        .id(newFundraiser.getId())
                        .name(newFundraiser.getName())
                        .description(newFundraiser.getDescription())
                        .goal(newFundraiser.getGoal())
                        .moneyRaised(0d)
                        .type(newFundraiser.getDecriminatorValue())
                        .build();
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type!");
            }


        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
    }


    public ProjectInfoDto getProject(Long orgId, Long id) {
        Project project = projectRepository.findById(id, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project or organization do not exist!"));

        if(project.getDecriminatorValue().equals("event")) {
            Event event = (Event) project;

            return EventInfoDto.builder()
                    .type(project.getDecriminatorValue())
                    .id(event.getId())
                    .name(event.getName())
                    .description(event.getDescription())
                    .ticketAmount(event.getTicketAmount())
                    .ticketPrice(event.getTicketPrice())
                    .ticketsSold(ticketRepository.getTicketsSold(event.getId()) + event.getExtraTicketsSold())
                    .moneyRaised(ticketRepository.getRaisedAmount(event.getId()) + event.getExtraAmount())
                    .posts(postRepository.getPostDtosByProjectId(event.getId()))
                    .build();
        }
        else {
            Fundraiser fundraiser = (Fundraiser) project;

            return FundraiserInfoDto.builder()
                    .type(project.getDecriminatorValue())
                    .id(fundraiser.getId())
                    .name(fundraiser.getName())
                    .description(fundraiser.getDescription())
                    .goal(fundraiser.getGoal())
                    .moneyRaised(donationRepository.getRaisedAmount(fundraiser.getId()) + fundraiser.getExtra())
                    .posts(postRepository.getPostDtosByProjectId(fundraiser.getId()))
                    .build();
        }
    }

    public List<ProjectInfoDto> getProjects(Long orgId) {
        if(organizationRepository.checkIfOrganizationExists(orgId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist");
        }

        return projectRepository.getProjectsDtoByOrgId(orgId);
    }

    public void deleteProject(Long orgId, Long id, Long uid, Set<String> roles) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist!"));

        User user = userRepository.getById(uid);

        if(AccessUtility.isAdmin(roles) || AccessUtility.isOrgAdminOrMod(organization, user)) {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project does not exist!"));

            if(project.getDecriminatorValue().equals("event")) {
                Event event = (Event) project;
                for(Ticket t : event.getTickets()) {
                     t.getUser().getTickets().remove(t);
                }
            }
            else if(project.getDecriminatorValue().equals("fundraiser")){
                Fundraiser fundraiser = (Fundraiser) project;
                for(Donation d : fundraiser.getDonations()) {
                    d.getUser().getDonations().remove(d);
                }
            }

            projectRepository.delete(project);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
        }
    }

    public ProjectInfoDto updateProject(UpdateProjectDto updateProjectDto, Long orgId, Long id, Long uid, Set<String> roles) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization does not exist!"));

        User user = userRepository.getById(uid);

        if(AccessUtility.isAdmin(roles) || AccessUtility.isOrgAdminOrMod(organization, user)) {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project does not exist!"));

            if(updateProjectDto.getName() != null)
                project.setName(updateProjectDto.getName());
            if(updateProjectDto.getDescription() != null)
                project.setDescription(updateProjectDto.getDescription());

            if(project.getDecriminatorValue().equals("event")) {
                Event event = (Event) project;
                //TODO: Merge cu el null?
                if(updateProjectDto.getTicketAmount() != null && updateProjectDto.getTicketAmount() >= 0)
                    event.setTicketAmount(updateProjectDto.getTicketAmount());
                if(updateProjectDto.getTicketPrice() != null && updateProjectDto.getTicketPrice() > 0)
                    event.setTicketPrice(updateProjectDto.getTicketPrice());

                projectRepository.save(event);

                return EventInfoDto.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .description(event.getDescription())
                        .ticketAmount(event.getTicketAmount())
                        .ticketPrice(event.getTicketPrice())
                        .ticketsSold(ticketRepository.getTicketsSold(event.getId()) + event.getExtraTicketsSold())
                        .moneyRaised(ticketRepository.getRaisedAmount(event.getId()) + event.getExtraAmount())
                        .type(event.getDecriminatorValue())
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
                        .moneyRaised(donationRepository.getRaisedAmount(fundraiser.getId()) + fundraiser.getExtra())
                        .type(fundraiser.getDecriminatorValue())
                        .build();
            }

        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this organization!");
        }
    }
}