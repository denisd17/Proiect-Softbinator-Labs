package com.example.SoftbinatorProject.services;


import com.example.SoftbinatorProject.config.AuthClient;
import com.example.SoftbinatorProject.dtos.ChangePasswordDto;
import com.example.SoftbinatorProject.repositories.UserRepository;
import com.example.SoftbinatorProject.utils.KeycloakUtility;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.resource}")
    private String keycloakClient;

    private final Keycloak keycloak;
    private final AuthClient authClient;
    private final UserRepository userRepository;
    private RealmResource realm;

    @Autowired
    public KeycloakAdminService(Keycloak keycloak, AuthClient authClient, UserRepository userRepository) {
        this.keycloak = keycloak;
        this.authClient = authClient;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initRealmResource() {
        this.realm = this.keycloak.realm(keycloakRealm);
    }

    public void registerUser(Long userId, String password, String role) {
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setEnabled(true);
        keycloakUser.setUsername(userId.toString());


        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setTemporary(false);

        keycloakUser.setCredentials(Collections.singletonList(credentialRepresentation));

        // Add the user to the Keycloak Realm
        Response response = realm.users().create(keycloakUser);
        String keycloakUserId = getCreatedId(response);

        UserResource userResource = realm.users().get(keycloakUserId);
        RoleRepresentation roleRepresentation = realm.roles().get(role).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));

    }

    public void deleteUser(Long userId) {
        UserRepresentation userRepresentation = realm.users().search(userId.toString()).get(0);
        realm.users().delete(userRepresentation.getId());

    }

    public void deleteAllUsers() {
        List<UserRepresentation> users = realm.users().list();
        for(UserRepresentation ur : users) {
            realm.users().delete(ur.getId());
        }
    }

    public void changePassword(ChangePasswordDto changePasswordDto) {
        UserRepresentation userRepresentation = realm.users().search(changePasswordDto.getUserId().toString()).get(0);

        CredentialRepresentation newCredential = new CredentialRepresentation();
        newCredential.setType(CredentialRepresentation.PASSWORD);
        newCredential.setValue(changePasswordDto.getNewPassword());
        newCredential.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(newCredential));

        realm.users().get(userRepresentation.getId()).update(userRepresentation);
    }

    public void addRole(String roleName, Long uid) {
        String keycloakUid = realm.users().search(uid.toString()).get(0).getId();
        UserResource userResource = realm.users().get(keycloakUid);
        RoleRepresentation roleToAdd = realm.roles().get(roleName).toRepresentation();

        userResource.roles().realmLevel().add(Collections.singletonList(roleToAdd));
    }

    public void removeRole(String roleName, Long uid) {
        String keycloakUid = realm.users().search(uid.toString()).get(0).getId();
        UserResource userResource = realm.users().get(keycloakUid);
        RoleRepresentation roleToRemove = realm.roles().get(roleName).toRepresentation();

        userResource.roles().realmLevel().remove(Collections.singletonList(roleToRemove));
    }

}
