package com.example.TPO.Tpo.TpoService;

import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.DBMS.Tpo.TPO_Role;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@Service
public class TpoService{
    @Autowired
    UserRepo userRepo;
    @Autowired
    TpoRepository tpoRepository;
    @Autowired
    JWTService jwtService;
    public String  createTpoUser(String role , @RequestHeader("Authorization") String authHeader){
        {   System.err.println(authHeader);
            TPOUser tpoUser=new TPOUser();
            Long userId = jwtService.extractUserId(authHeader);
            Optional<User> userOptional = userRepo.findById(userId);
            System.err.println(userOptional.get().getUsername());
            if (userOptional.isEmpty()) {
                return ("User not found with the provided token.");
            }

            User user = userOptional.get();

            // Check if the user is already associated with a student
            Optional<TPOUser> existingStudent = tpoRepository.findByUser(user);

            if (existingStudent.isPresent()) {
                return ("User is already associated with a TPO.");
            }

            // Associate user with the new student and save
            tpoUser.setUser(user);
            tpoUser.setRole(TPO_Role.TPO_USER);
            tpoRepository.save(tpoUser);
            return ("Tpo User Saved");
        }

    }
}
