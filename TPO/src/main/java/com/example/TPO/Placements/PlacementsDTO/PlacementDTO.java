package com.example.TPO.Placements.PlacementsDTO;

import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlacementDTO {

    private Long id;
    private JobApplicationDTO application; // Nested DTO
    private LocalDate placementDate;
    private byte[] offerLetterUrl;
    private double placedPackage;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public PlacementDTO() {}

    public PlacementDTO(Long id, JobApplicationDTO application, LocalDate placementDate, byte[] offerLetterUrl,
                        double placedPackage, String remarks, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.application = application;
        this.placementDate = placementDate;
        this.offerLetterUrl = offerLetterUrl;
        this.placedPackage = placedPackage;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobApplicationDTO getApplication() {
        return application;
    }

    public void setApplication(JobApplicationDTO application) {
        this.application = application;
    }

    public LocalDate getPlacementDate() {
        return placementDate;
    }

    public void setPlacementDate(LocalDate placementDate) {
        this.placementDate = placementDate;
    }

    public byte[] getOfferLetterUrl() {
        return offerLetterUrl;
    }

    public void setOfferLetterUrl(byte[] offerLetterUrl) {
        this.offerLetterUrl = offerLetterUrl;
    }

    public double getPlacedPackage() {
        return placedPackage;
    }

    public void setPlacedPackage(double placedPackage) {
        this.placedPackage = placedPackage;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
