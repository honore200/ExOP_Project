package com.omp.api.security;

import com.omp.common.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.SecurityContext;

/** Resout l'id User (omp-common) a partir du principal pose par JwtFilter dans le SecurityContext JAX-RS. */
@RequestScoped
public class CurrentUserResolver {

    @Inject
    private UserRepository userRepository;

    public Long resolveUserId(SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        return userRepository.findByUsername(username)
                .map(com.omp.common.entity.User::getId)
                .orElseThrow(() -> new IllegalStateException("Utilisateur authentifie introuvable: " + username));
    }
}
