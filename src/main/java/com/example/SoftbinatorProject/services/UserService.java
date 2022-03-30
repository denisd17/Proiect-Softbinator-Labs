package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.BalanceDto;
import com.example.SoftbinatorProject.dtos.ChangePasswordDto;
import com.example.SoftbinatorProject.dtos.RegisterDto;
import com.example.SoftbinatorProject.dtos.UserInfoDto;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.UserRepository;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
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

    @Autowired
    public UserService(UserRepository userRepository, KeycloakAdminService keycloakAdminService) {
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    @SneakyThrows
    public void registerUser(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new BadRequestException("User with email " + registerDto.getEmail() + " already exists.");
        }

        User newUser = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .username(registerDto.getUsername())
                .moneyBalance(0.d)
                .role("user")
                .build();

        newUser = userRepository.save(newUser);
        keycloakAdminService.registerUser(newUser.getId(), registerDto.getPassword(), "ROLE_USER");

    }

    @SneakyThrows
    public void registerAdmin(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new BadRequestException("User with email " + registerDto.getEmail() + " already exists.");
        }

        User newUser = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .username(registerDto.getUsername())
                .moneyBalance(0.d)
                .role("admin")
                .build();

        newUser = userRepository.save(newUser);
        keycloakAdminService.registerUser(newUser.getId(), registerDto.getPassword(), "ROLE_ADMIN");

    }

    public UserInfoDto getUser(Long uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

        return UserInfoDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .moneyBalance(user.getMoneyBalance())
                .build();
    }

    public List<UserInfoDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserInfoDto> userInfo = new ArrayList<>();

        for(User user : users) {
            userInfo.add(UserInfoDto.builder().id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .moneyBalance(user.getMoneyBalance())
                    .build());
        }
        return userInfo;
    }

    public UserInfoDto updateUser(Long uid, Long id, Set<String> roles, UserInfoDto userInfoDto) {
        if(uid.equals(id) || roles.contains("ROLE_ADMIN")) {
            User user = userRepository.findById(uid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

            //TODO: custom mapper si actualizarea tuturor campurilor
            user.setUsername(userInfoDto.getUsername());
            userRepository.save(user);

            return UserInfoDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .moneyBalance(user.getMoneyBalance())
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access user info!");
    }

    public void deleteUser(Long uid, Long id, Set<String> roles) {
        if(uid.equals(id) || roles.contains("ROLE_ADMIN")) {
            User user = userRepository.findById(uid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

            userRepository.delete(user);
            keycloakAdminService.deleteUser(user.getId());
        }
        //TODO: DE REVAZUT EXCEPTII AICI  (good/bad practice)
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access user info!");
        }

    }

    //TODO
    public void updatePassword(Long uid, String password) {
        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .userId(uid)
                .newPassword(password)
                .build();

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

}
