package com.example.TPO.DBMS.Applications;

import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.DBMS.stud.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime; // Add this import

@Data
@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status; // Applied, Shortlisted, Selected, Rejected

    private LocalDate interviewDate;

    // Add these new fields
    private LocalTime interviewTime;
    private String interviewLocation;

    private String feedback;

    // Basic constructor
    public JobApplication() {}

    // Full constructor with new fields
    public JobApplication(Long id, Student student, JobPost jobPost, LocalDate applicationDate,
                          ApplicationStatus status, LocalDate interviewDate,
                          LocalTime interviewTime, String interviewLocation, String feedback) {
        this.id = id;
        this.student = student;
        this.jobPost = jobPost;
        this.applicationDate = applicationDate;
        this.status = status;
        this.interviewDate = interviewDate;
        this.interviewTime = interviewTime;
        this.interviewLocation = interviewLocation;
        this.feedback = feedback;
    }

    // Getters and Setters for the new fields
    public LocalTime getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(LocalTime interviewTime) {
        this.interviewTime = interviewTime;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    // Existing getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDate interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}