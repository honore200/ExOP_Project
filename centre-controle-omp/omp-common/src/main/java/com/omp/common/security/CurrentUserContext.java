package com.omp.common.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import java.security.Principal;

/**
 * Propage l'identite de l'utilisateur courant a travers les couches, quelle que soit la voie
 * d'entree :
 *  - omp-web (session container-managed Jakarta Security) : resolue automatiquement via
 *    jakarta.security.enterprise.SecurityContext (peuple par OmpIdentityStore).
 *  - omp-api (JWT, cf plan decision d'architecture #1) : positionnee explicitement par JwtFilter,
 *    car aucune authentification container-managed n'a lieu sur ce module.
 *
 * @RequestScoped : une instance par requete HTTP, visible de bout en bout (filtre -> ressource ->
 * EJB -> AuditInterceptor) puisque omp-web appelle les EJB en local dans la meme requete.
 */
@RequestScoped
public class CurrentUserContext {

    private String username;

    @Inject
    private Instance<SecurityContext> jakartaSecurityContext;

    public String getUsername() {
        if (username != null) {
            return username;
        }
        if (!jakartaSecurityContext.isUnsatisfied()) {
            Principal principal = jakartaSecurityContext.get().getCallerPrincipal();
            if (principal != null) {
                return principal.getName();
            }
        }
        return null;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
