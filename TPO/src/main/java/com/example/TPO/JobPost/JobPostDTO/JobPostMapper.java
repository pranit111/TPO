package com.example.TPO.JobPost.JobPostDTO;

import com.example.TPO.DBMS.JobPost.JobPost;

public class JobPostMapper {
    public static JobPostDTO toJobPostDTO(JobPost jobPost) {
        if (jobPost == null) return null;

        return new JobPostDTO(
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
    }
}
