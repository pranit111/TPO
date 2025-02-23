package com.example.TPO.DBMS.stud;

import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.UserManagement.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Add this annotation

@Entity
@Table(name = "students")
public class Student {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user", unique = true)
    private User user;



    private String firstName;
    private String middleName;
    private String lastName;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSscMarks() {
        return sscMarks;
    }

    public void setSscMarks(double sscMarks) {
        this.sscMarks = sscMarks;
    }

    public double getHscMarks() {
        return hscMarks;
    }

    public void setHscMarks(double hscMarks) {
        this.hscMarks = hscMarks;
    }

    public double getDiplomaMarks() {
        return diplomaMarks;
    }

    public void setDiplomaMarks(double diplomaMarks) {
        this.diplomaMarks = diplomaMarks;
    }

    public double getSem1Marks() {
        return sem1Marks;
    }

    public void setSem1Marks(double sem1Marks) {
        this.sem1Marks = sem1Marks;
    }

    public double getSem2Marks() {
        return sem2Marks;
    }

    public void setSem2Marks(double sem2Marks) {
        this.sem2Marks = sem2Marks;
    }

    public double getSem3Marks() {
        return sem3Marks;
    }

    public void setSem3Marks(double sem3Marks) {
        this.sem3Marks = sem3Marks;
    }

    public double getSem4Marks() {
        return sem4Marks;
    }

    public void setSem4Marks(double sem4Marks) {
        this.sem4Marks = sem4Marks;
    }

    public double getSem5Marks() {
        return sem5Marks;
    }

    public void setSem5Marks(double sem5Marks) {
        this.sem5Marks = sem5Marks;
    }

    public double getSem6Marks() {
        return sem6Marks;
    }

    public void setSem6Marks(double sem6Marks) {
        this.sem6Marks = sem6Marks;
    }

    public int getNoOfBacklogs() {
        return noOfBacklogs;
    }

    public void setNoOfBacklogs(int noOfBacklogs) {
        this.noOfBacklogs = noOfBacklogs;
    }

    public List<JobApplication> getJobApplications() {
        return jobApplications;
    }

    public void setJobApplications(List<JobApplication> jobApplications) {
        this.jobApplications = jobApplications;
    }

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
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
