package com.example.TPO.Tpo.TpoController;

import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import com.example.TPO.Tpo.TpoService.TpoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api2/")
@RestController

public class TpoController {
    @Autowired
    TpoService tpoService;
    @PostMapping("register/TPO_USER")
    public ResponseEntity<String> Create_Tpo_user_(@RequestBody String role, @RequestHeader("Authorization") String authHeader){
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String response =tpoService.createTpoUser(role, token); // Pass token instead of UserDetails
            return ResponseEntity.ok(response);
        }
        String response="Error Occurred";
        return ResponseEntity.ok(response);

    }
}
