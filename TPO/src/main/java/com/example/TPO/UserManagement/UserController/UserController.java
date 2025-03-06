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

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
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
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        System.out.println(user.getUsername());

        Optional<User> existingUserOpt = Userrepo.findByEmail(user.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (existingUser.isVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists and is verified!");
            } else {
                // User exists but is NOT verified → Resend OTP
                String otp = otpService.generateOTP(existingUser.getEmail());
                emailService.sendOTPEmail(existingUser.getEmail(), otp);

                return ResponseEntity.ok("User exists but not verified. OTP sent for verification.");
            }
        }

        // Encrypt password before saving
        user.setPassword(encoder.encode(user.getPassword()));
        user.setVerified(false);
        Userrepo.save(user);

        // Generate and send OTP for new user
        String otp = otpService.generateOTP(user.getEmail());
        emailService.sendOTPEmail(user.getEmail(), otp);

        return ResponseEntity.ok("OTP sent to email for verification.");
    }
    @PostMapping("verify/otp")
    public ResponseEntity<?> verify_otp(@RequestBody Map<String,String> reqbody){
        String Email=reqbody.get("email");
        String Otp=reqbody.get("otp");


        Optional<User> optuser= Userrepo.findByEmail(Email);
        if(!optuser.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if(optuser.get().isVerified()){
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User is Verfied");
        }
        if(otpService.validateOTP(Email,Otp)){
             User user= optuser.get();
             user.setVerified(true);
             Userrepo.save(user);
            return ResponseEntity.ok("User Verified ");
        }
        else {
            return ResponseEntity.ok("Incorrect OTP");
        }

    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/stud/login")
    public ResponseEntity<?> studlogin(@RequestBody User user){
       service.verify(user);

    return service.verify(user);


    }
    @PostMapping("/tpo/login")
    public ResponseEntity<?> tpologin(@RequestBody User user){
        service.verify(user);

        return service.verify(user);


    }

    @CrossOrigin(origins = "http://localhost:4200")
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
