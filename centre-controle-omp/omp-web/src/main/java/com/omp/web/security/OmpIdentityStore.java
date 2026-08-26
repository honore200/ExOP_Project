package com.omp.web.security;

import com.omp.common.entity.User;
import com.omp.common.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.util.Optional;
import java.util.Set;

/**
 * IdentityStore Jakarta Security pour omp-web (session container-managed) - distinct du JWT de
 * omp-api (cf plan decision d'architecture #1). Hachage PBKDF2 via UserService (omp-common).
 */
@ApplicationScoped
public class OmpIdentityStore implements IdentityStore {

    @Inject
    private UserService userService;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (!(credential instanceof UsernamePasswordCredential)) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }
        UsernamePasswordCredential upCredential = (UsernamePasswordCredential) credential;

        Optional<User> userOpt = userService.findByUsername(upCredential.getCaller());
        if (userOpt.isEmpty()) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        User user = userOpt.get();
        if (!userService.verifyPassword(user, upCredential.getPasswordAsString())) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        Set<String> roles = userService.roleCodesOf(user);
        return new CredentialValidationResult(user.getUsername(), roles);
    }
}
