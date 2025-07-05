package com.example.TPO.JobApplication.JobApplicationService;

import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.Filters.JobApplicationFilter;
import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationMapper;
import com.example.TPO.JobApplication.JobApplicationRepository.JobApplicationRepository;
import com.example.TPO.JobPost.JobPostRepository.JobPostRepository;
import com.example.TPO.JobPost.JobPostService.JobPostService;
import com.example.TPO.Logs.LogsService.LogsService;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private JobPostRepository jobPostRepository;
@Autowired
    LogsService logsService;
@Autowired
    JobPostService jobPostService;
    //  Create Job Application
    public ResponseEntity<Map<String,String>> createApplication(long postId, String token) {
        Long userId = jwtService.extractUserId(token);

        // Get User
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of( "error","User not found"));
        }

        // Get JobPost
        Optional<JobPost> jobPostOptional = jobPostRepository.findById(postId);
        if (jobPostOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of( "error","No Job Post found with ID: " + postId));
        }

        // Get Student Profile
        Optional<Student> studentOptional = studentRepository.findByUserId(userId);
        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of( "error","Student profile not found for User ID: " + userId));
        }

        // Check if application already exists for this user & job post
        boolean alreadyApplied = jobApplicationRepository.existsByStudentIdAndJobPostId(
                studentOptional.get().getId(), postId);
        if (alreadyApplied) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "You Already Had Applied For This Job"));
        }
        //Check Eligibilty


        // Create Job Application
        JobApplication jobApplication = new JobApplication();
        jobApplication.setApplicationDate(LocalDate.now());
        jobApplication.setStatus(ApplicationStatus.APPLIED);
        jobApplication.setStudent(studentOptional.get());
        jobApplication.setJobPost(jobPostOptional.get());
        jobApplicationRepository.save(jobApplication);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Application submitted successfully"));
    }

    //  Get Job Application (Returns DTO)
    public ResponseEntity<?> getApplication(long applicationId) {
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository.findById(applicationId);

        if (jobApplicationOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Application Found with ID: " + applicationId);
        }

        JobApplication jobApplication = jobApplicationOptional.get();
        JobApplicationDTO jobApplicationDTO = JobApplicationMapper.toJobApplicationDTO(jobApplication);

        return ResponseEntity.ok(jobApplicationDTO);
    }
