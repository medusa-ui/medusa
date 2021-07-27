package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.UUID;

class SecurityContextTest {

    @Test
    void testUsernamePasswordToken() {
        UserDetails user = User.withUsername("johndoe123").password("p@ss123").authorities("USER").build();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, "johndoe123:p@ss123", user.getAuthorities());
        SecurityContext sc = new SecurityContext(authenticationToken);

        Assertions.assertEquals("johndoe123", sc.getUserDetails().getUsername());
        Assertions.assertEquals("p@ss123", sc.getUserDetails().getPassword());

        Assertions.assertEquals(authenticationToken, sc.getPrincipal());
        Assertions.assertTrue(sc.getAuthentication().isAuthenticated());
    }

    @Test
    void testAnonymousAuthenticationToken() {
        UserDetails user = User.withUsername("johndoe123").password("p@ss123").authorities("USER").build();
        AnonymousAuthenticationToken authenticationToken = new AnonymousAuthenticationToken(UUID.randomUUID().toString(), user, user.getAuthorities());
        SecurityContext sc = new SecurityContext(authenticationToken);

        Assertions.assertEquals("johndoe123", sc.getUserDetails().getUsername());
        Assertions.assertEquals("p@ss123", sc.getUserDetails().getPassword());

        Assertions.assertEquals(authenticationToken, sc.getPrincipal());
        Assertions.assertTrue(sc.getAuthentication().isAuthenticated());
    }

    @Test
    void testRememberMeAuthenticationToken() {
        UserDetails user = User.withUsername("johndoe123").password("p@ss123").authorities("USER").build();
        RememberMeAuthenticationToken authenticationToken = new RememberMeAuthenticationToken(UUID.randomUUID().toString(), user, user.getAuthorities());
        SecurityContext sc = new SecurityContext(authenticationToken);

        Assertions.assertEquals("johndoe123", sc.getUserDetails().getUsername());
        Assertions.assertEquals("p@ss123", sc.getUserDetails().getPassword());

        Assertions.assertEquals(authenticationToken, sc.getPrincipal());
        Assertions.assertTrue(sc.getAuthentication().isAuthenticated());
    }

    @Test
    void testPreAuthenticatedAuthenticationToken() {
        UserDetails user = User.withUsername("johndoe123").password("p@ss123").authorities("USER").build();
        PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(user, "johndoe123:p@ss123", user.getAuthorities());
        SecurityContext sc = new SecurityContext(authenticationToken);

        Assertions.assertEquals("johndoe123", sc.getUserDetails().getUsername());
        Assertions.assertEquals("p@ss123", sc.getUserDetails().getPassword());

        Assertions.assertEquals(authenticationToken, sc.getPrincipal());
        Assertions.assertTrue(sc.getAuthentication().isAuthenticated());
    }


}
