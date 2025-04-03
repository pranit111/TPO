package com.example.TPO.DBMS.Tpo.dto;

import com.example.TPO.DBMS.Tpo.TPO_Role;

public class TPOUserDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private TPO_Role role;

    // Constructors
    public TPOUserDTO() {}

    public TPOUserDTO(Long id, Long userId, String userEmail, TPO_Role role) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.role = role;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public TPO_Role getRole() {
        return role;
    }

    public void setRole(TPO_Role role) {
        this.role = role;
    }
}
