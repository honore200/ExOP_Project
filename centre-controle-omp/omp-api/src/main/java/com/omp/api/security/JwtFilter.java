package com.omp.api.security;

import com.omp.common.security.CurrentUserContext;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.security.Principal;
import java.util.Set;

/**
 * Verifie le JWT (Authorization: Bearer ...) sur toute ressource annotee @Secured et alimente le
 * SecurityContext pour que @RolesAllowed fonctionne nativement (RESTEasy/WildFly).
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private CurrentUserContext currentUserContext;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortUnauthorized(requestContext);
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            String username = jwtUtil.subjectOf(token);
            Set<String> roles = jwtUtil.rolesOf(token);
            requestContext.setSecurityContext(new JwtSecurityContext(username, roles));
            currentUserContext.setUsername(username);
        } catch (RuntimeException e) {
            abortUnauthorized(requestContext);
        }
    }

    private void abortUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    private record JwtSecurityContext(String username, Set<String> roles) implements SecurityContext {

        @Override
        public Principal getUserPrincipal() {
            return () -> username;
        }

        @Override
        public boolean isUserInRole(String role) {
            return roles.contains(role);
        }

        @Override
        public boolean isSecure() {
            return true;
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }
}
