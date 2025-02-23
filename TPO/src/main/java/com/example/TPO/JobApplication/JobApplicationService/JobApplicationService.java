package com.example.TPO.JobApplication.JobApplicationService;

import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationMapper;
import com.example.TPO.JobApplication.JobApplicationRepository.JobApplicationRepository;
import com.example.TPO.JobPost.JobPostRepository.JobPostRepository;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import com.example.TPO.UserManagement.UserDTO.UserDTO;
import com.example.TPO.Student.StudentDTO.StudentDTO;
import com.example.TPO.Student.StudentDTO.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class JobApplicationService {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private JobPostRepository jobPostRepository;

    //  Create Job Application
    public ResponseEntity<String> createApplication(long postId, String token) {
        Long userId = jwtService.extractUserId(token);

        // Get User
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Get JobPost
        Optional<JobPost> jobPostOptional = jobPostRepository.findById(postId);
        if (jobPostOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Job Post found with ID: " + postId);
        }

        // Get Student Profile
        Optional<Student> studentOptional = studentRepository.findByUserId(userId);
        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student profile not found for User ID: " + userId);
        }

        // Check if application already exists for this user & job post
        boolean alreadyApplied = jobApplicationRepository.existsByStudentIdAndJobPostId(
                studentOptional.get().getId(), postId);
        if (alreadyApplied) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You have already applied for this job.");
        }

        // Create Job Application
        JobApplication jobApplication = new JobApplication();
        jobApplication.setApplicationDate(LocalDate.now());
        jobApplication.setStatus(ApplicationStatus.APPLIED);
        jobApplication.setStudent(studentOptional.get());
        jobApplication.setJobPost(jobPostOptional.get());
        jobApplicationRepository.save(jobApplication);

        return ResponseEntity.status(HttpStatus.CREATED).body("Application submitted successfully");
    }

    //  Get Job Application (Returns DTO)
    public ResponseEntity<?> getApplication(long applicationId) {
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository.findById(applicationId);

        if (jobApplicationOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Application Found with ID: " + applicationId);
        }

        JobApplication jobApplication = jobApplicationOptional.get();
        JobApplicationDTO jobApplicationDTO = JobApplicationMapper.toJobApplicationDTO(jobApplication);

        return ResponseEntity.ok(jobApplicationDTO);
    }
    public ResponseEntity<?> updateApplication(long applicationId, JobApplicationDTO updatedJobApplicationDTO) {
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository.findById(applicationId);
        System.err.println(updatedJobApplicationDTO.getStatus());
        if (jobApplicationOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Application Found with ID: " + applicationId);
        }

        JobApplication jobApplication = jobApplicationOptional.get();

        // ✅ Update Status
        if (updatedJobApplicationDTO.getStatus() != null) {

            jobApplication.setStatus(ApplicationStatus.valueOf(updatedJobApplicationDTO.getStatus()));
        }

        // ✅ Update Application Date (if provided)
        if (updatedJobApplicationDTO.getApplicationDate() != null) {
            jobApplication.setApplicationDate(updatedJobApplicationDTO.getApplicationDate());
        }

        // ✅ Update Job Post (if changed)
        if (updatedJobApplicationDTO.getJobPost() != null) {
            Optional<JobPost> jobPostOptional = jobPostRepository.findById(updatedJobApplicationDTO.getJobPost().getId());
            if (jobPostOptional.isPresent()) {
                jobApplication.setJobPost(jobPostOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job Post not found with ID: " + updatedJobApplicationDTO.getJobPost().getId());
            }
        }

        // ✅ Save the updated application
        jobApplicationRepository.save(jobApplication);

        // ✅ Convert updated application to DTO
        JobApplicationDTO updatedDTO = JobApplicationMapper.toJobApplicationDTO(jobApplication);
        return ResponseEntity.ok(updatedDTO);
    }

}
