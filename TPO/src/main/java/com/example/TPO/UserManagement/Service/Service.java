package com.example.TPO.UserManagement.Service;

import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service implements UserDetailsService {
    private final UserRepo userRepo;
    private final AuthenticationConfiguration authenticationConfiguration;

    // Constructor-based dependency injection
    public Service(UserRepo userRepo, AuthenticationConfiguration authenticationConfiguration) {
        this.userRepo = userRepo;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User user = optionalUser.get();

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())  // Use email instead of username
                .password(user.getPassword())  // Ensure this is the hashed password
                .roles(user.getRole().toString())  // Ensure roles are correctly set
                .build();
    }


    @Autowired
    JWTService jwtService;

//    public String verify(User user) {
//        try {
//            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
//
//
//            // Authenticate using email and password
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
//            );
//            System.out.println(authentication);
//            if (authentication.isAuthenticated()) {
//                // Fetch user details after authentication
//                Optional<User> optionalUser = userRepo.findByEmail(user.getEmail());
//
//                if (optionalUser.isPresent()) {
//                    User dbUser = optionalUser.get();
//
//
//                    // Generate token using email and ID
//                    return jwtService.generateToken(dbUser.getEmail(), dbUser.getId());
//                } else {
//                    return "User not found in database after authentication";
//                }
//            }
//        } catch (Exception e) {
//            return "KEY GEN Failed: " + e.getMessage();
//        }
//        return "KEY GEN Failed";
//    }
//
//}
public ResponseEntity<?> verify(User user) {
    try {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        // Authenticate user with email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );

        if (authentication.isAuthenticated()) {
            // Fetch user details
            Optional<User> optionalUser = userRepo.findByEmail(user.getEmail());

            if (optionalUser.isPresent()) {
                User dbUser = optionalUser.get();

                // Generate JWT token
                String token = jwtService.generateToken(dbUser.getEmail(), dbUser.getId());

                // Create JSON response
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("status", "success");
                responseBody.put("message", "Login successful");
                responseBody.put("token", "Bearer " + token); // Add Bearer prefix
                responseBody.put("userId", dbUser.getId());
                responseBody.put("email", dbUser.getEmail());
                responseBody.put("role", dbUser.getRole());

                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", "User not found"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "message", "Authentication failed"));
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", "KEY GEN Failed: " + e.getMessage()));
    }
}}