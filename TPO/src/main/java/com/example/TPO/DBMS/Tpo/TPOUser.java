package com.example.TPO.DBMS.Tpo;

import com.example.TPO.DBMS.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tpo_users")
public class TPOUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private TPO_Role role; // TPO, TPO_MANAGER, TPO_ADMIN

    // Getters & Setters
}

