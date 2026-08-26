package com.omp.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

/**
 * Emission/verification des JWT de l'API REST (cf plan decision d'architecture #1 : JWT confine
 * a omp-api, omp-web utilise la session container-managed via OmpIdentityStore).
 *
 * La cle de signature DOIT etre fournie via la variable d'environnement OMP_JWT_SECRET en
 * production (docker-compose.yml) ; une valeur de secours est generee au demarrage pour le dev
 * local uniquement (invalide un token existant a chaque redemarrage).
 */
@Singleton
@Startup
public class JwtUtil {

    private static final Duration TOKEN_TTL = Duration.ofHours(8);
    private static final String ROLES_CLAIM = "roles";

    private SecretKey signingKey;

    @PostConstruct
    void init() {
        String secret = System.getenv("OMP_JWT_SECRET");
        this.signingKey = (secret != null && secret.length() >= 32)
                ? Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))
                : Jwts.SIG.HS256.key().build();
    }

    public String issueToken(String username, Set<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim(ROLES_CLAIM, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(TOKEN_TTL)))
                .signWith(signingKey)
                .compact();
    }

    public Instant expirationFor(String token) {
        return parse(token).getExpiration().toInstant();
    }

    public String subjectOf(String token) {
        return parse(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public Set<String> rolesOf(String token) {
        List<String> roles = parse(token).get(ROLES_CLAIM, List.class);
        return roles == null ? Set.of() : roles.stream().collect(Collectors.toSet());
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
