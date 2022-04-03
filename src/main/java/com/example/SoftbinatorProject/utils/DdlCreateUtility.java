package com.example.SoftbinatorProject.utils;

import com.example.SoftbinatorProject.services.KeycloakAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

//TODO:
@Component
public class DdlCreateUtility {
    private final KeycloakAdminService keycloakAdminService;

    @Autowired
    public DdlCreateUtility(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }
}
