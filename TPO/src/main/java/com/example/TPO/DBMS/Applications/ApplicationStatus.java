package com.example.TPO.DBMS.Applications;
public enum ApplicationStatus {
    APPLIED, UNDER_REVIEW, SHORTLISTED, REJECTED, OFFERED, HIRED;

    public static boolean equalsIgnoreCase(String status) {
        if (status == null) {
            return false;
        }
        for (ApplicationStatus applicationStatus : values()) {
            if (applicationStatus.name().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }
}

