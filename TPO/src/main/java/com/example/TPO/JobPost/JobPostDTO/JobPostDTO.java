package com.example.TPO.JobPost.JobPostDTO;

import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Company.Company;

import java.time.LocalDate;

public class JobPostDTO {
    private Long id;
    private Company company;
    private String jobDesignation;
    private String location;
    private String jobType;
    private String description;
    private double packageAmount;
    private double minPercentage;
    private int backlogAllowance;
    private String preferredCourse;

    private boolean aptitude;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getAptitudeDate() {
        return aptitudeDate;
    }

    public void setAptitudeDate(LocalDate aptitudeDate) {
        this.aptitudeDate = aptitudeDate;
    }

    public boolean isAptitude() {
        return aptitude;
    }

    public void setAptitude(boolean aptitude) {
        this.aptitude = aptitude;
    }

    private LocalDate aptitudeDate;
    private String status;
    private String skillRequirements;
    private String selectionRounds;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    private String modeOfRecruitment;
    private String testPlatform;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private LocalDate selectionStartDate;
    private LocalDate selectionEndDate;
    private ApplicationStatus applicationStatus;
    // Constructor
    public JobPostDTO(Long id, Company company, String jobDesignation, String location, String jobType,
                      String description, double packageAmount, double minPercentage, int backlogAllowance,
                      String preferredCourse, String skillRequirements, String selectionRounds,
                      String modeOfRecruitment, String testPlatform, LocalDate applicationStartDate,
                      LocalDate applicationEndDate, LocalDate selectionStartDate, LocalDate selectionEndDate,LocalDate aptitudeDate,String status,boolean aptitude) {
        this.id = id;
        this.company = company;
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
        this.modeOfRecruitment = modeOfRecruitment;
        this.testPlatform = testPlatform;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.selectionStartDate = selectionStartDate;
        this.selectionEndDate = selectionEndDate;
        this.aptitude=aptitude;
        this.aptitudeDate=aptitudeDate;
        this.status=status;
    }

    // Getters & Setters


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }



    public String getJobDesignation() { return jobDesignation; }
    public void setJobDesignation(String jobDesignation) { this.jobDesignation = jobDesignation; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPackageAmount() { return packageAmount; }
    public void setPackageAmount(double packageAmount) { this.packageAmount = packageAmount; }

    public double getMinPercentage() { return minPercentage; }
    public void setMinPercentage(double minPercentage) { this.minPercentage = minPercentage; }

    public int getBacklogAllowance() { return backlogAllowance; }
    public void setBacklogAllowance(int backlogAllowance) { this.backlogAllowance = backlogAllowance; }

    public String getPreferredCourse() { return preferredCourse; }
    public void setPreferredCourse(String preferredCourse) { this.preferredCourse = preferredCourse; }

    public String getSkillRequirements() { return skillRequirements; }
    public void setSkillRequirements(String skillRequirements) { this.skillRequirements = skillRequirements; }

    public String getSelectionRounds() { return selectionRounds; }
    public void setSelectionRounds(String selectionRounds) { this.selectionRounds = selectionRounds; }

    public String getModeOfRecruitment() { return modeOfRecruitment; }
    public void setModeOfRecruitment(String modeOfRecruitment) { this.modeOfRecruitment = modeOfRecruitment; }

    public String getTestPlatform() { return testPlatform; }
    public void setTestPlatform(String testPlatform) { this.testPlatform = testPlatform; }

    public LocalDate getApplicationStartDate() { return applicationStartDate; }
    public void setApplicationStartDate(LocalDate applicationStartDate) { this.applicationStartDate = applicationStartDate; }

    public LocalDate getApplicationEndDate() { return applicationEndDate; }
    public void setApplicationEndDate(LocalDate applicationEndDate) { this.applicationEndDate = applicationEndDate; }

    public LocalDate getSelectionStartDate() { return selectionStartDate; }
    public void setSelectionStartDate(LocalDate selectionStartDate) { this.selectionStartDate = selectionStartDate; }

    public LocalDate getSelectionEndDate() { return selectionEndDate; }
    public void setSelectionEndDate(LocalDate selectionEndDate) { this.selectionEndDate = selectionEndDate; }
}
