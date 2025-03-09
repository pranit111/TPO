package com.example.TPO.Student.StudentController;

import com.example.TPO.Student.StudentService.StudentService;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.DBMS.stud.Student;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("api1/")
@RestController


public class StudentController {
    @Autowired
    JWTService jwtService;
    @Autowired
    StudentService studentService;
    @Autowired
    StudentRepository studentRepository;
    @GetMapping("/Student/{id}")
    public ResponseEntity<?> getstud(@PathVariable long id ){

    return studentService.getStudent(id);

    }
    @GetMapping("/Student/profile")
    public ResponseEntity<?> getprofile(@RequestHeader("Authorization") String authHeader){
        Map<String, Object> response = new HashMap<>();
        if(authHeader.isEmpty()){
            response.put("error","User Not Logged In");
            new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);}
        String token = authHeader.substring(7);


        return studentService.getstudprofile(token);

    }

    private final ObjectMapper objectMapper;
    public StudentController(StudentService studentService, ObjectMapper objectMapper) {
        this.studentService = studentService;
        this.objectMapper = objectMapper;
    }
    @PostMapping("/Student")
    public ResponseEntity<?> createstud(
            @RequestPart(value = "student") String studentData,
            @RequestHeader("Authorization") String authHeader,
            @RequestPart MultipartFile prof_img,
            @RequestPart MultipartFile resume
    ) throws IOException {

        Map<String, Object> response = new HashMap<>();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // Convert studentData from JSON string to Student object
            Student student = objectMapper.readValue(studentData, Student.class);

            studentService.createstudent(student, token, prof_img, resume);

            response.put("status", "success");
            response.put("message", "Student saved successfully");
            response.put("student", student); // Optional: return saved student details
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.put("status", "error");
        response.put("message", "Authorization header missing or invalid");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
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
//    @GetMapping("/GetProfile")
//    public ResponseEntity<?> getprofile(@RequestHeader("Authorization") String authToken){
//        String token = authToken.substring(7);
//        studentService.getStudprofile(token);
//    }

}
