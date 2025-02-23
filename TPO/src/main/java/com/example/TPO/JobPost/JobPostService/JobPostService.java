package com.example.TPO.JobPost.JobPostService;

import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.JobPost.JobPostDTO.JobPostDTO;
import com.example.TPO.JobPost.JobPostRepository.JobPostRepository;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class JobPostService {
    @Autowired
    JobPostRepository jobPostRepository;
    @Autowired
    TpoRepository tpoRepository;
    @Autowired
    JWTService jwtService;
    @Autowired
    UserRepo userRepo;
    public ResponseEntity<String> createPost(JobPost jobPost, String token) {
        try {
            // Extract user ID from JWT token
            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            // Fetch User, throw exception if not found
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Fetch TPOUser, handle case if not found
            TPOUser tpoUser = tpoRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("TPO User not found for this user"));

            // Set TPOUser in JobPost
            jobPost.setCreatedBy(tpoUser);

            // Save JobPost
            jobPostRepository.save(jobPost);

            // Return success response
            return ResponseEntity.ok("Job post created successfully");
        } catch (Exception e) {
            // Log the error (consider using a logger)
            System.err.println("Error creating job post: " + e.getMessage());

            // Return appropriate error response
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    public ResponseEntity<?> updateJobPost(long jobPostId, JobPostDTO jobPostDTO) {
        Optional<JobPost> jobPostOptional = jobPostRepository.findById(jobPostId);

        if (jobPostOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Job Post Found with ID: " + jobPostId);
        }

        JobPost jobPost = jobPostOptional.get();

        // Updating fields only if they are not null
        if (jobPostDTO.getJobDesignation() != null) {
            jobPost.setJobDesignation(jobPostDTO.getJobDesignation());
        }
        if (jobPostDTO.getDescription() != null) {
            jobPost.setDescription(jobPostDTO.getDescription());
        }
        if (jobPostDTO.getLocation() != null) {
            jobPost.setLocation(jobPostDTO.getLocation());
        }
        if (jobPostDTO.getPackageAmount() != 0) {
            jobPost.setPackageAmount(jobPostDTO.getPackageAmount());
        }


        jobPostRepository.save(jobPost);

        return ResponseEntity.ok("Job post updated successfully.");
    }
}
