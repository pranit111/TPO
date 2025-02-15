package com.example.TPO.DBMS.stud;

import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;
    private String address;
    private String department;
    private double sscMarks;
    private double hscMarks;
    private double diplomaMarks;

    private double sem1Marks;
    private double sem2Marks;
    private double sem3Marks;
    private double sem4Marks;
    private double sem5Marks;
    private double sem6Marks;

    private int noOfBacklogs;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<JobApplication> jobApplications;

    // Getters & Setters
}
