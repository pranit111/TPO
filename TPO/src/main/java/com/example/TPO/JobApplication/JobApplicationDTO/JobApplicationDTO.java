package com.example.TPO.JobApplication.JobApplicationDTO;

import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.JobPost.JobPostDTO.JobPostDTO;
import com.example.TPO.Student.StudentDTO.StudentDTO;

import java.time.LocalDate;
import java.time.LocalTime;

public class JobApplicationDTO {
    private Long id;
    private StudentDTO student;  // Using StudentDTO instead of full Student entity
    private JobPostDTO jobPost;  // Using JobPostDTO instead of full JobPost entity
    private LocalDate applicationDate;
    private String status;  // Enum as String
    private String designation;

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

    private LocalTime interviewTime;
    private String interviewLocation;
// Applied, Shortlisted, Selected, Rejected
    private LocalDate  InterviewDate ;

    public LocalDate getInterviewDate() {
        return InterviewDate;
    }

    public void setInterviewDate(LocalDate interviewDate) {
        InterviewDate = interviewDate;
    }

    public String getFeedback() {
        return Feedback;
    }

    public void setFeedback(String feedback) {
        Feedback = feedback;
    }

    private String Feedback;// Added Designation field

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    // Constructor


    public JobApplicationDTO(Long id, StudentDTO student, JobPostDTO jobPost, LocalDate applicationDate, String status, String designation, LocalTime interviewTime, String interviewLocation, LocalDate interviewDate, String feedback) {
        this.id = id;
        this.student = student;
        this.jobPost = jobPost;
        this.applicationDate = applicationDate;
        this.status = status;
        this.designation = designation;
        this.interviewTime = interviewTime;
        this.interviewLocation = interviewLocation;
        this.InterviewDate = interviewDate;
        this.Feedback = feedback;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentDTO getStudent() { return student; }
    public void setStudent(StudentDTO student) { this.student = student; }

    public JobPostDTO getJobPost() { return jobPost; }
    public void setJobPost(JobPostDTO jobPost) { this.jobPost = jobPost; }

    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
