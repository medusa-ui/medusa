package io.getmedusa.medusa.core.injector;

import org.springframework.security.core.context.SecurityContextHolder;

@FunctionalInterface
public interface Applicable {
    /** Globally Applicable **/
    Applicable TRUE = () -> true;
    /** Applicable if the current user is authenticated **/
    Applicable AUTHENTICATED = new ApplicableIfAuthenticated();
    boolean apply();
}

/**
 * Applicable when a user is authenticated
*/
class ApplicableIfAuthenticated implements Applicable {
    @Override
    public boolean apply() {
        if (null == SecurityContextHolder.getContext() ||
            null == SecurityContextHolder.getContext().getAuthentication()) {
            return false;
        }
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }
}