package com.example.TPO.Placements.PlacementRepository;

import com.example.TPO.DBMS.Placements.Placement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PlacementRepository extends JpaRepository<Placement,Long> {

    @Query("""
    SELECT p FROM Placement p
    JOIN p.application ja
    JOIN ja.student s 
    JOIN ja.jobPost jp
    JOIN jp.company c
    WHERE 
        LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(jp.jobDesignation) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(p.remarks) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    Page<Placement> searchAll(@Param("keyword") String keyword, Pageable pageable);

    // Filter by company name
    @Query("""
    SELECT p FROM Placement p
    JOIN p.application ja
    JOIN ja.jobPost jp
    JOIN jp.company c
    WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :companyName, '%'))
    """)
    Page<Placement> filterByCompany(@Param("companyName") String companyName, Pageable pageable);

    // Filter by job designation
    @Query("""
    SELECT p FROM Placement p
    JOIN p.application ja
    JOIN ja.jobPost jp
    WHERE LOWER(jp.jobDesignation) LIKE LOWER(CONCAT('%', :jobDesignation, '%'))
    """)
    Page<Placement> filterByJobDesignation(@Param("jobDesignation") String jobDesignation, Pageable pageable);

    // Filter by package range
    @Query("""
    SELECT p FROM Placement p
    WHERE p.placed_package BETWEEN :minPackage AND :maxPackage
    """)
    Page<Placement> filterByPackageRange(@Param("minPackage") double minPackage, 
                                       @Param("maxPackage") double maxPackage, 
                                       Pageable pageable);

    // Filter by placement date range
    @Query("""
    SELECT p FROM Placement p
    WHERE p.placementDate BETWEEN :fromDate AND :toDate
    """)
    Page<Placement> filterByDateRange(@Param("fromDate") LocalDate fromDate, 
                                    @Param("toDate") LocalDate toDate, 
                                    Pageable pageable);

    // Filter by student name
    @Query("""
    SELECT p FROM Placement p
    JOIN p.application ja
    JOIN ja.student s
    WHERE LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :studentName, '%'))
    """)
    Page<Placement> filterByStudentName(@Param("studentName") String studentName, Pageable pageable);

    // Filter by department
    @Query("""
    SELECT p FROM Placement p
    JOIN p.application ja
    JOIN ja.student s
    WHERE LOWER(s.department) LIKE LOWER(CONCAT('%', :department, '%'))
    """)
    Page<Placement> filterByDepartment(@Param("department") String department, Pageable pageable);

    // Filter by student year (through job post)
    @Query("""
    SELECT p FROM Placement p
    JOIN p.application ja
    JOIN ja.jobPost jp
    WHERE jp.studentYear = :studentYear
    """)
    Page<Placement> filterByStudentYear(@Param("studentYear") String studentYear, Pageable pageable);

}
