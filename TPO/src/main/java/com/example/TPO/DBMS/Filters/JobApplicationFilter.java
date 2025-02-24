package com.example.TPO.DBMS.Filters;

public class JobApplicationFilter {
    private String status;
    private String location;

    public JobApplicationFilter() {
    }

    public JobApplicationFilter(String status, String location, Long minSalary, Long maxSalary) {
        this.status = status;
        this.location = location;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }

    private Long minSalary;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Long minSalary) {
        this.minSalary = minSalary;
    }

    public Long getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Long maxSalary) {
        this.maxSalary = maxSalary;
    }

    private Long maxSalary;
}
