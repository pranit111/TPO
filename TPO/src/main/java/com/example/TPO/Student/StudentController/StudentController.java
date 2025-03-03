package com.example.TPO.Student.StudentController;

import com.example.TPO.Student.StudentService.StudentService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.DBMS.stud.Student;

@RequestMapping("api1/")
@RestController


public class StudentController {

    @Autowired
    StudentService studentService;
    @Autowired
    StudentRepository studentRepository;
    @GetMapping("/Student/{id}")
    public Student getstud(@PathVariable long id ){

    return studentService.getStudent(id);

    }

    @PostMapping("/Student")
    public ResponseEntity<String> createstud(@RequestBody Student student,  @RequestHeader("Authorization") String authHeader){


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            studentService.createstudent(student, token); // Pass token instead of UserDetails
            return ResponseEntity.ok("Student Saved");
        }


        return ResponseEntity.internalServerError().body("NOT SAVED");

    }
    @PutMapping("/Updatestudent")
    public ResponseEntity<String> updateStudent(@RequestBody Student student, @RequestHeader("Authorization") String authToken) {
        String token = authToken.substring(7);
        String response = studentService.updateStudent(student, token);

        if (response.equals("Student not found.")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

}
