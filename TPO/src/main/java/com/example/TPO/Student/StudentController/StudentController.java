package com.example.TPO.Student.StudentController;

import com.example.TPO.DBMS.Filters.JobApplicationFilter;
import com.example.TPO.Student.StudentDTO.StudentBasicDTO;
import com.example.TPO.Student.StudentService.StudentService;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    @GetMapping("/Students/")
    public ResponseEntity<?> getstuds(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return studentService.getStudents(token);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR OCCURRED");
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
    @PutMapping("/Student")
    public ResponseEntity<?> updateStudent(
            @RequestBody Student student,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Authorization header missing or invalid");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        String updateResponse = studentService.updateStudent(student, token);

        if (updateResponse.equals("Student not found.")) {
            response.put("status", "error");
            response.put("message", updateResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("status", "success");
        response.put("message", "Student updated successfully");
        response.put("student", student); // Optional: return updated student details
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/Student/Search")
    public Page<StudentBasicDTO> searchStudents(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Double minAvgMarks,
            @RequestParam(required = false) Double maxAvgMarks,
            @RequestParam(required = false) Integer yearOfPassing,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return studentService.searchStudents(firstName, department,academicYear, minAvgMarks, maxAvgMarks, yearOfPassing, page, size);
    }
    @PostMapping("/Student/Search/Download")
    public ResponseEntity<?> downloadfilteredstud( @RequestParam(required = false) String firstName,
                                                   @RequestParam(required = false) String department,
                                                   @RequestParam(required = false) String academicYear,
                                                   @RequestParam(required = false) Double minAvgMarks,
                                                   @RequestParam(required = false) Double maxAvgMarks,
                                                   @RequestParam(required = false) Integer yearOfPassing,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        return studentService.downloadExcel(firstName, department,academicYear, minAvgMarks, maxAvgMarks, yearOfPassing, page, size);
    }
}
