package com.example.TPO.DBMS.JobPost;

import com.example.TPO.DBMS.Tpo.TPOUser;
import jakarta.persistence.*;
import com.example.TPO.DBMS.Applications.JobApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "job_posts")
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String jobDesignation;
    @Column(nullable = false)
    private String location; @Column(nullable = false)
    private String jobType;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private double packageAmount;
    @Column(nullable = false)
    private double minPercentage;
    @Column(nullable = false)
    private int backlogAllowance;
    @Column(nullable = false)
    private String preferredCourse;
    @Column(nullable = false)
    private String skillRequirements;

    public List<JobApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<JobApplication> applications) {
        this.applications = applications;
    }

    private String selectionRounds;
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false,referencedColumnName = "id")
    private TPOUser createdBy;

    public TPOUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(TPOUser createdBy) {
        this.createdBy = createdBy;
    }


    public JobPost(Long id, String companyName, String jobDesignation, String location, String jobType, String description, double packageAmount, double minPercentage, int backlogAllowance, String preferredCourse, String skillRequirements, String selectionRounds, TPOUser createdBy, String modeOfRecruitment, String testPlatform, LocalDate applicationStartDate, LocalDate applicationEndDate, LocalDate selectionStartDate, LocalDate selectionEndDate) {
        this.id = id;
        this.companyName = companyName;
        this.jobDesignation = jobDesignation;
        this.location = location;
        this.jobType = jobType;
        this.description = description;
        this.packageAmount = packageAmount;
        this.minPercentage = minPercentage;
        this.backlogAllowance = backlogAllowance;
        this.preferredCourse = preferredCourse;
        this.skillRequirements = skillRequirements;
        this.selectionRounds = selectionRounds;
        this.createdBy = createdBy;
        this.modeOfRecruitment = modeOfRecruitment;
        this.testPlatform = testPlatform;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.selectionStartDate = selectionStartDate;
        this.selectionEndDate = selectionEndDate;
    }

    public JobPost() {
    }

    private String modeOfRecruitment;
    private String testPlatform;

    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private LocalDate selectionStartDate;
    private LocalDate selectionEndDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobDesignation() {
        return jobDesignation;
    }

    public void setJobDesignation(String jobDesignation) {
        this.jobDesignation = jobDesignation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(double packageAmount) {
        this.packageAmount = packageAmount;
    }

    public double getMinPercentage() {
        return minPercentage;
    }

    public void setMinPercentage(double minPercentage) {
        this.minPercentage = minPercentage;
    }

    public int getBacklogAllowance() {
        return backlogAllowance;
    }

    public void setBacklogAllowance(int backlogAllowance) {
        this.backlogAllowance = backlogAllowance;
    }

    public String getPreferredCourse() {
        return preferredCourse;
    }

    public void setPreferredCourse(String preferredCourse) {
        this.preferredCourse = preferredCourse;
    }

    public String getSkillRequirements() {
        return skillRequirements;
    }

    public void setSkillRequirements(String skillRequirements) {
        this.skillRequirements = skillRequirements;
    }

    public String getSelectionRounds() {
        return selectionRounds;
    }

    public void setSelectionRounds(String selectionRounds) {
        this.selectionRounds = selectionRounds;
    }

    public String getModeOfRecruitment() {
        return modeOfRecruitment;
    }

    public void setModeOfRecruitment(String modeOfRecruitment) {
        this.modeOfRecruitment = modeOfRecruitment;
    }

    public String getTestPlatform() {
        return testPlatform;
    }

    public void setTestPlatform(String testPlatform) {
        this.testPlatform = testPlatform;
    }

    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public LocalDate getApplicationEndDate() {
        return applicationEndDate;
    }

    public void setApplicationEndDate(LocalDate applicationEndDate) {
        this.applicationEndDate = applicationEndDate;
    }

    public LocalDate getSelectionStartDate() {
        return selectionStartDate;
    }

    public void setSelectionStartDate(LocalDate selectionStartDate) {
        this.selectionStartDate = selectionStartDate;
    }

    public LocalDate getSelectionEndDate() {
        return selectionEndDate;
    }

    public void setSelectionEndDate(LocalDate selectionEndDate) {
        this.selectionEndDate = selectionEndDate;
    }


@Deprecated
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobApplication> applications;

    // Getters & Setters
}
