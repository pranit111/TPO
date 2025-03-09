package com.example.TPO.UserManagement.UserController;

import com.example.TPO.UserManagement.Service.EmailService;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.Service.OTPService;
import com.example.TPO.UserManagement.Service.Service;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api0/auth/")
public class UserController {
    @Autowired
    UserRepo Userrepo;
    @Autowired
    JWTService jwtService;
    @Autowired
    Service service;
    @Autowired
    OTPService otpService;
    @Autowired
    EmailService emailService;
    @Autowired
    AuthenticationManager manager;
    @Autowired
    private  PasswordEncoder encoder;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register/user")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        System.out.println(user.getUsername());
        Map<String, String> response = new HashMap<>();

        Optional<User> existingUserOpt = Userrepo.findByEmail(user.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (existingUser.isVerified()) {
                response.put("error", "User already exists and is verified!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                // User exists but is NOT verified → Resend OTP
                String otp = otpService.generateOTP(existingUser.getEmail());
                emailService.sendOTPEmail(existingUser.getEmail(), otp);

                response.put("message", "User exists but not verified. OTP sent for verification.");
                return ResponseEntity.ok(response);
            }
        }

        // Encrypt password before saving
        user.setPassword(encoder.encode(user.getPassword()));
        user.setVerified(false);
        Userrepo.save(user);

        // Generate and send OTP for new user
        String otp = otpService.generateOTP(user.getEmail());
        emailService.sendOTPEmail(user.getEmail(), otp);

        response.put("message", "OTP sent to email for verification.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify/otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, String> reqBody) {
        String email = reqBody.get("email");
        String otp = reqBody.get("otp");
        Map<String, String> response = new HashMap<>();

        Optional<User> optUser = Userrepo.findByEmail(email);
        if (optUser.isEmpty()) {
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (optUser.get().isVerified()) {
            response.put("error", "User is already verified");
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(response);
        }

        if (otpService.validateOTP(email, otp)) {
            User user = optUser.get();
            user.setVerified(true);
            Userrepo.save(user);
            response.put("message", "User Verified");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Incorrect OTP");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/stud/login")
    public ResponseEntity<Map<String, Object>> studLogin(@RequestBody User user) {
        return service.verify(user);
    }

    @PostMapping("/tpo/login")
    public ResponseEntity<Map<String, Object>> tpoLogin(@RequestBody User user) {
        return service.verify(user);
    }


    @PostMapping("/forgot/password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        Map<String, String> response = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            response.put("error", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }

        System.err.println(email);
        Optional<User> optionalUser = Userrepo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            response.put("error", "User Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        String otp = otpService.generateOTP(optionalUser.get().getEmail());
        emailService.sendOTPEmailResetpass(optionalUser.get().getEmail(), otp);

        response.put("message", "OTP Sent for Validation");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change/password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newpassword");
        Map<String, String> response = new HashMap<>();

        // Validate input
        if (email == null || email.trim().isEmpty() ||
                otp == null || otp.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty()) {
            response.put("error", "Email, OTP, and New Password are required");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if user exists
        Optional<User> optionalUser = Userrepo.findByEmail(email);
        if (optionalUser.isEmpty()) {
            response.put("error", "User Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = optionalUser.get();

        // Verify OTP
        boolean isOtpValid = otpService.validateOTP(email, otp);
        if (!isOtpValid) {
            response.put("error", "Invalid or expired OTP");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Encrypt the new password before saving
        user.setPassword(encoder.encode(newPassword));
        Userrepo.save(user);

        response.put("message", "Password successfully updated");
        return ResponseEntity.ok(response);
    }

        @GetMapping("/getuser")
        public Map<String, String> userdata(@RequestHeader("Authorization") String header) {
            System.out.println(header);

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7).trim();
                String username = jwtService.extractUser(token);

                // Return JSON instead of plain text
                return Map.of("username", username);
            }

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

    }
