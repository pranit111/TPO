package com.example.TPO.JobPost.JobPostController;

import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.JobPost.JobPostDTO.JobPostDTO;
import com.example.TPO.JobPost.JobPostService.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("api3/")
@RestController
public class JobPostController {
    @Autowired
    JobPostService jobPostService;
    @PostMapping("Post")
    public ResponseEntity<Map<String, String>> createPost(
            @RequestBody JobPost jobPost,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Missing or invalid Authorization header"
            ));
        }

        String token = authHeader.substring(7);
        return jobPostService.createPost(jobPost, token);
    }


    @PutMapping("Post")
    public ResponseEntity<?> update(@RequestParam long post_id ,@RequestBody JobPostDTO updatedjobPost){
        return  jobPostService.updateJobPost(post_id,updatedjobPost);
    }
    @GetMapping("Posts")
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
    }
    @GetMapping("Post/tpo")
    public ResponseEntity<?> getpostpo( @RequestHeader("Authorization") String authHeader,
                                        @RequestParam Long post_id){
        String token = authHeader.substring(7);
        return jobPostService.getPostTpo(token,post_id);
    }

    @GetMapping("Post/Search")
    public Page<JobPostDTO> searchJobPosts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("Searching Job Posts - Status: " + status + ", Company: " + company);

        return jobPostService.searchPost(status, company, position, jobType, minSalary, maxSalary, page, size);
    }
    @PostMapping("Post/Search/Download")
    public ResponseEntity<?> searchJobPostsDownload(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {



        return jobPostService.SearchdownloadExcel(status, company, position, jobType, minSalary, maxSalary, page, size);
    }

}



