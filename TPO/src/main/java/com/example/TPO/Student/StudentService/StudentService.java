package com.example.TPO.Student.StudentService;

import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationMapper;
import com.example.TPO.Student.StudentDTO.StudentDTO;
import com.example.TPO.Student.StudentDTO.StudentMapper;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class StudentService {
@Autowired
StudentRepository studentRepository;
    @Autowired
    UserRepo userRepo;
    @Autowired
    JWTService jwtService;
    public void createstudent(Student student, String authtoken, MultipartFile prof_img, MultipartFile resume) throws IOException {
        Long userId = jwtService.extractUserId(authtoken);
        Optional<User> userOptional = userRepo.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with the provided token.");
        }

        User user = userOptional.get();

        // Check if the user is already associated with a student
        if (studentRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("User is already associated with a student.");
        }

        // Validate files
        if (prof_img.isEmpty() || resume.isEmpty()) {
            throw new RuntimeException("Profile image or resume cannot be empty.");
        }

        if (!prof_img.getContentType().startsWith("image/")) {
            throw new RuntimeException("Invalid file type for profile image.");
        }

        if (!resume.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Resume must be a PDF file.");
        }

        // Set profile image data
        student.setProfileimagedata(prof_img.getBytes());
        student.setImagename(prof_img.getOriginalFilename());
        student.setImagetype(prof_img.getContentType());

        // Set resume data
        student.setResumename(student.getFirstName() + "'s Resume");
        student.setResume_file_data(resume.getBytes());  // ✅ Fixed syntax error

        // Associate user with the new student and save
        student.setUser(user);
        studentRepository.save(student);
    }


    public ResponseEntity<? extends Object> getStudent(long id) {
        Optional<Student> studentoptional = studentRepository.findById(id);

        if (studentoptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Student Found with ID: " + id);
        }

        Student student = studentoptional.get();
        StudentDTO StudentDTO = StudentMapper.toStudentDTO(student);

        return ResponseEntity.ok(StudentDTO);


    }
    public ResponseEntity<? extends Object> getstudprofile(String token) {
        long id=jwtService.extractUserId(token);
        Optional<Student> studentoptional = studentRepository.findByUserId(id);

        if (studentoptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Student Found with ID: " + id);
        }

        Student student = studentoptional.get();
        StudentDTO StudentDTO = StudentMapper.toStudentDTO(student);

        return ResponseEntity.ok(StudentDTO);


    }

        public String updateStudent(Student student, String authToken) {
            Optional<User> userOptional = userRepo.findById(jwtService.extractUserId(authToken));
            Optional<Student> existingStudentOpt = studentRepository.findByUserId(userOptional.get().getId());

            if (existingStudentOpt.isEmpty()) {
                return "Student not found.";
            }

            Student existingStudent = existingStudentOpt.get();

            // Update only non-null fields
            if (student.getFirstName() != null) existingStudent.setFirstName(student.getFirstName());
            if (student.getMiddleName() != null) existingStudent.setMiddleName(student.getMiddleName());
            if (student.getLastName() != null) existingStudent.setLastName(student.getLastName());
            if (student.getDateOfBirth() != null) existingStudent.setDateOfBirth(student.getDateOfBirth());
            if (student.getGender() != null) existingStudent.setGender(student.getGender());
            if (student.getPhoneNumber() != null) existingStudent.setPhoneNumber(student.getPhoneNumber());
            if (student.getAddress() != null) existingStudent.setAddress(student.getAddress());
            if (student.getDepartment() != null) existingStudent.setDepartment(student.getDepartment());
            if (student.getSscMarks() != 0) existingStudent.setSscMarks(student.getSscMarks());
            if (student.getHscMarks() != 0) existingStudent.setHscMarks(student.getHscMarks());
            if (student.getDiplomaMarks() != 0) existingStudent.setDiplomaMarks(student.getDiplomaMarks());
            if (student.getSem1Marks() != 0) existingStudent.setSem1Marks(student.getSem1Marks());
            if (student.getSem2Marks() != 0) existingStudent.setSem2Marks(student.getSem2Marks());
            if (student.getSem3Marks() != 0) existingStudent.setSem3Marks(student.getSem3Marks());
            if (student.getSem4Marks() != 0) existingStudent.setSem4Marks(student.getSem4Marks());
            if (student.getSem5Marks() != 0) existingStudent.setSem5Marks(student.getSem5Marks());
            if (student.getSem6Marks() != 0) existingStudent.setSem6Marks(student.getSem6Marks());
            if (student.getNoOfBacklogs() >=0) existingStudent.setNoOfBacklogs(student.getNoOfBacklogs());

            studentRepository.save(existingStudent);
            return "Student updated successfully.";
        }

    public void getStudprofile(String token) {
    }
}



