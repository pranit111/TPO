package com.example.TPO.JobPost.JobPostController;

import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.JobPost.JobPostDTO.JobPostDTO;
import com.example.TPO.JobPost.JobPostService.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api3/")
@RestController
public class JobPostController {
    @Autowired
    JobPostService jobPostService;
    @PostMapping("/post/job")
    public ResponseEntity<? > createpost(@RequestBody JobPost jobPost, @RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);

        return  jobPostService.createPost(jobPost,token);

    }
    @PutMapping("Update/job")
    public ResponseEntity<?> update(@RequestParam long p_id ,@RequestBody JobPostDTO updatedjobPost){
        return  jobPostService.updateJobPost(p_id,updatedjobPost);
    }

}
