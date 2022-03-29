package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.RegisterDto;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;

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
}
