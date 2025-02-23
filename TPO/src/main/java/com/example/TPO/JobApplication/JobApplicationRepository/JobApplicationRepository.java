package com.example.TPO.JobApplication.JobApplicationRepository;

import com.example.TPO.DBMS.Applications.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {
    boolean existsByStudentIdAndJobPostId(Long id, long postId);
}
