package com.example.TPO.Student.StudentRepository;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {

    Optional<Student> findByUserId(Long userId);

    Optional<Student> findByUser(User user);


    @Query("SELECT s FROM Student s WHERE " +
            "(:firstName IS NULL OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
            "AND (:department IS NULL OR LOWER(s.department) = LOWER(:department)) " +
            "AND (:academicYear IS NULL OR LOWER(s.academicyear) = LOWER(:academicYear)) " +
            "AND (:minAvgMarks IS NULL OR s.avgMarks >= :minAvgMarks) " +
            "AND (:maxAvgMarks IS NULL OR s.avgMarks <= :maxAvgMarks) " +
            "AND (:yearOfPassing IS NULL OR s.yearOfPassing = :yearOfPassing)")
    Page<Student> searchStudents(String firstName,
                                 String department,
                                 String academicYear,
                                 Double minAvgMarks,
                                 Double maxAvgMarks,
                                 Integer yearOfPassing,
                                 Pageable pageable);

    List<Student> findTop100ByOrderByIdDesc();
}
