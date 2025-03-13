package com.example.TPO.Companies.CompaniesDTO;

import com.example.TPO.DBMS.Company.Company;
import java.time.LocalDate;

public class CompanyDTO {
    private Long id;
    private String name;
    private String industryType;
    private String email;
    private String contactNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getAssociatedSince() {
        return associatedSince;
    }

    public void setAssociatedSince(LocalDate associatedSince) {
        this.associatedSince = associatedSince;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    private String location;
    private String website;
    private LocalDate associatedSince;
    private Boolean isActive;

    // Constructors
    public CompanyDTO() {}

    public CompanyDTO(Long id, String name, String industryType, String email, String contactNumber,
                      String location, String website, LocalDate associatedSince, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.industryType = industryType;
        this.email = email;
        this.contactNumber = contactNumber;
        this.location = location;
        this.website = website;
        this.associatedSince = associatedSince;
        this.isActive = isActive;
    }

    // Convert to DTO from Entity
    public static CompanyDTO fromEntity(Company company) {
        return new CompanyDTO(
                company.getId(),
                company.getName(),
                company.getIndustryType(),
                company.getEmail(),
                company.getContactNumber(),
                company.getLocation(),
                company.getWebsite(),
                company.getAssociatedSince(),
                company.getActive()
        );
    }

    // Convert DTO to Entity
    public Company toEntity() {
        Company company = new Company();
        company.setId(this.id);
        company.setName(this.name);
        company.setIndustryType(this.industryType);
        company.setEmail(this.email);
        company.setContactNumber(this.contactNumber);
        company.setLocation(this.location);
        company.setWebsite(this.website);
        company.setAssociatedSince(this.associatedSince);
        company.setActive(this.isActive);
        return company;
    }

    // Getters and Setters
}
