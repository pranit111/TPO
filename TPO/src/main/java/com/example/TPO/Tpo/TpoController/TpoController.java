package com.example.TPO.Tpo.TpoController;

import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.DBMS.Tpo.TPO_Role;
import com.example.TPO.Tpo.TPODTO.TPOUserDTO;
import com.example.TPO.Tpo.TPODTO.TPOUserMapper;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import com.example.TPO.Tpo.TpoService.TpoService;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.Service.Service;
import com.example.TPO.UserManagement.UserController.UserController;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("api2/")
@RestController

public class TpoController {
    @Autowired
    TpoService tpoService;
    @Autowired
    TpoRepository tpoRepository;
    @Autowired
    UserRepo userRepo;
    @Autowired
    JWTService jwtService;
@Autowired
TPOUserMapper tpoUserMapper;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    UserController userController;
    @PostMapping("register/TPO_USER")
    public ResponseEntity<Map<String,String>> Create_Tpo_user_(@RequestParam String role, @RequestBody User user, @RequestHeader("Authorization") String authHeader) {
        Map<String, String> response = new HashMap<>();
        System.err.println(role);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // Verify that the requesting user is a TPO admin
//            TPOUser admin = tpoRepository.findByUser(userRepo.findByUsername(jwtService.extractUser(token)).get()).get();

            if (true) {
                Optional<User> existingUserOpt = userRepo.findByEmail(user.getEmail());

                if (existingUserOpt.isPresent()) {
                    User existingUser = existingUserOpt.get();

                    if (existingUser.isVerified()) {
                        response.put("error", "User already exists and is verified!");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    } else {
                        // User exists but is NOT verified → Resend OTP
                        response.put("message", "User exists but not verified. OTP sent for verification.");
                        return ResponseEntity.ok(response);
                    }
                }

                // Encrypt password before saving
                user.setPassword(encoder.encode(user.getPassword()));
                user.setVerified(true);
                User savedUser = userRepo.save(user);

                // Create and save the TPO user with the specified role
                TPOUser tpoUser = new TPOUser();
                tpoUser.setUser(savedUser);

                // Convert string role to tpoRole enum
                try {
                    TPO_Role userRole = TPO_Role.valueOf(role.toUpperCase());
                    tpoUser.setRole(userRole);
                } catch (IllegalArgumentException e) {
                    // Default to a regular TPO role if the provided role is invalid
                    tpoUser.setRole(TPO_Role.TPO_USER);
                    response.put("warning", "Invalid role provided. Defaulted to TPO role.");
                }

                // Save the TPO user to the repository
                tpoRepository.save(tpoUser);

                response.put("message", "User created and TPO user role assigned successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Unauthorized. Only TPO Admins can create TPO users.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }

        response.put("error", "Invalid or missing authorization token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, String>> createInitialAdmin(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        // Check if admin already exists
        Optional<User> existing = userRepo.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            response.put("error", "Admin user already exists.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Set up user
        user.setPassword(encoder.encode(user.getPassword()));
        user.setVerified(true); // auto-verify
        User savedUser = userRepo.save(user);

        // Set up TPO role
        TPOUser tpoUser = new TPOUser();
        tpoUser.setUser(savedUser);
        tpoUser.setRole(TPO_Role.ADMIN); // set role as ADMIN
        tpoRepository.save(tpoUser);

        response.put("message", "Initial admin user created successfully.");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<TPOUser> user = tpoRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(tpoUserMapper.toDTO(user.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Long id, @RequestParam boolean status,@RequestBody User tpoUser) {
        System.err.println(status);
        return ResponseEntity.ok(tpoService.updateUser(id, status,tpoUser));
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "User deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
    }
    @GetMapping
    public ResponseEntity<List<TPOUserDTO>> getAllTpoUsers() {
        List<TPOUserDTO> tpoUserDTOs = tpoRepository.findAll()
                .stream()
                .map(TPOUserMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tpoUserDTOs);
    }
}
