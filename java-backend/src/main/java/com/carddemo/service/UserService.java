package com.carddemo.service;

import com.carddemo.exception.BusinessValidationException;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.model.UserSecurity;
import com.carddemo.repository.UserSecurityRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Replaces COBOL programs:
 *   COSGN00C.cbl (Sign-On — 260 LOC)      — user authentication
 *   COUSR00C.cbl (User List — 695 LOC)     — paginated user browse
 *   COUSR01C.cbl (User Add — 299 LOC)      — add new user
 *   COUSR02C.cbl (User Update — 414 LOC)   — update user record
 *   COUSR03C.cbl (User Delete — 359 LOC)   — delete user record
 *
 * The original COBOL stored passwords in plain text (PIC X(08)).
 * This implementation uses BCrypt hashing via Spring Security.
 */
@Service
public class UserService {

    private final UserSecurityRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserSecurityRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserSecurity> findAll() {
        return userRepository.findAll();
    }

    public UserSecurity findByUserId(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    public List<UserSecurity> findByType(String userType) {
        return userRepository.findByUserType(userType);
    }

    /**
     * Replicates COSGN00C — Sign-On validation.
     * The original COBOL read the USRSEC VSAM file by key (user ID),
     * then compared the password field. We use BCrypt matching instead.
     */
    public boolean authenticate(String userId, String rawPassword) {
        return userRepository.findById(userId)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    @Transactional
    public UserSecurity createUser(UserSecurity user) {
        if (user.getUserId() == null || user.getUserId().isBlank()) {
            throw new BusinessValidationException("User ID is required");
        }
        if (user.getUserId().length() > 8) {
            throw new BusinessValidationException(
                    "User ID cannot exceed 8 characters (COBOL PIC X(08))");
        }
        if (userRepository.existsById(user.getUserId())) {
            throw new BusinessValidationException(
                    "User already exists: " + user.getUserId());
        }
        validateUserType(user.getUserType());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public UserSecurity updateUser(String userId, UserSecurity updated) {
        UserSecurity existing = findByUserId(userId);

        if (updated.getFirstName() != null) {
            existing.setFirstName(updated.getFirstName());
        }
        if (updated.getLastName() != null) {
            existing.setLastName(updated.getLastName());
        }
        if (updated.getUserType() != null) {
            validateUserType(updated.getUserType());
            existing.setUserType(updated.getUserType());
        }
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        }
        return userRepository.save(existing);
    }

    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        userRepository.deleteById(userId);
    }

    private void validateUserType(String userType) {
        if (userType == null || (!"A".equals(userType) && !"U".equals(userType))) {
            throw new BusinessValidationException(
                    "User type must be 'A' (admin) or 'U' (user)");
        }
    }
}
