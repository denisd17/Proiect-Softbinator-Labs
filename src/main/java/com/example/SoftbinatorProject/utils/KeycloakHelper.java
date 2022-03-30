package com.example.SoftbinatorProject.utils;

import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.Authentication;

import java.util.Set;

public class KeycloakHelper {

    public static String getUser(Authentication authentication) {
        return ((KeycloakPrincipal) authentication.getPrincipal()).getKeycloakSecurityContext()
                .getToken().getPreferredUsername();
    }

    public static Set<String> getUserRoles(Authentication authentication) {
        return ((KeycloakPrincipal) authentication.getPrincipal()).getKeycloakSecurityContext()
                .getToken().getRealmAccess().getRoles();
    }
}

