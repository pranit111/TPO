package com.example.TPO.UserManagement.UserController;

import com.example.TPO.UserManagement.Service.JWTService;
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
    AuthenticationManager manager;
    @Autowired
    private  PasswordEncoder encoder;
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register/user")
    public User registeruser(@RequestBody User user){
        user.setPassword(encoder.encode(user.getPassword()));
        System.err.println(user.getRole());
        return Userrepo.save(user);


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
