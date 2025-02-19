package com.example.TPO.UserManagement.entity;

import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.DBMS.Tpo.TPO_Role;
import com.example.TPO.DBMS.stud.Student;
import jakarta.persistence.*;


@Entity(name = "User")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "Username",unique = true)
    private String username;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    @Column(name = "Email",unique = true)
    private String email;
    @Column(name = "Password")
    private String password;

    @Column(name = "role", nullable = false)
    public String role; // Enum for roles: STUDENT, TPO_USER, TPO_ADMIN

    public void setRole(String role) {
        this.role = role;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public TPOUser getTpoUser() {
        return tpoUser;
    }

    public void setTpoUser(TPOUser tpoUser) {
        this.tpoUser = tpoUser;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Student student;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TPOUser tpoUser;
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }


    public User(int id, String username, String password, String role,String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email= email;
    }
    public User(){}
}
