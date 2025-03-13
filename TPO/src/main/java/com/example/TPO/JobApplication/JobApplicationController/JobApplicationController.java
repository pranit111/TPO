package com.example.TPO.JobApplication.JobApplicationController;

import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.Filters.JobApplicationFilter;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import com.example.TPO.JobApplication.JobApplicationService.JobApplicationService;
import org.aspectj.weaver.patterns.IToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("http://localhost:4200")
@RequestMapping("api4")
@RestController
public class JobApplicationController {
@Autowired
JobApplicationService jobApplicationService;
@PostMapping("/Application")
public ResponseEntity<Map<String,String >> createapplication(@RequestParam long postid, @RequestHeader("Authorization") String authHeader){
    String token = authHeader.substring(7);

    return jobApplicationService.createApplication(postid,token);

}
@GetMapping("/Applications")
   public ResponseEntity<?> getApplications(@RequestHeader("Authorization") String authHeader){

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        authHeader = authHeader.substring(7);
        return jobApplicationService.getApplications(authHeader);}
    Map<String, String> response = new HashMap<>();
    response.put("error", "No Applications Found");
   return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);


}
@GetMapping("/Application")
    public ResponseEntity<?> getapplication(@RequestParam long application_id){
    return jobApplicationService.getApplication(application_id);

}
@PutMapping("/Application")
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
