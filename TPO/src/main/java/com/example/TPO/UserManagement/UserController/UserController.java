package com.example.TPO.UserManagement.UserController;

import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import com.example.TPO.UserManagement.Service.EmailService;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.Service.OTPService;
import com.example.TPO.UserManagement.Service.Service;
import com.example.TPO.UserManagement.Service.TokenExtractor;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    TpoRepository tpoRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    AuthenticationManager manager;
    @Autowired
    private  PasswordEncoder encoder;
    @Autowired
    private TokenExtractor tokenExtractor;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register/user")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) throws MessagingException {

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
    public ResponseEntity<Map<String, Object>> studLogin(@RequestBody User user, HttpServletResponse httpResponse) {
        ResponseEntity<Map<String, Object>> response = service.verify(user);
        // Set JWT as HttpOnly cookie on successful login
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String token = (String) response.getBody().get("token");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Remove "Bearer " prefix
            }
            addJwtCookie(httpResponse, token);
            response.getBody().remove("token"); // Don't send token in body anymore
        }
        return response;
    }

    @PostMapping("/tpo/login")
    public ResponseEntity<Map<String, Object>> tpoLogin(@RequestBody User user, HttpServletResponse httpResponse) {
        ResponseEntity<Map<String, Object>> response = service.verify_tpo(user);
        // Set JWT as HttpOnly cookie on successful login
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String token = (String) response.getBody().get("token");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            addJwtCookie(httpResponse, token);
            response.getBody().remove("token"); // Don't send token in body anymore
        }
        return response;
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse httpResponse) {
        // Clear the JWT cookie by setting maxAge to 0
        ResponseCookie cookie = ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    private void addJwtCookie(HttpServletResponse response, String token) {
        // 7 days in seconds, matching JWT expiry
        long maxAgeSeconds = 7 * 24 * 60 * 60;
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)       // Not accessible via JavaScript
                .secure(true)         // Only sent over HTTPS (set to false for local HTTP dev)
                .path("/")            // Available to all endpoints
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")      // CSRF protection
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Helper method to generate TPO verification token
    private String generateTpoVerificationToken(String userId) {
        // You could use your existing jwtService or create a specialized token
        // This is a placeholder - implement your token generation logic here
        return "TPO_" + userId + "_" + System.currentTimeMillis();
    }


    @PostMapping("/forgot/password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> req) throws MessagingException {
        String email = req.get("email");
        Map<String, String> response = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            response.put("error", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }


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
        public Map<String, String> userdata(HttpServletRequest request) {
            String token = tokenExtractor.extractToken(request);

            if (token != null) {
                String username = jwtService.extractUser(token);
                return Map.of("username", username);
            }

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

    }
