package com.example.TPO.Student.StudentService;

import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.Filters.JobApplicationFilter;
import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.DBMS.Tpo.TPO_Role;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationMapper;
import com.example.TPO.Logs.LogsService.LogsService;
import com.example.TPO.Student.StudentDTO.StudentBasicDTO;
import com.example.TPO.Student.StudentDTO.StudentBasicMapper;
import com.example.TPO.Student.StudentDTO.StudentDTO;
import com.example.TPO.Student.StudentDTO.StudentMapper;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserDTO.UserDTO;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    UserRepo userRepo;
    @Autowired
    JWTService jwtService;
    @Autowired
    LogsService logsService;
    @Autowired
    TpoRepository tpoRepository;
    public void createstudent(Student student, String authtoken, MultipartFile prof_img, MultipartFile resume, MultipartFile ssc_result, MultipartFile hsc_result, MultipartFile diploma_result) throws IOException {
        Long userId = jwtService.extractUserId(authtoken);
        Optional<User> userOptional = userRepo.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with the provided token.");
        }

        User user = userOptional.get();

        // Check if the user is already associated with a student
        if (studentRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("User is already associated with a student.");
        }

        // Validate files
        if (prof_img.isEmpty() || resume.isEmpty()) {
            throw new RuntimeException("Profile image or resume cannot be empty.");
        }

        if (!prof_img.getContentType().startsWith("image/")) {
            throw new RuntimeException("Invalid file type for profile image.");
        }


        if (!resume.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Resume must be a PDF file.");
        }
        if (ssc_result != null && !ssc_result.isEmpty()) {
            student.setSsc_result(ssc_result.getBytes());
        }
        if (hsc_result != null && !hsc_result.isEmpty()) {
            student.setHsc_result(hsc_result.getBytes());
        } else {
            student.setHsc_result(null); // Explicitly set to null if not provided
        }
        if (diploma_result != null && !diploma_result.isEmpty()) {
            student.setDiploma_result(diploma_result.getBytes());
        } else {
            student.setDiploma_result(null); // Explicitly set to null if not provided
        }
        // Set profile image data
        student.setProfileimagedata(prof_img.getBytes());
        student.setImagename(prof_img.getOriginalFilename());
        student.setImagetype(prof_img.getContentType());

        // Set resume data
        student.setResumename(student.getFirstName() + "'s Resume");
        student.setResume_file_data(resume.getBytes());  // ✅ Fixed syntax error

        // Associate user with the new student and save
        student.setUser(user);
        studentRepository.save(student);
    }


    public ResponseEntity<? extends Object> getStudent(long id) {
        Optional<Student> studentoptional = studentRepository.findById(id);

        if (studentoptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Student Found with ID: " + id);
        }

        Student student = studentoptional.get();
        StudentDTO StudentDTO = StudentMapper.toStudentDTO(student);

        return ResponseEntity.ok(StudentDTO);


    }

    public ResponseEntity<? extends Object> getstudprofile(String token) {
        long id = jwtService.extractUserId(token);
        Optional<Student> studentoptional = studentRepository.findByUserId(id);

        if (studentoptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Student Found with ID: " + id);
        }

        Student student = studentoptional.get();
        StudentDTO StudentDTO = StudentMapper.toStudentDTO(student);

        return ResponseEntity.ok(StudentDTO);


    }

    public String updateStudent(Student student, String authToken,MultipartFile profile_img,MultipartFile resume) throws IOException {
        Optional<User> userOptional = userRepo.findById(jwtService.extractUserId(authToken));
        if (userOptional.isEmpty()) {
            return "User not found.";
        }

        Optional<Student> existingStudentOpt = studentRepository.findByUserId(userOptional.get().getId());
        if (existingStudentOpt.isEmpty()) {
            return "Student not found.";
        }

        Student existingStudent = existingStudentOpt.get();
        boolean marksUpdated = false;
        boolean ktStatusUpdated = false;
        StringBuilder changes = new StringBuilder("Updated Marks: ");
        StringBuilder ktChanges = new StringBuilder("Updated KT Status: ");

        // Check if any marks have changed
        if(profile_img!=null){
            student.setProfileimagedata(profile_img.getBytes());
        }
        if(resume!=null){
            student.setResume_file_data(resume.getBytes());
        }
        if (student.getSscMarks() != 0 && student.getSscMarks() != existingStudent.getSscMarks()) {
            changes.append("SSC Marks: ").append(existingStudent.getSscMarks()).append(" → ").append(student.getSscMarks()).append("; ");
            existingStudent.setSscMarks(student.getSscMarks());
            marksUpdated = true;
        }
        if (student.getHscMarks() != 0 && student.getHscMarks() != existingStudent.getHscMarks()) {
            changes.append("HSC Marks: ").append(existingStudent.getHscMarks()).append(" → ").append(student.getHscMarks()).append("; ");
            existingStudent.setHscMarks(student.getHscMarks());
            marksUpdated = true;
        }
        if (student.getDiplomaMarks() != 0 && student.getDiplomaMarks() != existingStudent.getDiplomaMarks()) {
            changes.append("Diploma Marks: ").append(existingStudent.getDiplomaMarks()).append(" → ").append(student.getDiplomaMarks()).append("; ");
            existingStudent.setDiplomaMarks(student.getDiplomaMarks());
            marksUpdated = true;
        }
        if (student.getSem1Marks() != 0 && student.getSem1Marks() != existingStudent.getSem1Marks()) {
            changes.append("Sem1 Marks: ").append(existingStudent.getSem1Marks()).append(" → ").append(student.getSem1Marks()).append("; ");
            existingStudent.setSem1Marks(student.getSem1Marks());
            marksUpdated = true;
        }
        if (student.getSem2Marks() != 0 && student.getSem2Marks() != existingStudent.getSem2Marks()) {
            changes.append("Sem2 Marks: ").append(existingStudent.getSem2Marks()).append(" → ").append(student.getSem2Marks()).append("; ");
            existingStudent.setSem2Marks(student.getSem2Marks());
            marksUpdated = true;
        }
        if (student.getSem3Marks() != 0 && student.getSem3Marks() != existingStudent.getSem3Marks()) {
            changes.append("Sem3 Marks: ").append(existingStudent.getSem3Marks()).append(" → ").append(student.getSem3Marks()).append("; ");
            existingStudent.setSem3Marks(student.getSem3Marks());
            marksUpdated = true;
        }
        if (student.getSem4Marks() != 0 && student.getSem4Marks() != existingStudent.getSem4Marks()) {
            changes.append("Sem4 Marks: ").append(existingStudent.getSem4Marks()).append(" → ").append(student.getSem4Marks()).append("; ");
            existingStudent.setSem4Marks(student.getSem4Marks());
            marksUpdated = true;
        }
        if (student.getSem5Marks() != 0 && student.getSem5Marks() != existingStudent.getSem5Marks()) {
            changes.append("Sem5 Marks: ").append(existingStudent.getSem5Marks()).append(" → ").append(student.getSem5Marks()).append("; ");
            existingStudent.setSem5Marks(student.getSem5Marks());
            marksUpdated = true;
        }
        if (student.getSem6Marks() != 0 && student.getSem6Marks() != existingStudent.getSem6Marks()) {
            changes.append("Sem6 Marks: ").append(existingStudent.getSem6Marks()).append(" → ").append(student.getSem6Marks()).append("; ");
            existingStudent.setSem6Marks(student.getSem6Marks());
            marksUpdated = true;
        }

        // Check if any KT status has changed
        if (student.isSem1KT() != existingStudent.isSem1KT()) {
            ktChanges.append("Sem1 KT: ").append(existingStudent.isSem1KT()).append(" → ").append(student.isSem1KT()).append("; ");
            existingStudent.setSem1KT(student.isSem1KT());
            ktStatusUpdated = true;
        }
        if (student.isSem2KT() != existingStudent.isSem2KT()) {
            ktChanges.append("Sem2 KT: ").append(existingStudent.isSem2KT()).append(" → ").append(student.isSem2KT()).append("; ");
            existingStudent.setSem2KT(student.isSem2KT());
            ktStatusUpdated = true;
        }
        if (student.isSem3KT() != existingStudent.isSem3KT()) {
            ktChanges.append("Sem3 KT: ").append(existingStudent.isSem3KT()).append(" → ").append(student.isSem3KT()).append("; ");
            existingStudent.setSem3KT(student.isSem3KT());
            ktStatusUpdated = true;
        }
        if (student.isSem4KT() != existingStudent.isSem4KT()) {
            ktChanges.append("Sem4 KT: ").append(existingStudent.isSem4KT()).append(" → ").append(student.isSem4KT()).append("; ");
            existingStudent.setSem4KT(student.isSem4KT());
            ktStatusUpdated = true;
        }
        if (student.isSem5KT() != existingStudent.isSem5KT()) {
            ktChanges.append("Sem5 KT: ").append(existingStudent.isSem5KT()).append(" → ").append(student.isSem5KT()).append("; ");
            existingStudent.setSem5KT(student.isSem5KT());
            ktStatusUpdated = true;
        }
        if (student.isSem6KT() != existingStudent.isSem6KT()) {
            ktChanges.append("Sem6 KT: ").append(existingStudent.isSem6KT()).append(" → ").append(student.isSem6KT()).append("; ");
            existingStudent.setSem6KT(student.isSem6KT());
            ktStatusUpdated = true;
        }

        // If any marks were updated, log the changes
        if (marksUpdated) {
            logsService.saveLog(
                    "Marks Updated",
                    userOptional.get().getUsername(),
                    "Student",
                    String.valueOf(existingStudent.getId()),
                    changes.toString()
            );
        }

        // If any KT status was updated, log the changes
        if (ktStatusUpdated) {
            logsService.saveLog(
                    "KT Status Updated",
                    userOptional.get().getUsername(),
                    "Student",
                    String.valueOf(existingStudent.getId()),
                    ktChanges.toString()
            );
        }
        if(marksUpdated||ktStatusUpdated){
            existingStudent.setResults_verified(false);
        }
        // Update other non-null fields
        if (student.getProfileimagedata() != null) existingStudent.setProfileimagedata(student.getProfileimagedata());
        if (student.getResume_file_data() != null) existingStudent.setResume_file_data(student.getResume_file_data());
        if (student.getFirstName() != null) existingStudent.setFirstName(student.getFirstName());
        if (student.getGrNo() != null) existingStudent.setGrNo(student.getGrNo());
        if (student.getAcademicyear() != null) existingStudent.setAcademicyear(student.getAcademicyear());
        if (student.getMiddleName() != null) existingStudent.setMiddleName(student.getMiddleName());
        if (student.getLastName() != null) existingStudent.setLastName(student.getLastName());
        if (student.getDateOfBirth() != null) existingStudent.setDateOfBirth(student.getDateOfBirth());
        if (student.getGender() != null) existingStudent.setGender(student.getGender());
        if (student.getPhoneNumber() != null) existingStudent.setPhoneNumber(student.getPhoneNumber());
        if (student.getAddress() != null) existingStudent.setAddress(student.getAddress());
        if (student.getDepartment() != null) existingStudent.setDepartment(student.getDepartment());
        if (student.getNoOfBacklogs() >= 0) existingStudent.setNoOfBacklogs(student.getNoOfBacklogs());

        studentRepository.save(existingStudent);
        return "Student updated successfully.";
    }
    public void getStudprofile(String token) {
    }

    public ResponseEntity<?> getStudents(String token) {
        if (userRepo.findById(jwtService.extractUserId(token)).get().role == "STUDENT") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ONLY TPO ACCESS");
        }
        List<Student> latestStudents = studentRepository.findTop100ByOrderByIdDesc();

        // If no students are found

        // Convert students to DTOs
        List<StudentBasicDTO> studentDTOs = latestStudents.stream()
                .map(StudentBasicMapper::toStudentBasicDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentDTOs);
    }


    public StudentDTO createStudentWithFullDetails(Long id, UserDTO user, String firstName, String middleName, String lastName,
                                                   LocalDate dateOfBirth, String gender, String phoneNumber, String address,
                                                   String department, String academicYear, double sscMarks, double hscMarks,
                                                   double diplomaMarks, double sem1Marks, double sem2Marks, double sem3Marks,
                                                   double sem4Marks, double sem5Marks, double sem6Marks, int noOfBacklogs,
                                                   double avgMarks, String gr_No, byte[] profileImage ,boolean sem1KT, boolean sem2KT, boolean sem3KT, boolean sem4KT, boolean sem5KT, boolean sem6KT,int batch,boolean result_verified) {
        return new StudentDTO(id, user, firstName, middleName, lastName, dateOfBirth, gender, phoneNumber, address,
                department, academicYear, sscMarks, hscMarks, diplomaMarks, sem1Marks, sem2Marks,
                sem3Marks, sem4Marks, sem5Marks, sem6Marks, noOfBacklogs, avgMarks, gr_No, profileImage,sem1KT,sem2KT,sem3KT,sem4KT,sem5KT,sem6KT,batch,result_verified);
    }

    public StudentDTO createStudentWithBasicDetails(Long id, UserDTO user, String firstName, String middleName, String lastName,
                                                    LocalDate dateOfBirth, String gender, String phoneNumber, String address,
                                                    String department, String academicYear, String gr_No, double sscMarks,
                                                    double hscMarks, double diplomaMarks, double avgMarks) {
        return new StudentDTO(id, user, firstName, middleName, lastName, dateOfBirth, gender, phoneNumber, address,
                department, academicYear, gr_No, sscMarks, hscMarks, diplomaMarks, avgMarks);
    }

    public Page<StudentBasicDTO> searchStudents(String firstName,
                                                String department, String academicYear,
                                                Double minAvgMarks,
                                                Double maxAvgMarks,
                                                Integer yearOfPassing,
                                                int page,
                                                int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage = studentRepository.searchStudents(firstName, department, academicYear, minAvgMarks, maxAvgMarks, yearOfPassing, pageable);

        return studentPage.map(StudentBasicMapper::toStudentBasicDTO);
    }

    public ResponseEntity<?> downloadExcel(String firstName, String department, String academicYear,
                                           Double minAvgMarks, Double maxAvgMarks, Integer yearOfPassing,
                                           int page, int size) {
        // Fetch student data using searchStudents
        Page<StudentBasicDTO> studentPage = searchStudents(firstName, department, academicYear, minAvgMarks, maxAvgMarks, yearOfPassing, page, size);

        // Convert Page to List
        List<StudentBasicDTO> students = studentPage.getContent();

        // Generate Excel file
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Student List");
            Row headerRow = sheet.createRow(0);

            // Define headers
            String[] headers = {
                    "First Name", "Middle Name", "Last Name", "Gender","Department", "Date of Birth",
                    "GR No", "SSC Marks", "HSC Marks", "Diploma Marks", "Average Marks",
                    "No. of Backlogs", "Email", "Phone No", "Year of Passing", "Academic Year","No of Kt ","sem1","sem2","sem3","sem4","sem5","sem6","CV "
            };

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Populate data rows
            int rowNum = 1;
            for (StudentBasicDTO basicDTO : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(basicDTO.getFirstName());
                row.createCell(1).setCellValue(basicDTO.getMiddleName());
                row.createCell(2).setCellValue(basicDTO.getLastName());
                row.createCell(3).setCellValue(basicDTO.getGender());
                row.createCell(4).setCellValue(basicDTO.getDepartment());
                row.createCell(5).setCellValue(basicDTO.getDateOfBirth());
                row.createCell(6).setCellValue(basicDTO.getGrNo());
                row.createCell(7).setCellValue(basicDTO.getSscMarks());
                row.createCell(8).setCellValue(basicDTO.getHscMarks());
                row.createCell(9).setCellValue(basicDTO.getDiplomaMarks());
                row.createCell(10).setCellValue(basicDTO.getAvgMarks());
                row.createCell(11).setCellValue(basicDTO.getNoOfBacklogs());
                row.createCell(12).setCellValue(basicDTO.getEmail());
                row.createCell(13).setCellValue(basicDTO.getPhoneNo());
                row.createCell(14).setCellValue(basicDTO.getYearOfpassing());
                row.createCell(15).setCellValue(basicDTO.getAcademicYear());
                row.createCell(16).setCellValue(basicDTO.getNoOfBacklogs());
                row.createCell(17).setCellValue( basicDTO.isSem1KT() ? "KT" : String.valueOf(basicDTO.getSem1Marks()<1?"NA":basicDTO.getSem1Marks()));
                row.createCell(18).setCellValue(basicDTO.isSem2KT() ? "KT" : String.valueOf(basicDTO.getSem2Marks()<1?"NA":basicDTO.getSem2Marks()));
                row.createCell(19).setCellValue(basicDTO.isSem3KT() ? "KT" : String.valueOf(basicDTO.getSem3Marks()));
                row.createCell(20).setCellValue(basicDTO.isSem4KT() ? "KT" : String.valueOf(basicDTO.getSem4Marks()));
                row.createCell(21).setCellValue(basicDTO.isSem5KT() ? "KT" : String.valueOf(basicDTO.getSem5Marks()));
                row.createCell(22).setCellValue(basicDTO.isSem6KT() ? "KT" : String.valueOf(basicDTO.getSem6Marks()));
                CreationHelper createHelper = workbook.getCreationHelper();
                Cell cell = row.createCell(23);
                Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
                link.setAddress("http://localhost:4200/download/cv/"+basicDTO.getId()+"/ignore");
                cell.setHyperlink(link);
                cell.setCellValue( basicDTO.getFirstName()+ "'CV");

// Optional: Add hyperlink styling
                CellStyle hyperlinkStyle = workbook.createCellStyle();
                Font hyperlinkFont = workbook.createFont();
                hyperlinkFont.setUnderline(Font.U_SINGLE);
                hyperlinkFont.setColor(IndexedColors.BLUE.getIndex());
                hyperlinkStyle.setFont(hyperlinkFont);
                cell.setCellStyle(hyperlinkStyle);

            }

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] excelBytes = outputStream.toByteArray();

            // Set response headers for download
            HttpHeaders headersObj = new HttpHeaders();
            headersObj.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headersObj.setContentDisposition(ContentDisposition.attachment().filename("Student_List.xlsx").build());

            return ResponseEntity.ok().headers(headersObj).body(excelBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<?> getstudprofiletpo(String token,long id) {
        // Validate JWT token and get user
        Long userId = jwtService.extractUserId(token);
        Optional<User> userOptional = userRepo.findById(userId);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        
        User user = userOptional.get();
        
        // Check if user is TPO user
        Optional<TPOUser> tpoUserOptional = tpoRepository.findByUser(user);
        if (tpoUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied - Only TPO users can access this endpoint");
        }
        
        TPOUser tpoUser = tpoUserOptional.get();
        
        // Check if TPO user has appropriate role
        if (tpoUser.getRole() != TPO_Role.ADMIN && tpoUser.getRole() != TPO_Role.TPO_USER && tpoUser.getRole() != TPO_Role.TPO_ASSISTANT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions");
        }
        
        Optional<Student> studentoptional = studentRepository.findById(id);

        if (studentoptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Student Found with ID: " + id);
        }

        Student student = studentoptional.get();
        StudentDTO StudentDTO = StudentMapper.toStudentDTO(student);

        return ResponseEntity.ok(StudentDTO);
    }

    // Student Results Verification Methods
    public String verifyStudentResults(Long studentId, String token, boolean verified, String remarks) {
        try {
            // Validate JWT token
            Long userId = jwtService.extractUserId(token);
            Optional<User> userOptional = userRepo.findById(userId);
            
            if (userOptional.isEmpty()) {
                return "Invalid token";
            }
            
            User user = userOptional.get();
            
            // Check if user is TPO user
            Optional<TPOUser> tpoUserOptional = tpoRepository.findByUser(user);
            if (tpoUserOptional.isEmpty()) {
                return "Unauthorized";
            }
            
            TPOUser tpoUser = tpoUserOptional.get();
            
            // Check if TPO user has permission (ADMIN or TPO_USER role)
            if (tpoUser.getRole() != TPO_Role.ADMIN && tpoUser.getRole() != TPO_Role.TPO_USER) {
                return "Unauthorized";
            }
            
            // Find student
            Optional<Student> studentOptional = studentRepository.findById(studentId);
            if (studentOptional.isEmpty()) {
                return "Student not found";
            }
            
            Student student = studentOptional.get();
            
            // Update verification status
            student.setResults_verified(verified);
            
            // Save the student
            studentRepository.save(student);
            
            // Log the verification action
            String action = verified ? "verified" : "unverified";
            String logMessage = String.format("TPO user %s %s results for student ID: %d", 
                tpoUser.getUser().getUsername(), action, studentId);
            
            if (remarks != null && !remarks.trim().isEmpty()) {
                logMessage += " - Remarks: " + remarks;
            }
            
            logsService.saveLog("STUDENT_VERIFICATION", tpoUser.getUser().getUsername(), 
                "Student", studentId.toString(), logMessage);
            
            return "Success";
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    public Map<String, Object> getStudentVerificationStatus(Long studentId, String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate JWT token
            Long userId = jwtService.extractUserId(token);
            Optional<User> userOptional = userRepo.findById(userId);
            
            if (userOptional.isEmpty()) {
                response.put("error", "Invalid token");
                return response;
            }
            
            User user = userOptional.get();
            
            // Check if user is TPO user
            Optional<TPOUser> tpoUserOptional = tpoRepository.findByUser(user);
            if (tpoUserOptional.isEmpty()) {
                response.put("error", "Unauthorized");
                return response;
            }
            
            // Find student
            Optional<Student> studentOptional = studentRepository.findById(studentId);
            if (studentOptional.isEmpty()) {
                response.put("error", "Student not found");
                return response;
            }
            
            Student student = studentOptional.get();
            
            // Prepare verification status response
            response.put("status", "success");
            response.put("studentId", studentId);
            response.put("studentName", student.getFirstName() + " " + student.getLastName());
            response.put("department", student.getDepartment());
            response.put("academicYear", student.getAcademicyear());
            response.put("verified", student.isResults_verified());
            response.put("cgpa", student.getAvgMarks());
            response.put("sscMarks", student.getSscMarks());
            response.put("hscMarks", student.getHscMarks());
            
            // Add available documents info
            Map<String, Boolean> documents = new HashMap<>();
            documents.put("resume", student.getResume_file_data() != null);
            documents.put("sscResult", student.getSsc_result() != null);
            documents.put("hscResult", student.getHsc_result() != null);
            documents.put("diplomaResult", student.getDiploma_result() != null);
            response.put("documentsAvailable", documents);
            
            return response;
            
        } catch (Exception e) {
            response.put("error", "Error: " + e.getMessage());
            return response;
        }
    }

    // Prepare response with Excel file



}



