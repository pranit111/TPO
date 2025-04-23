package com.example.TPO.Student.StudentDTO;

import java.time.LocalDate;

public class StudentBasicDTO {
    private long id;
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
    private double sem1Marks;
    private double sem2Marks;
    private double sem3Marks;
    private double sem4Marks;
    private double sem5Marks;
    private double sem6Marks;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private String academicYear;
    private int noOfBacklogs;
    private String email;

    public boolean isSem1KT() {
        return sem1KT;
    }

    public void setSem1KT(boolean sem1KT) {
        this.sem1KT = sem1KT;
    }

    public boolean isSem2KT() {
        return sem2KT;
    }

    public void setSem2KT(boolean sem2KT) {
        this.sem2KT = sem2KT;
    }

    public boolean isSem3KT() {
        return sem3KT;
    }

    public void setSem3KT(boolean sem3KT) {
        this.sem3KT = sem3KT;
    }

    public boolean isSem4KT() {
        return sem4KT;
    }

    public void setSem4KT(boolean sem4KT) {
        this.sem4KT = sem4KT;
    }

    public boolean isSem5KT() {
        return sem5KT;
    }

    public void setSem5KT(boolean sem5KT) {
        this.sem5KT = sem5KT;
    }

    public boolean isSem6KT() {
        return sem6KT;
    }

    public void setSem6KT(boolean sem6KT) {
        this.sem6KT = sem6KT;
    }

    private String phoneNo;
    private int yearOfpassing;
    private boolean sem1KT;
    private boolean sem2KT;
    private boolean sem3KT;
    private boolean sem4KT;
    private boolean sem5KT;
    private boolean sem6KT;
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


    public StudentBasicDTO(long id, String firstName, String middleName, String lastName, String gender, LocalDate dateOfBirth, String grNo, double sscMarks, double hscMarks, double diplomaMarks, double avgMarks, String department, double sem1Marks, double sem2Marks, double sem3Marks, double sem4Marks, double sem5Marks, double sem6Marks, String academicYear, int noOfBacklogs, String email, String phoneNo, int yearOfpassing, boolean sem1KT, boolean sem2KT, boolean sem3KT, boolean sem4KT, boolean sem5KT, boolean sem6KT) {
        this.id = id;
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
        this.sem1Marks = sem1Marks;
        this.sem2Marks = sem2Marks;
        this.sem3Marks = sem3Marks;
        this.sem4Marks = sem4Marks;
        this.sem5Marks = sem5Marks;
        this.sem6Marks = sem6Marks;
        this.academicYear = academicYear;
        this.noOfBacklogs = noOfBacklogs;
        this.email = email;
        this.phoneNo = phoneNo;
        this.yearOfpassing = yearOfpassing;
        this.sem1KT = sem1KT;
        this.sem2KT = sem2KT;
        this.sem3KT = sem3KT;
        this.sem4KT = sem4KT;
        this.sem5KT = sem5KT;
        this.sem6KT = sem6KT;
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
