package com.example.TPO.DBMS.Tpo;

import com.example.TPO.UserManagement.entity.User;
import jakarta.persistence.*;



@Entity
@Table(name = "tpo_users")
public class TPOUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user", unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private TPO_Role role; // TPO, TPO_MANAGER, TPO_ADMIN


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TPO_Role getRole() {
        return role;
    }

    public void setRole(TPO_Role role) {
        this.role = role;
    }

    public TPOUser(Long id, User user, TPO_Role role) {
        this.id = id;
        this.user = user;
        this.role = role;
    }
    public TPOUser(){}


    // Getters & Setters
}

