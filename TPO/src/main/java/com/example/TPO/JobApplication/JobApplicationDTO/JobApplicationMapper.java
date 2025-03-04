package com.example.TPO.JobApplication.JobApplicationDTO;

import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.JobPost.JobPostDTO.JobPostDTO;
import com.example.TPO.Student.StudentDTO.StudentDTO;
import com.example.TPO.UserManagement.UserDTO.UserDTO;
import com.example.TPO.UserManagement.entity.User;

public class JobApplicationMapper {
    public static JobApplicationDTO toJobApplicationDTO(JobApplication jobApplication) {
        if (jobApplication == null) return null;

        // Convert Student entity to StudentDTO
        Student student = jobApplication.getStudent();
        User user = student.getUser();
        StudentDTO studentDTO = new StudentDTO(
                student.getId(),
                new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole()),
                student.getFirstName(),
                student.getMiddleName(),
                student.getLastName(),
                student.getDateOfBirth(),
                student.getGender().toString(),
                student.getPhoneNumber(),
                student.getAddress(),
                student.getDepartment(),
                student.getSscMarks(),
                student.getHscMarks(),
                student.getDiplomaMarks(),
                student.getSem1Marks(),
                student.getSem2Marks(),
                student.getSem3Marks(),
                student.getSem4Marks(),
                student.getSem5Marks(),
                student.getSem6Marks(),
                student.getNoOfBacklogs(),
                student.getAvgMarks(),
                student.getGr_No()
        );

        // Convert JobPost entity to JobPostDTO
        JobPost jobPost = jobApplication.getJobPost();
        JobPostDTO jobPostDTO = new JobPostDTO(
                jobPost.getId(),
                jobPost.getCompanyName(),
                jobPost.getJobDesignation(),
                jobPost.getLocation(),
                jobPost.getJobType(),
                jobPost.getDescription(),
                jobPost.getPackageAmount(),
                jobPost.getMinPercentage(),
                jobPost.getBacklogAllowance(),
                jobPost.getPreferredCourse(),
                jobPost.getSkillRequirements(),
                jobPost.getSelectionRounds(),
                jobPost.getModeOfRecruitment(),
                jobPost.getTestPlatform(),
                jobPost.getApplicationStartDate(),
                jobPost.getApplicationEndDate(),
                jobPost.getSelectionStartDate(),
                jobPost.getSelectionEndDate()
        );

        return new JobApplicationDTO(
                jobApplication.getId(),
                studentDTO,
                jobPostDTO,
                jobApplication.getApplicationDate(),
                jobApplication.getStatus().toString()
        );
    }
}
