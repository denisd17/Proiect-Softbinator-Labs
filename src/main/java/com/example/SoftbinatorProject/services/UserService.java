package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.*;
import com.example.SoftbinatorProject.models.*;
import com.example.SoftbinatorProject.repositories.*;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final AmazonService amazonService;
    private final ProjectRepository projectRepository;
    private final DonationRepository donationRepository;
    private final TicketRepository ticketRepository;
    private final OrganizationRepository organizationRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MailService mailService;
    private final OrganizationService organizationService;

    @Autowired
    public UserService(UserRepository userRepository, KeycloakAdminService keycloakAdminService, AmazonService amazonService, ProjectRepository projectRepository, DonationRepository donationRepository, TicketRepository ticketRepository, OrganizationRepository organizationRepository, CommentRepository commentRepository, PostRepository postRepository, MailService mailService, OrganizationService organizationService) {
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.amazonService = amazonService;
        this.projectRepository = projectRepository;
        this.donationRepository = donationRepository;
        this.ticketRepository = ticketRepository;
        this.organizationRepository = organizationRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mailService = mailService;
        this.organizationService = organizationService;
    }

    @SneakyThrows
    public void registerUser(RegisterDto registerDto, MultipartFile image) {
        if (userRepository.findByEmailOrUsername(registerDto.getEmail(), registerDto.getUsername()).isPresent()) {
            throw new BadRequestException("Email or username already in use!");
        }

        String profilePicUrl = amazonService.upload("images", "profile_pic_" + registerDto.getUsername(), image);

        User newUser = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .username(registerDto.getUsername())
                .moneyBalance(0.d)
                .role("user")
                .profilePicUrl(profilePicUrl)
                .build();

        newUser = userRepository.save(newUser);
        keycloakAdminService.registerUser(newUser.getId(), registerDto.getPassword(), "ROLE_USER");
        mailService.sendRegistrationEmail(newUser.getEmail(), newUser.getUsername(), newUser.getFirstName());

    }

    @SneakyThrows
    public void registerAdmin(RegisterDto registerDto, MultipartFile image) {
        if (userRepository.findByEmailOrUsername(registerDto.getEmail(), registerDto.getUsername()).isPresent()) {
            throw new BadRequestException("Email or username already in use!");
        }

        String profilePicUrl = amazonService.upload("images", "profile_pic_" + registerDto.getUsername(), image);

        User newUser = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .username(registerDto.getUsername())
                .moneyBalance(0.d)
                .role("admin")
                .profilePicUrl(profilePicUrl)
                .build();

        newUser = userRepository.save(newUser);
        keycloakAdminService.registerUser(newUser.getId(), registerDto.getPassword(), "ROLE_ADMIN");
        mailService.sendRegistrationEmail(newUser.getEmail(), newUser.getUsername(), newUser.getFirstName());

    }

    public UserInfoDto getUser(Long uid) {
        return userRepository.getUserDtoById(uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
    }

    public List<UserInfoDto> getUsers() {
        return userRepository.getUserDtos();
    }

    public UserInfoDto updateUser(Long uid, Long id, Set<String> roles, UserInfoDto userInfoDto, MultipartFile image) {
        if(uid.equals(id) || roles.contains("ROLE_ADMIN")) {
            User user = userRepository.findById(uid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

            if(userInfoDto != null) {
                if (!userRepository.findDifferentByEmailOrUsername(userInfoDto.getEmail(), userInfoDto.getUsername(), uid).isEmpty()) {
                    throw new BadRequestException("Email or username already in use!");
                }

                String oldUsername = user.getUsername();
                String profilePicUrl = user.getProfilePicUrl();

                // Actualizam mai intai numele de utilizator
                if(userInfoDto.getUsername() != null) {
                    user.setUsername(userInfoDto.getUsername());
                }
                // Daca am actualizat numele si am oferit o noua poza
                // Stergem vechea poza
                // Incarcam noua poza cu numele corect
                if(userInfoDto.getUsername() != null && image != null) {
                    amazonService.deleteFileFroms3bucket("images", "profile_pic_" + oldUsername);
                    profilePicUrl = amazonService.upload("images", "profile_pic_" + user.getUsername(), image);
                }
                // Daca am actualizat numele si nu am oferit o noua poza
                // Actualizam numele pozei de profil in bucket
                else if(userInfoDto.getUsername() != null && image == null) {
                    profilePicUrl = amazonService.renameFileOns3bucket("images", "profile_pic_" + oldUsername, "profile_pic_" + user.getUsername());
                }
                else if(userInfoDto.getUsername() == null && image != null) {
                    profilePicUrl = amazonService.upload("images", "profile_pic_" + user.getUsername(), image);
                    user.setProfilePicUrl(profilePicUrl);
                }

                if(userInfoDto.getEmail() != null)
                    user.setEmail(userInfoDto.getEmail());
                if(userInfoDto.getFirstName() != null)
                    user.setFirstName(userInfoDto.getFirstName());
                if(userInfoDto.getLastName() != null)
                    user.setLastName(userInfoDto.getLastName());

                // Link catre poza veche in cazul in care nu a fost actualizata sau null in cazul in care nu a existat
                user.setProfilePicUrl(profilePicUrl);

                userRepository.save(user);
            }
            // Daca userInfoDto e null, inseamna ca am oferit doar imaginea pentru actualizare
            else if(image != null){
                String profilePicUrl = amazonService.upload("images", "profile_pic_" + user.getUsername(), image);
                user.setProfilePicUrl(profilePicUrl);
            }
            // Bad Request daca nu am oferit nimic pentru update
            else {
                throw new BadRequestException();
            }

            return UserInfoDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .moneyBalance(user.getMoneyBalance())
                    .profilePicUrl(user.getProfilePicUrl())
                    .build();

        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access user info!");
        }
    }

    public void deleteUser(Long uid, Long id, Set<String> roles) {
        if(uid.equals(id) || roles.contains("ROLE_ADMIN")) {
            User user = userRepository.findById(uid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

            // Stergerea donatiilor si a biletelor userului
            for(Donation d : user.getDonations()) {
                Double amount = d.getAmount();
                Fundraiser fundraiser = d.getFundraiser();
                fundraiser.addExtra(amount);
                //user.getDonations().remove(d);
                fundraiser.getDonations().remove(d);
                projectRepository.save(fundraiser);
                donationRepository.delete(d);
            }
            user.getDonations().clear();

            for(Ticket t : user.getTickets()) {
                Integer amount = t.getAmount();
                Double price = t.getPrice();
                Event event = t.getEvent();
                event.addExtra(amount, price);
                //user.getTickets().remove(t);
                event.getTickets().remove(t);
                projectRepository.save(event);
                ticketRepository.delete(t);
            }
            user.getTickets().clear();

            // Stergerea comentariilor userului
            for(Comment c : user.getComments()) {
                Post p = c.getPost();
                p.getComments().remove(c);
                postRepository.save(p);
                commentRepository.delete(c);
            }
            user.getComments().clear();

            System.out.println(user.getOrganizations().size());



            // Stergerea organizatiilor userului
            for(Organization o : user.getOrganizations()) {
                organizationService.deleteOrganization(o.getId(), id, roles);
            }
            user.getOrganizations().clear();

            user.getModeratedOrganizations().clear();
            // Stergerea pozei de profil din bucket
            amazonService.deleteFileFroms3bucket("images", "profile_pic_" + user.getUsername());

            // Stergere user din db si din keycloak
            userRepository.delete(user);
            keycloakAdminService.deleteUser(user.getId());

        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access user info!");
        }
    }

    public void changePassword(ChangePasswordDto changePasswordDto, Long uid) {
        changePasswordDto.setUserId(uid);
        keycloakAdminService.changePassword(changePasswordDto);
    }

    public BalanceDto addFunds(Long uid, Double amount) {
        if(amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid amount!");
        }

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        Double oldBalance = user.getMoneyBalance();
        Double newBalance = oldBalance + amount;
        user.setMoneyBalance(newBalance);
        userRepository.save(user);

        return BalanceDto.builder()
                .oldBalance(oldBalance)
                .amountAdded(amount)
                .newBalance(newBalance)
                .build();
    }

    public ReceiptDto getReceipts(Long uid) {
        ReceiptDto receiptDto = new ReceiptDto();
        receiptDto.setDonations(donationRepository.getDonationDtos(uid));
        receiptDto.setTickets(ticketRepository.getTicketDtos(uid));

        return receiptDto;
    }

}
