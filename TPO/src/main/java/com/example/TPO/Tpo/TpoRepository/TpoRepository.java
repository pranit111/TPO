package com.example.TPO.Tpo.TpoRepository;

import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TpoRepository extends JpaRepository<TPOUser,Long> {
    Optional<TPOUser> findByUser(User user);
}
