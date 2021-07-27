package io.getmedusa.medusa.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

public class SecurityContext {

    private final Principal principal;

    public SecurityContext(Principal principal) {
        this.principal = principal;
    }

    public UserDetails getUserDetails() {
        if(null != principal) {
            return (UserDetails) getAuthentication().getPrincipal();
        }
        return null;
    }

    public Authentication getAuthentication() {
        return (Authentication) principal;
    }

    public Principal getPrincipal() {
        return principal;
    }
}
