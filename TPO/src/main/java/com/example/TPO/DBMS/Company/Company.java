package com.example.TPO.DBMS.Company;

import com.example.TPO.DBMS.Tpo.TPOUser;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String industryType;
    private String email;
    private String contactNumber;
    private String location;
    private String website;
    private LocalDate associatedSince;
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "tpo_coordinator_id")
    private TPOUser tpoCoordinator;  // TPO Staff managing the company

    // Getters and Setters
}
