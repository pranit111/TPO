package com.example.TPO.Tpo.TPODTO;

import com.example.TPO.DBMS.Tpo.TPO_Role;

public class TPOUserDTO {
    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private TPO_Role role;
    private boolean verified;
    // Constructors
    public TPOUserDTO() {}

    public TPOUserDTO(Long id, String username,Long userId, String userEmail, TPO_Role role, boolean verified) {
        this.id = id;
        this.username=username;
        this.userId = userId;
        this.userEmail = userEmail;
        this.role = role;
        this.verified=verified;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
