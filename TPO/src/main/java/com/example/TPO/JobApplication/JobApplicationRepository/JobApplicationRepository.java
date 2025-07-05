package com.example.TPO.JobApplication.JobApplicationRepository;

import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Applications.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
    List<JobApplication> findByStudentId(Long studentId);
    @Query("SELECT ja FROM JobApplication ja " +
            "JOIN ja.student s " +
            "JOIN ja.jobPost jp " +
            "JOIN jp.company c " +
            "WHERE (:status IS NULL OR ja.status = :status) " +
            "AND (:location IS NULL OR jp.location = :location) " +
            "AND (:minSalary IS NULL OR jp.packageAmount >= :minSalary) " +
            "AND (:maxSalary IS NULL OR jp.packageAmount <= :maxSalary) " +
            "AND (:studentName IS NULL OR LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :studentName, '%'))) " +
            "AND (:department IS NULL OR LOWER(s.department) LIKE LOWER(CONCAT('%', :department, '%'))) " +
            "AND (:jobType IS NULL OR LOWER(jp.jobType) LIKE LOWER(CONCAT('%', :jobType, '%'))) " +
            "AND (:jobDesignation IS NULL OR LOWER(jp.jobDesignation) LIKE LOWER(CONCAT('%', :jobDesignation, '%'))) " +
            "AND (:fromDate IS NULL OR ja.applicationDate >= :fromDate) " +
            "AND (:toDate IS NULL OR ja.applicationDate <= :toDate) " +
            "AND (:searchTerm IS NULL OR " +
            "     LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(jp.jobDesignation) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(jp.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(s.department) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(jp.jobType) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<JobApplication> searchJobApplications(@Param("status") ApplicationStatus status,
                                               @Param("location") String location,
                                               @Param("minSalary") Long minSalary,
                                               @Param("maxSalary") Long maxSalary,
                                               @Param("studentName") String student,
                                               @Param("department") String department,
                                               @Param("jobType") String jobType,
                                               @Param("jobDesignation") String designation,
                                               @Param("fromDate") LocalDate fromDate,
                                               @Param("toDate") LocalDate toDate,
                                               @Param("searchTerm") String searchTerm,
                                               Pageable pageable);

}
