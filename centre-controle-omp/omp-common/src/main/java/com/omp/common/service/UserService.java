package com.omp.common.service;

import com.omp.common.entity.Role;
import com.omp.common.entity.User;
import com.omp.common.entity.UserRole;
import com.omp.common.repository.RoleRepository;
import com.omp.common.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean verifyPassword(User user, String rawPassword) {
        return user.isEnabled() && passwordHash.verify(rawPassword.toCharArray(), user.getPasswordHash());
    }

    public User createUser(String username, String fullName, String rawPassword, Set<String> roleCodes) {
        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setPasswordHash(passwordHash.generate(rawPassword.toCharArray()));
        user.setEnabled(true);
        userRepository.save(user);
        assignRoles(user, roleCodes);
        return user;
    }

    public void assignRoles(User user, Set<String> roleCodes) {
        for (String code : roleCodes) {
            Role role = roleRepository.findByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("Role inconnu: " + code));
            user.getUserRoles().add(new UserRole(user, role));
        }
    }

    public Set<String> roleCodesOf(User user) {
        return user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode())
                .collect(Collectors.toSet());
    }

    public void setEnabled(Long userId, boolean enabled) {
        userRepository.findById(userId).ifPresent(u -> u.setEnabled(enabled));
    }
}
