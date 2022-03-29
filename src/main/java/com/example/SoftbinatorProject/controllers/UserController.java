package com.example.SoftbinatorProject.controllers;

import com.example.SoftbinatorProject.dtos.RegisterDto;
import com.example.SoftbinatorProject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.example.SoftbinatorProject.utils.HttpStatusUtility.successResponse;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        userService.registerUser(registerDto);
        return successResponse();
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterDto registerDto) {
        userService.registerAdmin(registerDto);
        return successResponse();
    }

}
