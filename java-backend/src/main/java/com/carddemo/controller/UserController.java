package com.carddemo.controller;

import com.carddemo.config.JwtUtils;
import com.carddemo.model.UserSecurity;
import com.carddemo.service.UserService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for User/Security management.
 * Replaces CICS programs:
 *   CC00 — COSGN00C (Sign-On)
 *   CU00 — COUSR00C (User List)
 *   CU01 — COUSR01C (User Add)
 *   CU02 — COUSR02C (User Update)
 *   CU03 — COUSR03C (User Delete)
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public List<UserSecurity> listUsers() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserSecurity getUser(@PathVariable String userId) {
        return userService.findByUserId(userId);
    }

    @GetMapping("/by-type/{userType}")
    public List<UserSecurity> getUsersByType(@PathVariable String userType) {
        return userService.findByType(userType);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> authenticate(
            @RequestBody Map<String, String> credentials) {
        String userId = credentials.get("userId");
        String password = credentials.get("password");
        boolean authenticated = userService.authenticate(userId, password);

        if (authenticated) {
            UserSecurity user = userService.findByUserId(userId);
            String token = jwtUtils.generateToken(userId, user.getUserType());
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("authenticated", true);
            response.put("userId", userId);
            response.put("userType", user.getUserType());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("token", token);
            return ResponseEntity.ok(response);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("authenticated", false);
        response.put("userId", userId != null ? userId : "");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping
    public ResponseEntity<UserSecurity> createUser(@RequestBody UserSecurity user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }

    @PutMapping("/{userId}")
    public UserSecurity updateUser(@PathVariable String userId,
                                   @RequestBody UserSecurity user) {
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
