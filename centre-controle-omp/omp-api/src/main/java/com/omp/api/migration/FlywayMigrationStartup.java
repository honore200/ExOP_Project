package com.omp.api.migration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

/**
 * Applique les migrations Flyway au demarrage de l'application (cf plan decision d'architecture
 * #8) - c'est le mecanisme qui tourne reellement en Docker/prod. flyway-maven-plugin (pluginManagement
 * du pom parent) reste disponible pour un `mvn flyway:migrate` manuel en dev local.
 *
 * Emplacement par defaut des scripts : classpath:db/migration (empaquetes dans omp-api).
 * OMP_FLYWAY_LOCATIONS permet de pointer vers un volume monte (ex. filesystem:/opt/flyway/sql)
 * sans reconstruire le WAR.
 */
@Singleton
@Startup
public class FlywayMigrationStartup {

    private static final Logger LOG = Logger.getLogger(FlywayMigrationStartup.class.getName());
    private static final String DEFAULT_LOCATION = "classpath:db/migration";

    @Resource(lookup = "java:/OmpDS")
    private DataSource dataSource;

    @PostConstruct
    void migrate() {
        String locations = System.getenv().getOrDefault("OMP_FLYWAY_LOCATIONS", DEFAULT_LOCATION);
        LOG.info(() -> "Flyway: migration depuis " + locations);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .baselineOnMigrate(true)
                .load();

        try {
            var result = flyway.migrate();
            LOG.info(() -> "Flyway: " + result.migrationsExecuted + " migration(s) appliquee(s)");
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Flyway: echec des migrations au demarrage", e);
            throw e;
        }
    }
}
