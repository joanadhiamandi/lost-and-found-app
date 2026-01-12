package com.lostandfound.app.services;

import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppUserService {

    public enum RegistrationStatus {
        SUCCESS,
        USERNAME_TAKEN,
        EMAIL_TAKEN,
        FAILED
    }

    public static class RegistrationResult {
        private final RegistrationStatus status;
        private final AppUser user;

        public RegistrationResult(RegistrationStatus status, AppUser user) {
            this.status = status;
            this.user = user;
        }

        public RegistrationStatus getStatus() {
            return status;
        }

        public AppUser getUser() {
            return user;
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Read admin credentials from application.properties
    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:Admin123!}")
    private String adminPassword;

    @Value("${app.admin.email:admin@lostfound.local}")
    private String adminEmail;

    // ✅ Automatically create admin account on app startup
    @PostConstruct
    public void initializeAdmin() {
        try {
            AppUser existingAdmin = userRepository.findByUsername(adminUsername);

            if (existingAdmin == null) {
                // Create new admin user
                AppUser admin = new AppUser();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setEmail(adminEmail);
                admin.setFullName("System Administrator");
                admin.setRole("ADMIN");  // ✅ Set role to ADMIN
                admin.setIsActive(true);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());

                userRepository.save(admin);
                System.out.println("✅ Admin account created: " + adminUsername);
            } else {
                // Update existing user to ensure they have ADMIN role
                if (!"ADMIN".equals(existingAdmin.getRole())) {
                    existingAdmin.setRole("ADMIN");
                    existingAdmin.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(existingAdmin);
                    System.out.println("✅ Admin role assigned to existing user: " + adminUsername);
                } else {
                    System.out.println("✅ Admin user already exists: " + adminUsername);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing admin account: " + e.getMessage());
        }
    }

    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public AppUser getUserByUsername(String username) {
        return findByUsername(username);
    }

    public AppUser getUserByEmail(String email) {
        return findByEmail(email);
    }

    public boolean validatePassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public RegistrationResult registerUser(String username, String email, String fullName, String password) {
        try {
            // ✅ Prevent registering with admin username
            if (username.equalsIgnoreCase(adminUsername)) {
                return new RegistrationResult(RegistrationStatus.USERNAME_TAKEN, null);
            }

            if (userRepository.existsByUsername(username)) {
                return new RegistrationResult(RegistrationStatus.USERNAME_TAKEN, null);
            }

            if (userRepository.existsByEmail(email)) {
                return new RegistrationResult(RegistrationStatus.EMAIL_TAKEN, null);
            }

            AppUser newUser = new AppUser();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setRole("MEMBER");  // ✅ Regular users get MEMBER role
            newUser.setIsActive(true);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());

            AppUser saved = userRepository.save(newUser);
            return new RegistrationResult(RegistrationStatus.SUCCESS, saved);

        } catch (DataIntegrityViolationException e) {
            // If UNIQUE constraints still hit (race condition), treat as failed
            return new RegistrationResult(RegistrationStatus.FAILED, null);
        } catch (Exception e) {
            return new RegistrationResult(RegistrationStatus.FAILED, null);
        }
    }

    public AppUser authenticateUser(String username, String password) {
        AppUser user = findByUsername(username);

        if (user == null) return null;
        if (user.getIsActive() != null && !user.getIsActive()) return null;

        if (validatePassword(password, user.getPassword())) {
            return user;
        }

        return null;
    }
}
