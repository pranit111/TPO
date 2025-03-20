package com.example.TPO.UserManagement.Service;

import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    @Autowired
    StudentRepository studentRepository;
    public ResponseEntity<Map<String, Object>> verify(User user) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

            // Authenticate user with email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            if (!authentication.isAuthenticated()) {
                responseBody.put("status", "error");
                responseBody.put("message", "Authentication failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            }

            // Fetch user details
            Optional<User> optionalUser = userRepo.findByEmail(user.getEmail());

            if (optionalUser.isEmpty()) {
                responseBody.put("status", "error");
                responseBody.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
            }

            User dbUser = optionalUser.get();
            if(!dbUser.isVerified()){        responseBody.put("status", "error");
                responseBody.put("message", "User not verified");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseBody);

            }
            // Generate JWT token
            String token = jwtService.generateToken(dbUser.getEmail(), dbUser.getId());
            Optional<Student> studentOptional = studentRepository.findByUserId(dbUser.getId());
            responseBody.put("HasProfile", studentOptional.isPresent() ? "Yes" : "No");

            // Create JSON response
            responseBody.put("status", "success");
            responseBody.put("name", "success");
            responseBody.put("message", "Login successful");
            responseBody.put("token", "Bearer " + token); // Add Bearer prefix
            responseBody.put("userId", dbUser.getId());
            responseBody.put("email", dbUser.getEmail());
            responseBody.put("role", dbUser.getRole());

            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException e) {
            responseBody.put("status", "error");
            responseBody.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);

        } catch (Exception e) {
            responseBody.put("status", "error");
            responseBody.put("message", "Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }}
