package com.example.TPO.DBMS.Placements;

import com.example.TPO.DBMS.Applications.JobApplication;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "placements")
public class Placement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Column(name = "placement_date")
    private LocalDate placementDate;
    @Lob
    @Column(name = "offer_letter_url", columnDefinition = "LONGBLOB")
    private byte[] offerLetterUrl;

    @Column (name="package")
    private double placed_package;
    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Placement() {
    }

    public Placement(Long id, JobApplication application, LocalDate placementDate, byte[] offerLetterUrl, double placed_package, String remarks, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.application = application;
        this.placementDate = placementDate;
        this.offerLetterUrl = offerLetterUrl;
        this.placed_package = placed_package;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public double getPlaced_package() {
        return placed_package;
    }

    public void setPlaced_package(double placed_package) {
        this.placed_package = placed_package;
    }
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobApplication getApplication() {
        return application;
    }

    public void setApplication(JobApplication application) {
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
// Getters and Setters
    // (You can use Lombok @Data to generate them automatically)
}
