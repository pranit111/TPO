package com.example.TPO.JobApplication.JobApplicationRepository;

import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Applications.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {
    boolean existsByStudentIdAndJobPostId(Long id, long postId);
    @Query("SELECT ja FROM JobApplication ja WHERE (:status IS NULL OR ja.status = :status) " +
            "AND (:location IS NULL OR ja.jobPost.location = :location) " +
            "AND (:minSalary IS NULL OR ja.jobPost.packageAmount >= :minSalary) " +
            "AND (:maxSalary IS NULL OR ja.jobPost.packageAmount <= :maxSalary)")
    List<JobApplication> filterJobApplications(@Param("status") ApplicationStatus status,
                                               @Param("location") String location,
                                               @Param("minSalary") Long minSalary,
                                               @Param("maxSalary") Long maxSalary);
}
