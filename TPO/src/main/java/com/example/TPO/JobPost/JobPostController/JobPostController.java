package com.example.TPO.JobPost.JobPostController;

import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.JobPost.JobPostDTO.JobPostDTO;
import com.example.TPO.JobPost.JobPostService.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @GetMapping("/all")
    public ResponseEntity<List<JobPostDTO>> getAllJobPosts() {
        return ResponseEntity.ok(jobPostService.getAllJobPosts());
    }
    @GetMapping("/posts/student")
    public ResponseEntity<List<JobPostDTO>> getElegiblePosts(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);

        return ResponseEntity.ok(jobPostService.getEligiblePosts(token));
    }
    @GetMapping("/get/post")
    public ResponseEntity<?> getPostByBody(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long post_id
    ) {
        String token = "";
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
             token = authHeader.substring(7);}

        return jobPostService.getEligiblePost(token, post_id); // Reuse existing logic
    }}



