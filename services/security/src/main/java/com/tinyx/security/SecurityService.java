package com.tinyx.security;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecurityService {

    @RolesAllowed("admin")
    public void performAdminTask() {
        // Logic for admin task
    }

    @RolesAllowed("user")
    public void performUserTask() {
        // Logic for user task
    }
}
