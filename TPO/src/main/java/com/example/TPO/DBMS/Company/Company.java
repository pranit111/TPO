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

    public String getHr_Name() {
        return hr_Name;
    }

    public void setHr_Name(String hr_Name) {
        this.hr_Name = hr_Name;
    }

    public boolean isMnc() {
        return Mnc;
    }

    public void setMnc(boolean mnc) {
        Mnc = mnc;
    }

    private String name;
    private boolean Mnc;
    private String hr_Name;

    public Company(Long id, String name, boolean mnc, String hr_Name, String industryType, String email, String contactNumber, String location, String website, LocalDate associatedSince, Boolean isActive, TPOUser tpoCoordinator) {
        this.id = id;
        this.name = name;
        Mnc = mnc;
        this.hr_Name = hr_Name;
        this.industryType = industryType;
        this.email = email;
        this.contactNumber = contactNumber;
        this.location = location;
        this.website = website;
        this.associatedSince = associatedSince;
        this.isActive = isActive;
        this.tpoCoordinator = tpoCoordinator;
    }

    public Company() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TPOUser getTpoCoordinator() {
        return tpoCoordinator;
    }

    public void setTpoCoordinator(TPOUser tpoCoordinator) {
        this.tpoCoordinator = tpoCoordinator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public LocalDate getAssociatedSince() {
        return associatedSince;
    }

    public void setAssociatedSince(LocalDate associatedSince) {
        this.associatedSince = associatedSince;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

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
