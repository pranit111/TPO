package com.example.TPO.JobPost.JobPostService;

import com.example.TPO.Companies.CompaniesRepository.CompaniesRepository;
import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.Company.Company;
import com.example.TPO.DBMS.JobPost.JobPost;
import com.example.TPO.DBMS.JobPost.JobPostStatus;
import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationMapper;
import com.example.TPO.JobPost.JobPostDTO.JobPostDTO;
import com.example.TPO.JobPost.JobPostDTO.JobPostMapper;
import com.example.TPO.JobPost.JobPostRepository.JobPostRepository;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.formula.functions.T;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobPostService {
    @Autowired
    JobPostRepository jobPostRepository;
    @Autowired
    TpoRepository tpoRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    JWTService jwtService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    CompaniesRepository companiesRepository;

    public ResponseEntity<Map<String, String>> createPost(JobPost jobPost, String token) {
        try {
            Company company = companiesRepository.findById(jobPost.getCompany().getId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "status", "error",
                        "message", "Invalid or expired token"
                ));
            }

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TPOUser tpoUser = tpoRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("TPO User not found for this user"));

            jobPost.setCompany(company);
            jobPost.setCreatedBy(tpoUser);
            jobPostRepository.save(jobPost);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Job post created successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Internal Server Error: " + e.getMessage()
            ));
        }
    }

    public ResponseEntity<?> updateJobPost(long jobPostId, JobPostDTO jobPostDTO) {
        Optional<JobPost> jobPostOptional = jobPostRepository.findById(jobPostId);

        if (jobPostOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Job Post Found with ID: " + jobPostId);
        }

        JobPost jobPost = jobPostOptional.get();

        // Update all fields if provided
        if (jobPostDTO.getJobDesignation() != null) {
            jobPost.setJobDesignation(jobPostDTO.getJobDesignation());
        }
        if (jobPostDTO.getDescription() != null) {
            jobPost.setDescription(jobPostDTO.getDescription());
        }
        if (jobPostDTO.getLocation() != null) {
            jobPost.setLocation(jobPostDTO.getLocation());
        }
        if (jobPostDTO.getJobType() != null) {
            jobPost.setJobType(jobPostDTO.getJobType());
        }
        if (jobPostDTO.getStatus() != null) {
            jobPost.setStatus(jobPostDTO.getStatus());
        }
        if (jobPostDTO.getPackageAmount() != 0) {
            jobPost.setPackageAmount(jobPostDTO.getPackageAmount());
        }
        if (jobPostDTO.getMinPercentage() != 0) {
            jobPost.setMinPercentage(jobPostDTO.getMinPercentage());
        }
        if (jobPostDTO.getBacklogAllowance() != 0) {
            jobPost.setBacklogAllowance(jobPostDTO.getBacklogAllowance());
        }
        if (jobPostDTO.getPreferredCourse() != null) {
            jobPost.setPreferredCourse(jobPostDTO.getPreferredCourse());
        }
        if (jobPostDTO.getSkillRequirements() != null) {
            jobPost.setSkillRequirements(jobPostDTO.getSkillRequirements());
        }
        if (jobPostDTO.getSelectionRounds() != null) {
            jobPost.setSelectionRounds(jobPostDTO.getSelectionRounds());
        }
        if (jobPostDTO.getModeOfRecruitment() != null) {
            jobPost.setModeOfRecruitment(jobPostDTO.getModeOfRecruitment());
        }
        if (jobPostDTO.getTestPlatform() != null) {
            jobPost.setTestPlatform(jobPostDTO.getTestPlatform());
        }
        if (jobPostDTO.getApplicationStartDate() != null) {
            jobPost.setApplicationStartDate(jobPostDTO.getApplicationStartDate());
        }
        if (jobPostDTO.getApplicationEndDate() != null) {
            jobPost.setApplicationEndDate(jobPostDTO.getApplicationEndDate());
        }
        if (jobPostDTO.getSelectionStartDate() != null) {
            jobPost.setSelectionStartDate(jobPostDTO.getSelectionStartDate());
        }
        if (jobPostDTO.getSelectionEndDate() != null) {
            jobPost.setSelectionEndDate(jobPostDTO.getSelectionEndDate());
        }
        if (jobPostDTO.getAptitudeDate() != null) {
            jobPost.setAptitudedate(jobPostDTO.getAptitudeDate());
        }
        if (jobPostDTO.isAptitude()) {

            jobPost.setAptitude(jobPostDTO.isAptitude());


        }
        if (jobPostDTO.isAptitude()==false) {

            jobPost.setAptitude(jobPostDTO.isAptitude());


        }
        if (jobPostDTO.getPortalLink() != null) {
            jobPost.setPortalLink(jobPostDTO.getPortalLink());
        }


        jobPostRepository.save(jobPost);

        return ResponseEntity.ok("Job post updated successfully.");
    }


    public List<JobPostDTO> getAllJobPosts() {
        List<JobPost> jobPosts = jobPostRepository.findAll();
        return JobPostMapper.toJobPostDTOList(jobPosts);
    }

    public List<JobPostDTO> getEligiblePosts(String Token) {
        Optional<Student> optionalStudent = studentRepository.findByUserId(jwtService.extractUserId(Token));

        if (!optionalStudent.isPresent()) {
            throw new RuntimeException("Student not found"); // Handle missing student scenario
        }

        Student student = optionalStudent.get();
        List<JobPost> allJobPosts = jobPostRepository.findAll();

        List<JobPost> eligibleJobPosts = allJobPosts.stream()
                .filter(jobPost -> isStudentEligibleForJob(student, jobPost))
                .filter(jobPost -> jobPost.getStatus() != JobPostStatus.CANCELLED &&
                        jobPost.getStatus() != JobPostStatus.ON_HOLD &&
                        jobPost.getStatus() != JobPostStatus.EXPIRED &&  jobPost.getStatus() != JobPostStatus.UPCOMING)

                .collect(Collectors.toList());

        return JobPostMapper.toJobPostDTOList(eligibleJobPosts);
    }

    private boolean isStudentEligibleForJob(Student student, JobPost jobPost) {
        Double requiredSsc = jobPost.getMinimumSsc();
        Double requiredHsc = jobPost.getMinimumHsc();
        Double requiredDiploma = jobPost.getMinimumHsc(); // Possible typo? Should be `getMinimumDiploma()` if exists
        Double requiredMinAvg = jobPost.getMinPercentage();
        Double reqbacklogAllowance= ((double) jobPost.getBacklogAllowance());
        Double studbacklog= ((double) student.getNoOfBacklogs());
        Double studentSsc = student.getSscMarks();
        Double studentHsc = student.getHscMarks();
        Double studentDiploma = student.getDiplomaMarks();
        Double studentMinAvg = student.getAvgMarks();

        // Ensure student meets all required criteria (SSC, HSC/Diploma, and Min Percentage)
        boolean sscEligible = requiredSsc == null || (studentSsc != null && studentSsc >= requiredSsc);
        boolean hscEligible = requiredHsc == null || (studentHsc != null && studentHsc >= requiredHsc);
        boolean backlog = reqbacklogAllowance == null || (studbacklog != null && studbacklog <= reqbacklogAllowance);
        boolean diplomaEligible = requiredDiploma == null || (studentDiploma != null && studentDiploma >= requiredDiploma);
        boolean avgEligible = requiredMinAvg == null || (studentMinAvg != null && studentMinAvg >= requiredMinAvg);

        // Student must satisfy SSC & (HSC or Diploma) & Minimum Percentage
        return sscEligible && (hscEligible || diplomaEligible) && avgEligible&& backlog;
    }
    public ResponseEntity<?> getPostTpo(String authHeader, Long postId) {

        Optional<User> userOptional = userRepo.findById(jwtService.extractUserId(authHeader));

        if (userOptional.isPresent()) {
            TPOUser tpoUser = userOptional.get().getTpoUser();

            if (tpoUser != null) {
                Optional<JobPost> jobPostOptional = jobPostRepository.findById(postId);

                if (jobPostOptional.isPresent()) {

                    return ResponseEntity.ok(JobPostMapper.toJobPostDTO( jobPostOptional.get()));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not a TPO user");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
    }



    public ResponseEntity<?> getEligiblePost(String authHeader, Long postId) {
        System.err.println(authHeader);
        Optional<Student> optstud= studentRepository.findByUserId(jwtService.extractUserId(authHeader));
        Optional<JobPost> jobPost=jobPostRepository.findById(postId);
        JobPostDTO jobPostDTO=JobPostMapper.toJobPostDTO(jobPost.get());
        if(isStudentEligibleForJob(optstud.get(),jobPost.get())){
            return ResponseEntity.ok(jobPostDTO);
        }
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error POST Cannot be fetched");
    }
    public Page<JobPostDTO> searchPost(String status,
                                                   String company,
                                                    String position,
                                                    String jobType,
                                                    Double minSalary,
                                                    Double maxSalary,

                                                    int page,
                                                    int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPost> jobPostPage = jobPostRepository.searchJobApplications(status,company,position,jobType,minSalary,maxSalary,pageable);

        return jobPostPage.map(JobPostMapper::toJobPostDTO);
    }
    public ResponseEntity<byte[]> SearchdownloadExcel(String status,
                                                      String company,
                                                      String position,
                                                      String jobType,
                                                      Double minSalary,
                                                      Double maxSalary,

                                                      int page,
                                                      int size) {
        Page<JobPostDTO> response = searchPost(status,company,position,jobType,minSalary,maxSalary,page,size);

        List<JobPostDTO> filteredPost = response.getContent();
        if (filteredPost == null || filteredPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Job Post");
            Row headerRow = sheet.createRow(0);

            // Define headers
            String[] headers = {"ID", "Company", "Designation", "Location", "Status", "Package", "Backlog Allowance", "Minimum Percentage"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

// Populate data rows
            int rowNum = 1;
            int generatedId = 1; // Start ID from 1

            for (JobPostDTO jobpost : filteredPost) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(generatedId++); // Auto-increment ID
                row.createCell(1).setCellValue(jobpost.getCompany().getName());
                row.createCell(2).setCellValue(jobpost.getJobDesignation());
                row.createCell(3).setCellValue(jobpost.getLocation());
                row.createCell(4).setCellValue(jobpost.getStatus().toString());
                row.createCell(5).setCellValue(jobpost.getPackageAmount());
                row.createCell(6).setCellValue(jobpost.getBacklogAllowance());
                row.createCell(7).setCellValue(jobpost.getMinPercentage());
            }

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] excelBytes = outputStream.toByteArray();

            // Set response headers for download
            HttpHeaders headersObj = new HttpHeaders();
            headersObj.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headersObj.setContentDisposition(ContentDisposition.attachment().filename("JobPost.xlsx").build());

            return ResponseEntity.ok().headers(headersObj).body(excelBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
