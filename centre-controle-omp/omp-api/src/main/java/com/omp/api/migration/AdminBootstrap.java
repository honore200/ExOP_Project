package com.omp.api.migration;

import com.omp.common.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Cree un compte ADMIN par defaut si la base ne contient encore aucun utilisateur (premier
 * demarrage). Le mot de passe est genere via UserService (Pbkdf2PasswordHash CDI standard) -
 * jamais de hash fabrique a la main dans un script SQL.
 *
 * OMP_ADMIN_USERNAME / OMP_ADMIN_PASSWORD permettent de fixer les identifiants en prod ; a
 * defaut, des valeurs de secours sont utilisees et un avertissement est logue (a changer
 * immediatement apres le premier login, cf docs/rbac-roles.md).
 */
@Singleton
@Startup
@DependsOn("FlywayMigrationStartup")
public class AdminBootstrap {

    private static final Logger LOG = Logger.getLogger(AdminBootstrap.class.getName());

    @Inject
    private UserService userService;

    @PostConstruct
    void bootstrap() {
        if (!userService.findAll().isEmpty()) {
            return;
        }

        String username = System.getenv().getOrDefault("OMP_ADMIN_USERNAME", "admin");
        String password = System.getenv().getOrDefault("OMP_ADMIN_PASSWORD", "changeme123!");

        userService.createUser(username, "Administrateur", password, Set.of("ADMIN"));
        LOG.warning(() -> "Compte ADMIN par defaut cree (" + username
                + "). Changez le mot de passe immediatement si OMP_ADMIN_PASSWORD n'a pas ete fixe.");
    }
}
