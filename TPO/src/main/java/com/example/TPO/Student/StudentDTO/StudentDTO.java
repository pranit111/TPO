package com.example.TPO.Student.StudentDTO;

import com.example.TPO.UserManagement.UserDTO.UserDTO;

import java.time.LocalDate;
import java.util.List;

public class StudentDTO {
    private Long id;
    private UserDTO user; // Nested UserDTO
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
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

    // Constructor
    public StudentDTO(Long id, UserDTO user, String firstName, String middleName, String lastName, LocalDate dateOfBirth,
                      String gender, String phoneNumber, String address, String department, double sscMarks,
                      double hscMarks, double diplomaMarks, double sem1Marks, double sem2Marks, double sem3Marks,
                      double sem4Marks, double sem5Marks, double sem6Marks, int noOfBacklogs) {
        this.id = id;
        this.user = user;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.department = department;
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
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getSscMarks() { return sscMarks; }
    public void setSscMarks(double sscMarks) { this.sscMarks = sscMarks; }

    public double getHscMarks() { return hscMarks; }
    public void setHscMarks(double hscMarks) { this.hscMarks = hscMarks; }

    public double getDiplomaMarks() { return diplomaMarks; }
    public void setDiplomaMarks(double diplomaMarks) { this.diplomaMarks = diplomaMarks; }

    public double getSem1Marks() { return sem1Marks; }
    public void setSem1Marks(double sem1Marks) { this.sem1Marks = sem1Marks; }

    public double getSem2Marks() { return sem2Marks; }
    public void setSem2Marks(double sem2Marks) { this.sem2Marks = sem2Marks; }

    public double getSem3Marks() { return sem3Marks; }
    public void setSem3Marks(double sem3Marks) { this.sem3Marks = sem3Marks; }

    public double getSem4Marks() { return sem4Marks; }
    public void setSem4Marks(double sem4Marks) { this.sem4Marks = sem4Marks; }

    public double getSem5Marks() { return sem5Marks; }
    public void setSem5Marks(double sem5Marks) { this.sem5Marks = sem5Marks; }

    public double getSem6Marks() { return sem6Marks; }
    public void setSem6Marks(double sem6Marks) { this.sem6Marks = sem6Marks; }

    public int getNoOfBacklogs() { return noOfBacklogs; }
    public void setNoOfBacklogs(int noOfBacklogs) { this.noOfBacklogs = noOfBacklogs; }
}
