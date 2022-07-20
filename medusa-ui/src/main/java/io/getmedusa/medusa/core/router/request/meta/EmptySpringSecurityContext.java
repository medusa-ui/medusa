package io.getmedusa.medusa.core.router.request.meta;

import org.springframework.security.core.Authentication;

/**
 * Used as fallback in case there is Spring Security, but no such context on the request
 */
public class EmptySpringSecurityContext  implements org.springframework.security.core.context.SecurityContext {

    private static final long serialVersionUID = 1;

    @Override
    public Authentication getAuthentication() { return null;}

    @Override
    public void setAuthentication(Authentication authentication) { /* intentionally left empty */ }

}
