package com.example.TPO.DBMS.JobPost;

import jakarta.persistence.*;
import com.example.TPO.DBMS.Applications.JobApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job_posts")
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String jobDesignation;
    private String location;
    private String jobType;
    private String description;
    private double packageAmount;

    private double minPercentage;
    private int backlogAllowance;
    private String preferredCourse;
    private String skillRequirements;

    private String selectionRounds;
    private String modeOfRecruitment;
    private String testPlatform;

    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private LocalDate selectionStartDate;
    private LocalDate selectionEndDate;
@Deprecated
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobApplication> applications;

    // Getters & Setters
}
