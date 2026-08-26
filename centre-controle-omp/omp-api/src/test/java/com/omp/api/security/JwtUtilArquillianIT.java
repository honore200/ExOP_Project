package com.omp.api.security;

import jakarta.inject.Inject;
import java.util.Set;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test d'integration reel (deploiement sur une VRAIE instance WildFly, cf pom.xml profil
 * "arquillian" + src/test/resources/arquillian.xml) - verifie que @Singleton @Startup JwtUtil
 * s'initialise correctement dans le conteneur EJB et que le round-trip emission/verification JWT
 * fonctionne en conditions reelles. Complementaire aux tests Mockito (qui ne valident jamais le
 * cycle de vie EJB/CDI reel) et aux IT Testcontainers de omp-common (qui valident la persistence,
 * pas le conteneur EJB) - cf plan Phase 7.
 *
 * N'est PAS execute par `mvn test`/`mvn verify` standard : necessite `mvn verify -Parquillian`
 * avec JBOSS_HOME defini.
 */
@ExtendWith(ArquillianExtension.class)
class JwtUtilArquillianIT {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "jwt-util-test.war")
                .addClasses(JwtUtil.class)
                .addAsLibraries(Maven.resolver()
                        .resolve("io.jsonwebtoken:jjwt-api:0.12.6",
                                "io.jsonwebtoken:jjwt-impl:0.12.6",
                                "io.jsonwebtoken:jjwt-jackson:0.12.6")
                        .withTransitivity()
                        .asFile())
                .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Inject
    private JwtUtil jwtUtil;

    @Test
    void issueAndParseToken_shouldRoundTripInRealContainer() {
        String token = jwtUtil.issueToken("agent.port", Set.of("PORT", "CONTROL_ROOM"));

        assertEquals("agent.port", jwtUtil.subjectOf(token));
        Set<String> roles = jwtUtil.rolesOf(token);
        assertTrue(roles.contains("PORT"));
        assertTrue(roles.contains("CONTROL_ROOM"));
        assertTrue(jwtUtil.expirationFor(token).isAfter(java.time.Instant.now()));
    }
}
