package com.example.SoftbinatorProject.utils;

import com.example.SoftbinatorProject.models.Organization;
import com.example.SoftbinatorProject.models.User;

import java.util.Set;

public class AccessUtility {
    public static boolean isAdmin(Set<String> roles) {
        return roles.contains("ROLE_ADMIN");
    }

    public static boolean isOrgAdmin(Organization org, Long uid) {
        return org.getUser().getId().equals(uid);
    }

    public static boolean isOrgAdminOrMod(Organization org, User user) {
        return isOrgAdmin(org, user.getId()) || org.getModerators().contains(user);
    }
}
