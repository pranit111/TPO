package com.example.TPO.JobApplication.JobApplicationController;

import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.Filters.JobApplicationFilter;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import com.example.TPO.JobApplication.JobApplicationService.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin("http://localhost:4200")
@RequestMapping("api4")
@RestController
public class JobApplicationController {
@Autowired
JobApplicationService jobApplicationService;
@PostMapping("/create/Application")
public ResponseEntity<String> createapplication(@RequestParam long postid, @RequestHeader("Authorization") String authHeader){
    String token = authHeader.substring(7);

    return jobApplicationService.createApplication(postid,token);

}
@GetMapping("/get/Application")
    public ResponseEntity<?> getapplication(@RequestParam long application_id){
    return jobApplicationService.getApplication(application_id);

}
@PutMapping("/update/Application")
    public ResponseEntity<?> updateapplication(@RequestParam long application_id,@RequestBody JobApplicationDTO jobApplicationDTO){
    return jobApplicationService.updateApplication(application_id,jobApplicationDTO);
}
@PostMapping("filter/Applications")
    public ResponseEntity<?> filterapplications(@RequestBody JobApplicationFilter jobApplicationFilter){
    return jobApplicationService.filterJobApplications(jobApplicationFilter);
}
@PostMapping("filter/Application/download")
    public ResponseEntity<?> downloadfilteredapp(@RequestBody JobApplicationFilter jobApplicationFilter){
    return jobApplicationService.downloadExcel(jobApplicationFilter);
}

}
