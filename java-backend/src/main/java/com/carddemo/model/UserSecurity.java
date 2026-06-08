package com.carddemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Maps to COBOL copybook CSUSR01Y.cpy — SEC-USER-DATA (80-byte VSAM KSDS).
 * Stores user credentials for authentication.
 *
 * COBOL layout:
 *   SEC-USR-ID     PIC X(08)
 *   SEC-USR-FNAME  PIC X(20)
 *   SEC-USR-LNAME  PIC X(20)
 *   SEC-USR-PWD    PIC X(08)  — migrated to hashed password
 *   SEC-USR-TYPE   PIC X(01)  — 'A' = admin, 'U' = user
 *
 * NOTE: The original COBOL stored passwords in plain text.
 * This Java implementation stores hashed passwords using BCrypt.
 */
@Entity
@Table(name = "users")
public class UserSecurity {

    @Id
    @Column(name = "user_id", length = 8)
    private String userId;

    @Column(name = "first_name", length = 20)
    private String firstName;

    @Column(name = "last_name", length = 20)
    private String lastName;

    @Column(name = "password", length = 72, nullable = false)
    private String password;

    @Column(name = "user_type", length = 1, nullable = false)
    private String userType;

    public UserSecurity() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public boolean isAdmin() {
        return "A".equalsIgnoreCase(userType);
    }
}
