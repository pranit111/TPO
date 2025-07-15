package com.example.TPO.DBMS.JobPost;

import com.example.TPO.DBMS.Company.Company;
import com.example.TPO.DBMS.Tpo.TPOUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    // Establishing Foreign Key
    private Company company;
    @Column(nullable = false)
    private String jobDesignation;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private String jobType;
    
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String description;
    @Column(nullable = false)
    private double packageAmount;
    @Column(nullable = false)
    private double minPercentage;
    @Column(nullable = false)
    private int backlogAllowance;
    @Column(nullable = false)
    private String preferredCourse;
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    @JsonProperty("skillsRequirements")
    private String skillRequirements;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JobPostStatus Status;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_year", nullable = false)
    private StudentYear studentYear;

    private Boolean aptitude;

    public List<JobApplication> getApplications() {
        return applications;
    }

    public String PortalLink;
    public void setApplications(List<JobApplication> applications) {
        this.applications = applications;
    }

    @Lob
    @Column(columnDefinition = "LONGTEXT")
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


    public String getPortalLink() {
        return PortalLink;
    }

    public void setPortalLink(String portalLink) {
        PortalLink = portalLink;
    }

    public JobPost() {
    }

    private String modeOfRecruitment;

    public JobPost(Long id, Company company, String jobDesignation, String location, String jobType, String description, double packageAmount, double minPercentage, int backlogAllowance, String preferredCourse, String skillRequirements, JobPostStatus status, StudentYear studentYear, Boolean aptitude, String portalLink, String selectionRounds, TPOUser createdBy, String modeOfRecruitment, String testPlatform, double minimumSsc, double minimumHsc, LocalDate aptitudedate, LocalDate applicationStartDate, LocalDate applicationEndDate, LocalDate selectionStartDate, LocalDate selectionEndDate) {
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
        this.Status = status;
        this.studentYear = studentYear;
        this.aptitude = aptitude;
        this.PortalLink = portalLink;
        this.selectionRounds = selectionRounds;
        this.createdBy = createdBy;
        this.modeOfRecruitment = modeOfRecruitment;
        this.testPlatform = testPlatform;
        this.minimumSsc = minimumSsc;
        this.minimumHsc = minimumHsc;
        this.aptitudedate = aptitudedate;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.selectionStartDate = selectionStartDate;
        this.selectionEndDate = selectionEndDate;
    }

    public JobPostStatus getStatus() {
        updateStatus(); // Auto-update status before returning
        return Status;
    }

    public void setStatus(JobPostStatus status) {
        Status = status;
    }

    private String testPlatform;

    public double getMinimumHsc() {
        return minimumHsc;
    }

    public void setMinimumHsc(double minimumHsc) {
        this.minimumHsc = minimumHsc;
    }

    public Boolean getAptitude() {
        return aptitude;
    }

    public void setAptitude(Boolean aptitude) {
        this.aptitude = aptitude;
    }

    public LocalDate getAptitudedate() {
        return aptitudedate;
    }

    public void setAptitudedate(LocalDate aptitudedate) {
        this.aptitudedate = aptitudedate;
    }

    public double getMinimumSsc() {
        return minimumSsc;
    }

    public void setMinimumSsc(double minimumSsc) {
        this.minimumSsc = minimumSsc;
    }

    private double minimumSsc;
    private double minimumHsc;
    private LocalDate aptitudedate;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public StudentYear getStudentYear() {
        return studentYear;
    }

    public void setStudentYear(StudentYear studentYear) {
        this.studentYear = studentYear;
    }

    @Deprecated
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobApplication> applications;

    /**
     * Automatically updates the job post status based on current date and configured dates
     * This method is called by lifecycle events and getter methods to ensure status is always current
     */
  public void updateStatus() {
        LocalDate today = LocalDate.now();

        // Respect admin-set terminal statuses
        if (Status == JobPostStatus.CLOSED || Status == JobPostStatus.ON_HOLD || Status == JobPostStatus.CANCELLED) {
            return;
        }

        // If job is marked as FILLED, handle expiration
        if (Status == JobPostStatus.FILLED) {
            if (selectionEndDate != null && today.isAfter(selectionEndDate)) {
                this.Status = JobPostStatus.EXPIRED;
            }
            return; // if not expired, keep as FILLED
        }

        // Check if required dates are set
        if (applicationStartDate == null || applicationEndDate == null ||
                selectionStartDate == null || selectionEndDate == null) {
            return;
        }

        // Auto-update based on timeline
        if (today.isBefore(applicationStartDate)) {
            this.Status = JobPostStatus.UPCOMING;

        } else if ((today.isEqual(applicationStartDate) || today.isAfter(applicationStartDate)) &&
                (today.isBefore(applicationEndDate) || today.isEqual(applicationEndDate))) {
            this.Status = JobPostStatus.OPEN;
        } else if (today.isAfter(applicationEndDate) &&
                (today.isBefore(selectionStartDate) || today.isEqual(selectionStartDate))) {
            this.Status = JobPostStatus.CLOSED;
        } else if (today.isAfter(selectionStartDate) &&
                (today.isBefore(selectionEndDate) || today.isEqual(selectionEndDate))) {
            this.Status = JobPostStatus.SELECTION_IN_PROGRESS;
        } else if (today.isAfter(selectionEndDate)) {
            this.Status = JobPostStatus.EXPIRED;
        }
    }

    /**
     * JPA lifecycle method to update status whenever the entity is loaded
     */
    @PostLoad
    public void onLoad() {
        updateStatus();
    }

    /**
     * JPA lifecycle method to update status before persisting
     */
    @PrePersist
    @PreUpdate
    public void beforeSave() {
        updateStatus();
    }
}