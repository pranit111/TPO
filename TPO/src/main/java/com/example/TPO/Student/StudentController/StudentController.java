package com.example.TPO.Student.StudentController;

import com.example.TPO.DBMS.Filters.JobApplicationFilter;
import com.example.TPO.Student.StudentDTO.StudentBasicDTO;
import com.example.TPO.Student.StudentService.StudentService;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
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
    @GetMapping("/tpo/Student/profile/{stud_id}")
    public ResponseEntity<?> getprofilefortpo(@RequestHeader("Authorization") String authHeader,@PathVariable long stud_id){
        Map<String, Object> response = new HashMap<>();
        if(authHeader.isEmpty()){
            response.put("error","User Not Logged In");
            new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);}
        String token = authHeader.substring(7);


        return studentService.getstudprofiletpo(token,stud_id);

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
            @RequestPart MultipartFile resume,
            @RequestPart MultipartFile ssc_result,
            @RequestPart(required = false) MultipartFile hsc_result,
            @RequestPart(required = false) MultipartFile diploma_result


    ) throws IOException {

        Map<String, Object> response = new HashMap<>();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // Convert studentData from JSON string to Student object
            Student student = objectMapper.readValue(studentData, Student.class);

            studentService.createstudent(student, token, prof_img, resume,ssc_result,hsc_result,diploma_result);

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
            @RequestPart Student student,
            @RequestHeader("Authorization") String authHeader,@RequestPart( required = false) MultipartFile profile_img,@RequestPart( required = false) MultipartFile resume) throws IOException {

        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Authorization header missing or invalid");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        String updateResponse = studentService.updateStudent(student, token,profile_img,resume);

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
    }@GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long id) {
        Optional<Student> optional = studentRepository.findById(id);
        if (optional.isEmpty() || optional.get().getResume_file_data() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = optional.get().getResume_file_data();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("document.pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
    @GetMapping("/pdf/results/{id}/{res}")
    public ResponseEntity<byte[]> getResultPdf(@PathVariable Long id, @PathVariable String res) {
        Optional<Student> optional = studentRepository.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = optional.get();
        byte[] pdfBytes = null;

        switch (res.toLowerCase()) {
            case "ssc":
                pdfBytes = student.getSsc_result();
                break;
            case "hsc":
                pdfBytes = student.getHsc_result();
                break;
            case "diploma":
                pdfBytes = student.getDiploma_result();
                break;
            default:
                return ResponseEntity.badRequest().body(null); // Invalid res path variable
        }

        if (pdfBytes == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename(res + "_result.pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PutMapping("/tpo/Student/verify-results/{studentId}")
    public ResponseEntity<?> verifyStudentResults(
            @PathVariable Long studentId,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam boolean verified,
            @RequestParam(required = false) String remarks) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Authorization header missing or invalid");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7);
        
        try {
            String result = studentService.verifyStudentResults(studentId, token, verified, remarks);
            
            if (result.equals("Unauthorized")) {
                response.put("status", "error");
                response.put("message", "Only TPO users can verify student results");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            } else if (result.equals("Student not found")) {
                response.put("status", "error");
                response.put("message", "Student not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (result.equals("Invalid token")) {
                response.put("status", "error");
                response.put("message", "Invalid authorization token");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            
            response.put("status", "success");
            response.put("message", verified ? "Student results verified successfully" : "Student results marked as unverified");
            response.put("studentId", studentId);
            response.put("verified", verified);
            if (remarks != null && !remarks.trim().isEmpty()) {
                response.put("remarks", remarks);
            }
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error occurred while verifying student results: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tpo/Student/verification-status/{studentId}")
    public ResponseEntity<?> getStudentVerificationStatus(
            @PathVariable Long studentId,
            @RequestHeader("Authorization") String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Authorization header missing or invalid");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7);
        
        try {
            Map<String, Object> verificationStatus = studentService.getStudentVerificationStatus(studentId, token);
            
            if (verificationStatus.containsKey("error")) {
                String error = (String) verificationStatus.get("error");
                if (error.equals("Unauthorized")) {
                    return new ResponseEntity<>(verificationStatus, HttpStatus.FORBIDDEN);
                } else if (error.equals("Student not found")) {
                    return new ResponseEntity<>(verificationStatus, HttpStatus.NOT_FOUND);
                } else if (error.equals("Invalid token")) {
                    return new ResponseEntity<>(verificationStatus, HttpStatus.UNAUTHORIZED);
                }
            }
            
            return new ResponseEntity<>(verificationStatus, HttpStatus.OK);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error occurred while getting verification status: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
