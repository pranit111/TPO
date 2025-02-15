package com.example.TPO.DBMS.user;

import com.example.TPO.DBMS.Tpo.TPOUser;
import jakarta.persistence.*;
import com.example.TPO.DBMS.Tpo.TPO_Role;

import com.example.TPO.DBMS.stud.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private TPO_Role role; // STUDENT, TPO, TPO_ADMIN

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Student student;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TPOUser tpoUser;

    // Getters & Setters
}
