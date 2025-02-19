package com.example.TPO.JobPost.JobPostService;

import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.JobPost.JobPostRepository.JobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobPostService {
    @Autowired
    JobPostRepository jobPostRepository;
    public String createpost(JobPost jobPost,String token){

    }
}
