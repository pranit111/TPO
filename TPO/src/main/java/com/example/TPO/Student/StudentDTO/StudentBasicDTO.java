package com.example.TPO.Student.StudentDTO;

import java.time.LocalDate;

public class StudentBasicDTO {
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String grNo;
    private double sscMarks;
    private double hscMarks;
    private double diplomaMarks;
    private double avgMarks;
    private String department;
    private String academicYear;
    private int noOfBacklogs;
    private String email;
    private String phoneNo;
    private int yearOfpassing;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getYearOfpassing() {
        return yearOfpassing;
    }

    public void setYearOfpassing(int yearOfpassing) {
        this.yearOfpassing = yearOfpassing;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    // Constructor
    public StudentBasicDTO(String firstName, String middleName, String lastName, String gender, LocalDate dateOfBirth,
                           String grNo, double sscMarks, double hscMarks, double diplomaMarks, double avgMarks,
                           String department, String academicYear, int noOfBacklogs,  String email, String phoneNo, int yearOfpassing) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.grNo = grNo;
        this.sscMarks = sscMarks;
        this.hscMarks = hscMarks;
        this.diplomaMarks = diplomaMarks;
        this.avgMarks = avgMarks;
        this.department = department;
        this.academicYear = academicYear;
        this.noOfBacklogs = noOfBacklogs;
        this.email=email;
        this.phoneNo=phoneNo;
        this.yearOfpassing=yearOfpassing;
    }

    // Getters & Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGrNo() { return grNo; }
    public void setGrNo(String grNo) { this.grNo = grNo; }

    public double getSscMarks() { return sscMarks; }
    public void setSscMarks(double sscMarks) { this.sscMarks = sscMarks; }

    public double getHscMarks() { return hscMarks; }
    public void setHscMarks(double hscMarks) { this.hscMarks = hscMarks; }

    public double getDiplomaMarks() { return diplomaMarks; }
    public void setDiplomaMarks(double diplomaMarks) { this.diplomaMarks = diplomaMarks; }

    public double getAvgMarks() { return avgMarks; }
    public void setAvgMarks(double avgMarks) { this.avgMarks = avgMarks; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public int getNoOfBacklogs() { return noOfBacklogs; }
    public void setNoOfBacklogs(int noOfBacklogs) { this.noOfBacklogs = noOfBacklogs; }
}
