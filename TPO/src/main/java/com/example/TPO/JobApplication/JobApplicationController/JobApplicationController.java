package com.example.TPO.JobApplication.JobApplicationController;

import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Filters.JobApplicationFilter;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import com.example.TPO.JobApplication.JobApplicationService.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
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
    public ResponseEntity<?> updateapplication(@RequestParam long application_id,@RequestBody JobApplicationDTO jobApplicationDTO,@RequestHeader("Authorization") String authHeader){
    return jobApplicationService.updateApplication(application_id,jobApplicationDTO,authHeader);
}
@PostMapping("filter/Applications")
    public ResponseEntity<?> filterapplications(@RequestBody JobApplicationFilter jobApplicationFilter){
    return jobApplicationService.filterJobApplications(jobApplicationFilter);
}
@PostMapping("filter/Application/download")
    public ResponseEntity<?> downloadfilteredapp(@RequestBody JobApplicationFilter jobApplicationFilter){
    return jobApplicationService.downloadExcel(jobApplicationFilter);
}
    @GetMapping("/Application/Search")
    public Page<JobApplicationDTO> searchApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Long maxSalary,
            @RequestParam(required = false) String student,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        System.err.println(status);
        return jobApplicationService.searchApplication(status, location, minSalary, maxSalary, student, department, jobType, designation, fromDate, toDate, searchTerm, page, size);
    }
    @PostMapping("/Application/Search/Download")
    public ResponseEntity<?> searchApplicationsDownload(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) Long maxSalary,
            @RequestParam(required = false) String student,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        System.err.println(status);
        return jobApplicationService.searchDownloadExcel(status, location, minSalary, maxSalary, student, department, jobType, designation, fromDate, toDate, searchTerm, page, size);
    }
}
