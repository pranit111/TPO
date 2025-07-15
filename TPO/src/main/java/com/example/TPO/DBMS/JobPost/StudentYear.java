package com.example.TPO.DBMS.JobPost;

import java.time.Year;

/**
 * Enum representing the academic year levels for which a job post is intended.
 * FE = First Year (First Engineering)
 * SE = Second Year (Second Engineering)  
 * TE = Third Year (Third Engineering)
 * BE = Final Year (Bachelor of Engineering)
 */
public enum StudentYear {
    FE("First Year"),
    SE("Second Year"), 
    TE("Third Year"),
    BE("Final Year");
    
    private final String displayName;
    
    StudentYear(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * Determines the current student year level based on the academic year string.
     * This method calculates the student's current year based on when they started
     * and the current academic year.
     * 
     * @param academicYear The academic year string (e.g., "2024-2025")
     * @param yearOfJoining The year when the student joined (e.g., 2022)
     * @return The corresponding StudentYear enum
     */
    public static StudentYear determineStudentYear(String academicYear, int yearOfJoining) {
        if (academicYear == null || academicYear.isEmpty()) {
            return BE; // Default to final year if no academic year provided
        }
        
        try {
            // Extract the starting year from academic year string (e.g., "2024-2025" -> 2024)
            String[] parts = academicYear.split("-");
            int currentAcademicYear = Integer.parseInt(parts[0]);
            
            // Calculate which year the student is in
            int yearLevel = currentAcademicYear - yearOfJoining + 1;
            
            switch (yearLevel) {
                case 1:
                    return FE;
                case 2:
                    return SE;
                case 3:
                    return TE;
                case 4:
                default:
                    return BE;
            }
        } catch (Exception e) {
            // If parsing fails, default to final year
            return BE;
        }
    }
    
    /**
     * Alternative method using current date to determine student year
     * @param yearOfJoining The year when student joined
     * @return The corresponding StudentYear enum
     */
    public static StudentYear determineCurrentStudentYear(int yearOfJoining) {
        int currentYear = Year.now().getValue();
        int yearLevel = currentYear - yearOfJoining + 1;
        
        switch (yearLevel) {
            case 1:
                return FE;
            case 2:
                return SE;
            case 3:
                return TE;
            case 4:
            default:
                return BE;
        }
    }
}
