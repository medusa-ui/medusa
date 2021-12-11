package io.getmedusa.medusa.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.security.SecureRandom;

public class SecurityContext {

    private final Principal principal;
    private final String uniqueId;

    public SecurityContext(Principal principal) {
        this.principal = principal;
        this.uniqueId = regenerateUniqueID();
    }

    public String regenerateUniqueID() {
        return System.currentTimeMillis() + "+" + new SecureRandom().nextInt(Integer.MAX_VALUE);
    }

    public UserDetails getUserDetails() {
        if(null != principal) {
            return (UserDetails) getAuthentication().getPrincipal();
        }
        return null;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Authentication getAuthentication() {
        return (Authentication) principal;
    }

    public Principal getPrincipal() {
        return principal;
    }
}
