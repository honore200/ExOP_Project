package com.omp.web.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;

/**
 * Authentification par session container-managed (Jakarta Security) pour omp-web - distincte du
 * JWT de omp-api (cf plan decision d'architecture #1).
 */
@ApplicationScoped
@FormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/pages/login.xhtml",
                errorPage = "/pages/login.xhtml?error=1"
        )
)
public class SecurityConfig {
}
