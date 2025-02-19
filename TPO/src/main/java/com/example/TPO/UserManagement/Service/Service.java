package com.example.TPO.UserManagement.Service;

import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

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

    public String verify(User user) {
        try {
            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();


            // Authenticate using email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            System.out.println(authentication);
            if (authentication.isAuthenticated()) {
                // Fetch user details after authentication
                Optional<User> optionalUser = userRepo.findByEmail(user.getEmail());

                if (optionalUser.isPresent()) {
                    User dbUser = optionalUser.get();


                    // Generate token using email and ID
                    return jwtService.generateToken(dbUser.getEmail(), dbUser.getId());
                } else {
                    return "User not found in database after authentication";
                }
            }
        } catch (Exception e) {
            return "KEY GEN Failed: " + e.getMessage();
        }
        return "KEY GEN Failed";
    }

}