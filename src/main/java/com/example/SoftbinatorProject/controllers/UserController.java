package com.example.SoftbinatorProject.controllers;

import com.example.SoftbinatorProject.dtos.*;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.services.UserService;
import com.example.SoftbinatorProject.utils.KeycloakHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> registerUser(@RequestPart("json") RegisterDto registerDto, @RequestPart(value = "image", required = false) MultipartFile image) {
        userService.registerUser(registerDto, image);
        return successResponse();
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestPart("json") RegisterDto registerDto, @RequestPart(value = "image", required = false) MultipartFile image) {
        userService.registerAdmin(registerDto, image);
        return successResponse();
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        return new ResponseEntity<>(userService.getUser(Long.parseLong(KeycloakHelper.getUser(authentication))), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<UserInfoDto>> getUsersInfo() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestPart(value = "json", required = false) UserInfoDto userInfoDto, @RequestPart(value = "image", required = false) MultipartFile image, Authentication authentication) {
        return new ResponseEntity<>(userService.updateUser(id,
                Long.parseLong(KeycloakHelper.getUser(authentication)),
                KeycloakHelper.getUserRoles(authentication),
                userInfoDto,
                image),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        userService.deleteUser(id,
                Long.parseLong(KeycloakHelper.getUser(authentication)),
                KeycloakHelper.getUserRoles(authentication));

        return successResponse();
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto, Authentication authentication) {
        userService.changePassword(changePasswordDto, Long.parseLong(KeycloakHelper.getUser(authentication)));

        return successResponse();
    }

    @GetMapping("/receipts")
    public ResponseEntity<?> getReceipts(Authentication authentication) {
        return new ResponseEntity<>(userService.getReceipts(Long.parseLong(KeycloakHelper.getUser(authentication))), HttpStatus.OK);
    }

    @PutMapping("/add-funds")
    public ResponseEntity<?> addFunds(Authentication authentication, @RequestBody Double ammount) {
        return new ResponseEntity<>(userService.addFunds(Long.parseLong(KeycloakHelper.getUser(authentication)), ammount), HttpStatus.OK);
    }

}
