package com.example.SoftbinatorProject.utils;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;

public class KeycloakUtility {

    // The Keycloak Login URL
    static String baseUrl;

    @Value("${keycloak.auth-server-url}")
    public void setBaseUrl(String nonStaticBaseUrl){
        KeycloakUtility.baseUrl = nonStaticBaseUrl;
    }

    // The Keycloak admin username
    private static String adminUsername;

    @Value("${my.keycloak.admin.username}")
    public void setAdminUsername(String nonStaticAdminUsername){
        KeycloakUtility.adminUsername = nonStaticAdminUsername;
    }

    // The Keycloak admin password
    private static String adminPassword;

    @Value("${my.keycloak.admin.password}")
    public void setAdminPassword(String nonStaticAdminPassword){
        KeycloakUtility.adminPassword = nonStaticAdminPassword;
    }

    // The Keycloak Admin-CLI client ID
    private static String adminCli;

    @Value("${my.keycloak.admin.client}")
    public void setAdminCli(String nonStaticAdminCli){
        KeycloakUtility.adminCli = nonStaticAdminCli;
    }


    public static Keycloak getAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(baseUrl)
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(adminCli)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

}
