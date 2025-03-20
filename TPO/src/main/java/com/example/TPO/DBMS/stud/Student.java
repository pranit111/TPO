package com.example.TPO.DBMS.stud;

import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.UserManagement.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.Year;
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
        updateCalculatedFields();
    }

    public double getSem2Marks() {
        return sem2Marks;
    }

    public void setSem2Marks(double sem2Marks) {
        this.sem2Marks = sem2Marks;
        updateCalculatedFields();
    }

    public double getSem3Marks() {
        return sem3Marks;
    }

    public void setSem3Marks(double sem3Marks) {
        this.sem3Marks = sem3Marks;
        updateCalculatedFields();
    }

    public double getSem4Marks() {
        return sem4Marks;
    }

    public void setSem4Marks(double sem4Marks) {
        this.sem4Marks = sem4Marks;
        updateCalculatedFields();
    }

    public double getSem5Marks() {
        return sem5Marks;
    }

    public void setSem5Marks(double sem5Marks) {
        this.sem5Marks = sem5Marks;
        updateCalculatedFields();
    }

    public double getSem6Marks() {
        return sem6Marks;
    }

    public void setSem6Marks(double sem6Marks) {
        this.sem6Marks = sem6Marks;
        updateCalculatedFields();
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

    public Student() {
    }

    private String phoneNumber;

    public String getAcademicyear() {
        return academicyear;
    }

    public void setAcademicyear(String academicyear) {
        this.academicyear = academicyear;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getImagetype() {
        return imagetype;
    }

    public void setImagetype(String imagetype) {
        this.imagetype = imagetype;
    }

    public byte[] getProfileimagedata() {
        return profileimagedata;
    }

    public void setProfileimagedata(byte[] profileimagedata) {
        this.profileimagedata = profileimagedata;
    }

    public String getResumename() {
        return resumename;
    }

    public void setResumename(String resumename) {
        this.resumename = resumename;
    }

    public byte[] getResume_file_data() {
        return resume_file_data;
    }





    public double getAvgMarks() {
        return avgMarks;
    }

    public void setAvgMarks(double avgMarks) {
        this.avgMarks = avgMarks;
    }

    public void setResume_file_data(byte[] resume_file_data) {
        this.resume_file_data = resume_file_data;
    }
    private void updateCalculatedFields() {
        double sum = 0;
        int count = 0;

        double[] marks = {sem1Marks, sem2Marks, sem3Marks, sem4Marks, sem5Marks, sem6Marks};

        for (double mark : marks) {
            if (mark > 0) { // Exclude 0 values
                sum += mark;
                count++;
            }
        }

        double avg = (count > 0) ? sum / count : 0; // Avoid division by zero
        this.avgMarks = avg * 9.5; // Convert CGPA to Percentage
    }



    public Student(Long id, User user, String firstName, String middleName, String lastName, LocalDate dateOfBirth, Gender gender, String phoneNumber, String gr_No, String address, String department, String academicyear, double avgMarks, double sscMarks, double hscMarks, double diplomaMarks, double sem1Marks, double sem2Marks, double sem3Marks, double sem4Marks, double sem5Marks, double sem6Marks, int noOfBacklogs, String imagename, String imagetype, byte[] profileimagedata, String resumename, byte[] resume_file_data, List<JobApplication> jobApplications) {
        this.id = id;
        this.user = user;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.grNo =gr_No;
        this.address = address;
        this.department = department;
        this.academicyear = academicyear;
        this.avgMarks = avgMarks;
        this.sscMarks = sscMarks;
        this.hscMarks = hscMarks;
        this.diplomaMarks = diplomaMarks;
        this.sem1Marks = sem1Marks;
        this.sem2Marks = sem2Marks;
        this.sem3Marks = sem3Marks;
        this.sem4Marks = sem4Marks;
        this.sem5Marks = sem5Marks;
        this.sem6Marks = sem6Marks;
        this.noOfBacklogs = noOfBacklogs;
        this.imagename = imagename;
        this.imagetype = imagetype;
        this.profileimagedata = profileimagedata;
        this.resumename = resumename;
        this.resume_file_data = resume_file_data;
        this.jobApplications = jobApplications;
    }

    @JsonProperty("gr_No") // Ensure correct mapping from JSON request
    private String grNo;

    public String getGrNo() {
        return grNo;
    }

    public void setGrNo(String grNo) {
        this.grNo = grNo;  // Corrected to update the instance variable
    }

    private String address;
    private String department;
    private String academicyear;

    private double avgMarks;
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
    private String imagename;
    private String imagetype;
    @Lob
    private byte[] profileimagedata;
    private String resumename;
    @Lob
    private byte[] resume_file_data;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<JobApplication> jobApplications;
    @PrePersist
    @PreUpdate
    public void calculateAvgBeforeSave() {
        updateCalculatedFields();
        setYearOfPassing();
    }
    // Getters & Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getYearOfPassing() {
        return yearOfPassing;
    }



    // This field won't be saved in the database
    private int yearOfPassing;

    public void setYearOfPassing() {
        this.yearOfPassing= calculateYearOfPassing();
    }

    private int calculateYearOfPassing() {
        int currentYear = Year.now().getValue();

        switch (this.academicyear) {
            case "FE": return currentYear + 3; // First Year -> Pass in 4 years (FE -> SE -> TE -> BE)
            case "SE": return currentYear + 2; // Second Year -> Pass in 3 years (SE -> TE -> BE)
            case "TE": return currentYear + 1; // Third Year -> Pass in 2 years (TE -> BE)
            case "BE": return currentYear;     // Final Year -> Pass this year
            default: return 0; // Handle unexpected values
        }
}}
