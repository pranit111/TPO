package com.example.TPO.JobPost.JobPostRepository;

import com.example.TPO.DBMS.JobPost.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostRepository extends JpaRepository<JobPost,Long> {


    @Query("SELECT jp FROM JobPost jp " +
            "JOIN jp.company c " +  // Join to access company details
            "WHERE (:status IS NULL OR jp.Status = :status) " +
            "AND (:company IS NULL OR c.name LIKE %:company%) " +
            "AND (:position IS NULL OR jp.jobDesignation LIKE %:position%) " +
            "AND (:jobType IS NULL OR jp.jobType = :jobType) " +
            "AND (:minSalary IS NULL OR jp.packageAmount >= :minSalary) " +
            "AND (:maxSalary IS NULL OR jp.packageAmount <= :maxSalary)")
    Page<JobPost> searchJobApplications(
            @Param("status") String status,
            @Param("company") String company,
            @Param("position") String position,
            @Param("jobType") String jobType,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            Pageable pageable);

}