//gGet list of Application according to student
    public ResponseEntity<List<JobApplicationDTO>> getApplications(String token) {
        List<JobApplication> optionalJobApplication=jobApplicationRepository.findByStudentId(studentRepository.findByUserId(jwtService.extractUserId(token)).get().getId());

        return ResponseEntity.ok(JobApplicationMapper.toJobApplicationDTOList(optionalJobApplication));

    }
    public ResponseEntity<?> updateApplication(long applicationId, JobApplicationDTO updatedJobApplicationDTO, String authheader) {
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository.findById(applicationId);
        String authToken="";
        if (authheader  != null && authheader.startsWith("Bearer ")) {
            authToken = authheader.substring(7);}

        if (jobApplicationOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Application Found with ID: " + applicationId);
        }

        JobApplication jobApplication = jobApplicationOptional.get();
        Optional<User> userOptional = userRepo.findById(jwtService.extractUserId(authToken));

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }

        User user = userOptional.get();
        boolean statusUpdated = false;
        StringBuilder changes = new StringBuilder();

        // ✅ Update Status and Log Changes
        if (updatedJobApplicationDTO.getStatus() != null) {
            ApplicationStatus newStatus = ApplicationStatus.valueOf(updatedJobApplicationDTO.getStatus());
            if (!jobApplication.getStatus().equals(newStatus)) {
                changes.append("Status changed from ")
                        .append(jobApplication.getStatus())
                        .append(" to ")
                        .append(newStatus)
                        .append("; ");
                jobApplication.setStatus(newStatus);
                statusUpdated = true;
            }
        }

        // ✅ Update Application Date (if provided)
        if (updatedJobApplicationDTO.getInterviewDate() != null) {
            jobApplication.setInterviewDate(updatedJobApplicationDTO.getInterviewDate());
        }
        if (updatedJobApplicationDTO.getInterviewLocation() != null) {
            jobApplication.setInterviewLocation(updatedJobApplicationDTO.getInterviewLocation());
        }
        if (updatedJobApplicationDTO.getInterviewTime() != null) {
            jobApplication.setInterviewTime(updatedJobApplicationDTO.getInterviewTime());
        }

        // ✅ Update Feedback
        if (updatedJobApplicationDTO.getFeedback() != null) {
            jobApplication.setFeedback(updatedJobApplicationDTO.getFeedback());
        }

        // ✅ Update Job Post (if changed)
        if (updatedJobApplicationDTO.getJobPost() != null) {
            Optional<JobPost> jobPostOptional = jobPostRepository.findById(updatedJobApplicationDTO.getJobPost().getId());
            if (jobPostOptional.isPresent()) {
                jobApplication.setJobPost(jobPostOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Job Post not found with ID: " + updatedJobApplicationDTO.getJobPost().getId());
            }
        }

        // ✅ Save the updated application
        jobApplicationRepository.save(jobApplication);

        // ✅ Log the status update
        if (statusUpdated) {
            logsService.saveLog(
                    "Application Status Updated",
                    user.getUsername(),
                    "JobApplication",
                    String.valueOf(jobApplication.getId()),
                    changes.toString()
            );
        }

        // ✅ Convert updated application to DTO and return response
        JobApplicationDTO updatedDTO = JobApplicationMapper.toJobApplicationDTO(jobApplication);
        return ResponseEntity.ok(updatedDTO);
    }


    public ResponseEntity<List<JobApplicationDTO>> filterJobApplications(JobApplicationFilter jobApplicationFilter) {
        System.err.println(jobApplicationFilter.getStatus());
        ApplicationStatus status=null;
        if (jobApplicationFilter.getStatus() != null) {
            try {
                status = ApplicationStatus.valueOf(jobApplicationFilter.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }
        }
        List<JobApplication> filteredApplications = jobApplicationRepository.filterJobApplications(status, jobApplicationFilter.getLocation(), jobApplicationFilter.getMinSalary(), jobApplicationFilter.getMaxSalary());

        List<JobApplicationDTO> filteredApplicationDTOs = filteredApplications.stream()
                .map(JobApplicationMapper::toJobApplicationDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredApplicationDTOs);
    }
    public ResponseEntity<byte[]> downloadExcel(JobApplicationFilter jobApplicationFilter) {
        ResponseEntity<List<JobApplicationDTO>> response = filterJobApplications(jobApplicationFilter);

        List<JobApplicationDTO> filteredApplications = response.getBody();
        if (filteredApplications == null || filteredApplications.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Job Applications");
            Row headerRow = sheet.createRow(0);

            // Define headers
            String[] headers = {"ID", "Job Title", "Company", "Status", "Applied On"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Populate data rows
            int rowNum = 1;
            for (JobApplicationDTO jobApp : filteredApplications) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(jobApp.getId());
                row.createCell(1).setCellValue(jobApp.getJobPost().getJobDesignation());
                row.createCell(2).setCellValue(jobApp.getJobPost().getCompany().getName());
                row.createCell(3).setCellValue(jobApp.getStatus());
                row.createCell(4).setCellValue(jobApp.getApplicationDate().toString());
            }

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] excelBytes = outputStream.toByteArray();

            // Set response headers for download
            HttpHeaders headersObj = new HttpHeaders();
            headersObj.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headersObj.setContentDisposition(ContentDisposition.attachment().filename("JobApplications.xlsx").build());

            return ResponseEntity.ok().headers(headersObj).body(excelBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public Page<JobApplicationDTO> searchApplication(ApplicationStatus status,
                                                 String location,
                                               Long minSalary,
                                                Long maxSalary,
                                                    String student,
                                            String department,
                                             String jobType,
                                        String designation,
                                                LocalDate fromDate,LocalDate toDate,
                                                    String searchTerm,
                                                int page,
                                                int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobApplication> jobApplicationspage = jobApplicationRepository.searchJobApplications(status,location,minSalary,maxSalary,student,department,jobType,designation,fromDate,toDate,searchTerm,pageable);

        return jobApplicationspage.map(JobApplicationMapper::toJobApplicationDTO);
    }
    public ResponseEntity<byte[]> searchDownloadExcel(ApplicationStatus status,
                                                      String location,
                                                      Long minSalary,
                                                      Long maxSalary,
                                                      String student,
                                                      String department,
                                                      String jobType,
                                                      String designation,
                                                      LocalDate fromDate,LocalDate toDate,
                                                      String searchTerm,
                                                      int page,
                                                      int size) {
        Page<JobApplicationDTO> response = searchApplication(status,location,minSalary,maxSalary,student,department,jobType,designation,fromDate,toDate,searchTerm,page,size);

        List<JobApplicationDTO> filteredApplications = response.getContent();
        if (filteredApplications == null || filteredApplications.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Job Applications");
            Row headerRow = sheet.createRow(0);

            // Define headers
            String[] headers = {"ID","Student", "Job Title", "Company", "Status", "Applied On","Interview Date"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Populate data rows
            int rowNum = 1;
            for (JobApplicationDTO jobApp : filteredApplications) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(jobApp.getId());
                row.createCell(1).setCellValue(jobApp.getStudent().getFirstName()+" "+jobApp.getStudent().getLastName());
                row.createCell(2).setCellValue(jobApp.getJobPost().getJobDesignation());
                row.createCell(3).setCellValue(jobApp.getJobPost().getCompany().getName()+" "+(jobApp.getJobPost().getCompany().isMnc()?"MNC":" "));
                row.createCell(4).setCellValue(jobApp.getStatus());
                row.createCell(5).setCellValue(jobApp.getApplicationDate().toString());
                row.createCell(6).setCellValue(jobApp.getInterviewDate() != null ? jobApp.getInterviewDate().toString() : "");


            }

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] excelBytes = outputStream.toByteArray();

            // Set response headers for download
            HttpHeaders headersObj = new HttpHeaders();
            headersObj.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headersObj.setContentDisposition(ContentDisposition.attachment().filename("JobApplications.xlsx").build());

            return ResponseEntity.ok().headers(headersObj).body(excelBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
