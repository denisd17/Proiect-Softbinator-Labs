package com.example.SoftbinatorProject.controllers;

import com.example.SoftbinatorProject.dtos.BalanceDto;
import com.example.SoftbinatorProject.dtos.RegisterDto;
import com.example.SoftbinatorProject.dtos.UserInfoDto;
import com.example.SoftbinatorProject.services.UserService;
import com.example.SoftbinatorProject.utils.KeycloakHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /*@GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id, Authentication authentication) {
        return new ResponseEntity<>(userService.getUser(id, Long.parseLong(KeycloakHelper.getUser(authentication)), KeycloakHelper.getUserRoles(authentication)), HttpStatus.OK);
    }*/

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        return new ResponseEntity<>(userService.getUser(Long.parseLong(KeycloakHelper.getUser(authentication))), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<UserInfoDto>> getUsersInfo() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserInfoDto userInfoDto, Authentication authentication) {
        return new ResponseEntity<>(userService.updateUser(id,
                Long.parseLong(KeycloakHelper.getUser(authentication)),
                KeycloakHelper.getUserRoles(authentication),
                userInfoDto),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        userService.deleteUser(id,
                Long.parseLong(KeycloakHelper.getUser(authentication)),
                KeycloakHelper.getUserRoles(authentication));

        return successResponse();
    }

    //TODO: change user password
    /*@PutMapping("/change-password")
    public ResponseEntity<?> updatePassword(Authentication authentication) {
        userService.updatePassword();
    }*/

    //TODO: check if double is passed
    @PutMapping("/add-funds")
    public ResponseEntity<?> addFunds(Authentication authentication, @RequestBody Double ammount) {
        return new ResponseEntity<>(userService.addFunds(Long.parseLong(KeycloakHelper.getUser(authentication)), ammount), HttpStatus.OK);
    }


}
