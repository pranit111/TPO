package com.example.TPO.Placements.PlacementRepository;

import com.example.TPO.DBMS.Placements.Placement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
