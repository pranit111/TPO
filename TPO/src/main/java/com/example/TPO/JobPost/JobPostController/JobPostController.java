package com.example.TPO.JobPost.JobPostController;

import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.DBMS.JobPost.StudentYear;
import com.example.TPO.JobPost.JobPostDTO.JobPostDTO;
import com.example.TPO.JobPost.JobPostService.JobPostService;
import com.example.TPO.UserManagement.Service.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    TokenExtractor tokenExtractor;
    @PostMapping("Post")
    public ResponseEntity<Map<String, String>> createPost(
            @RequestBody JobPost jobPost,
            HttpServletRequest request) {

        String token = tokenExtractor.extractToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Missing or invalid Authorization"
            ));
        }

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
    public ResponseEntity<List<JobPostDTO>> getElegiblePosts(HttpServletRequest request){
        String token = tokenExtractor.extractToken(request);

        return ResponseEntity.ok(jobPostService.getEligiblePosts(token));
    }
    @GetMapping("/get/post")
    public ResponseEntity<?> getPostByBody(
            HttpServletRequest request,
            @RequestParam Long post_id
    ) {
        String token = tokenExtractor.extractToken(request);

        return jobPostService.getEligiblePost(token, post_id); // Reuse existing logic
    }
    @GetMapping("Post/tpo")
    public ResponseEntity<?> getpostpo(HttpServletRequest request,
                                        @RequestParam Long post_id){
        String token = tokenExtractor.extractToken(request);
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
            @RequestParam(required = false) StudentYear studentYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("Searching Job Posts - Status: " + status + ", Company: " + company + ", Student Year: " + studentYear);

        return jobPostService.searchPost(status, company, position, jobType, minSalary, maxSalary, studentYear, page, size);
    }
    @PostMapping("Post/Search/Download")
    public ResponseEntity<?> searchJobPostsDownload(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) StudentYear studentYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return jobPostService.SearchdownloadExcel(status, company, position, jobType, minSalary, maxSalary, studentYear, page, size);
    }

    // New endpoint to get job posts by student year
    @GetMapping("Posts/by-year/{studentYear}")
    public ResponseEntity<List<JobPostDTO>> getJobPostsByStudentYear(@PathVariable StudentYear studentYear) {
        return ResponseEntity.ok(jobPostService.getJobPostsByStudentYear(studentYear));
    }

    // Endpoint to get all available student years
    @GetMapping("student-years")
    public ResponseEntity<StudentYear[]> getAllStudentYears() {
        return ResponseEntity.ok(StudentYear.values());
    }

}



