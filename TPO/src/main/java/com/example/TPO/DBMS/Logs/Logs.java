package com.example.TPO.DBMS.Logs;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
public class Logs {
    public Logs() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Logs(Long id, String action, String performedBy, LocalDateTime timestamp, String entityName, String entityId, String details) {
        this.id = id;
        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.entityName = entityName;
        this.entityId = entityId;
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String action; // What action was performed
    private String performedBy; // Who performed the action

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // Auto-set timestamp

    private String entityName; // (Optional) Name of the affected entity, e.g., "User"
    private String entityId; // (Optional) ID of the affected entity, e.g., "123"

    @Column(columnDefinition = "TEXT")
    private String details; // (Optional) Store additional info like request data or error message

    // Constructors


    // Getters & Setters
}
