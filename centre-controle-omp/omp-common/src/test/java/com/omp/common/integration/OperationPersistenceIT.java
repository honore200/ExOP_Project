package com.omp.common.integration;

import com.omp.common.entity.Domain;
import com.omp.common.entity.Operation;
import com.omp.common.entity.OperationStep;
import com.omp.common.entity.OperationType;
import com.omp.common.entity.StepType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Valide contre une VRAIE instance MySQL 8 (Testcontainers) que les scripts Flyway reels
 * (omp-api/src/main/resources/db/migration - jamais dupliques ici, cf commentaire ci-dessous) et
 * le mapping JPA sont coherents : cle etrangere effectivement appliquee, cascade de suppression
 * operation -> operation_step. H2 ne reproduit pas fidelement MySQL 8 (JSON, contraintes) d'ou ce
 * choix (cf plan Phase 7).
 */
@Testcontainers
class OperationPersistenceIT {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.4"))
            .withDatabaseName("omp_it")
            .withUsername("omp")
            .withPassword("omp");

    static EntityManagerFactory emf;

    @BeforeAll
    static void migrateAndBootstrapEmf() {
        Flyway.configure()
                .dataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword())
                // Chemin relatif au basedir du module omp-common (working dir de Surefire/Failsafe)
                // vers les VRAIS scripts de omp-api - jamais de copie locale qui pourrait diverger.
                .locations("filesystem:../omp-api/src/main/resources/db/migration")
                .load()
                .migrate();

        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", mysql.getJdbcUrl());
        props.put("jakarta.persistence.jdbc.user", mysql.getUsername());
        props.put("jakarta.persistence.jdbc.password", mysql.getPassword());
        props.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
        emf = Persistence.createEntityManagerFactory("ompTestPU", props);
    }

    @AfterAll
    static void closeEmf() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    void operationDeletion_shouldCascadeToOperationStep() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Long operationId;

        try {
            tx.begin();

            Domain domain = new Domain();
            domain.setCode("IT_DOMAIN");
            domain.setName("Domaine de test IT");
            em.persist(domain);

            OperationType operationType = new OperationType();
            operationType.setDomain(domain);
            operationType.setCode("IT_OP_TYPE");
            operationType.setName("Type de test IT");
            em.persist(operationType);

            StepType stepType = new StepType();
            stepType.setDomain(domain);
            stepType.setCode("IT_STEP_TYPE");
            stepType.setName("Etape de test IT");
            em.persist(stepType);

            Operation operation = new Operation();
            operation.setOperationCode("IT-OP-001");
            operation.setOperationType(operationType);
            operation.setDomain(domain);
            operation.setStartDatetime(LocalDateTime.now());
            em.persist(operation);

            OperationStep step = new OperationStep();
            step.setOperation(operation);
            step.setStepType(stepType);
            step.setSequence(1);
            em.persist(step);

            tx.commit();
            operationId = operation.getId();
        } finally {
            em.close();
        }

        // Relit dans un EntityManager frais pour eviter tout effet de cache de premier niveau.
        EntityManager verifyEm = emf.createEntityManager();
        try {
            Operation reloaded = verifyEm.find(Operation.class, operationId);
            assertNotNull(reloaded);
            assertEquals("IT-OP-001", reloaded.getOperationCode());
            assertEquals(1, reloaded.getSteps().size());
        } finally {
            verifyEm.close();
        }

        // Supprime l'operation : le FK ON DELETE CASCADE (V2__operations_core.sql) doit emporter
        // l'OperationStep sans intervention JPA explicite.
        EntityManager deleteEm = emf.createEntityManager();
        try {
            deleteEm.getTransaction().begin();
            Operation toDelete = deleteEm.find(Operation.class, operationId);
            deleteEm.remove(toDelete);
            deleteEm.getTransaction().commit();
        } finally {
            deleteEm.close();
        }

        EntityManager finalEm = emf.createEntityManager();
        try {
            assertNull(finalEm.find(Operation.class, operationId));
            Long stepCount = finalEm.createQuery(
                            "SELECT COUNT(s) FROM OperationStep s WHERE s.operation.id = :id", Long.class)
                    .setParameter("id", operationId)
                    .getSingleResult();
            assertEquals(0L, stepCount);
        } finally {
            finalEm.close();
        }
    }
}
