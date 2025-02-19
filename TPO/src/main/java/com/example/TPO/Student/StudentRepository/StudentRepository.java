package com.example.TPO.Student.StudentRepository;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {

    Optional<Student> findByUserId(Long userId);

    Optional<Student> findByUser(User user);
}
