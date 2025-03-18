package com.example.TPO.Student.StudentService;

import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.Filters.JobApplicationFilter;
import com.example.TPO.DBMS.stud.Student;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationMapper;
import com.example.TPO.Student.StudentDTO.StudentBasicDTO;
import com.example.TPO.Student.StudentDTO.StudentBasicMapper;
import com.example.TPO.Student.StudentDTO.StudentDTO;
import com.example.TPO.Student.StudentDTO.StudentMapper;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserDTO.UserDTO;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.List;
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

    public void createstudent(Student student, String authtoken, MultipartFile prof_img, MultipartFile resume) throws IOException {
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

    public String updateStudent(Student student, String authToken) {
        Optional<User> userOptional = userRepo.findById(jwtService.extractUserId(authToken));
        Optional<Student> existingStudentOpt = studentRepository.findByUserId(userOptional.get().getId());

        if (existingStudentOpt.isEmpty()) {
            return "Student not found.";
        }

        Student existingStudent = existingStudentOpt.get();
        System.out.println(student.getAcademicyear());
        // Update only non-null fields
        if (student.getProfileimagedata() != null) existingStudent.setProfileimagedata(student.getProfileimagedata());
        if (student.getFirstName() != null) existingStudent.setFirstName(student.getFirstName());
        if (student.getGr_No() != null) existingStudent.setGr_No(student.getGr_No());
        if (student.getAcademicyear() != null) existingStudent.setAcademicyear(student.getAcademicyear());
        if (student.getMiddleName() != null) existingStudent.setMiddleName(student.getMiddleName());
        if (student.getLastName() != null) existingStudent.setLastName(student.getLastName());
        if (student.getDateOfBirth() != null) existingStudent.setDateOfBirth(student.getDateOfBirth());
        if (student.getGender() != null) existingStudent.setGender(student.getGender());
        if (student.getPhoneNumber() != null) existingStudent.setPhoneNumber(student.getPhoneNumber());
        if (student.getAddress() != null) existingStudent.setAddress(student.getAddress());
        if (student.getDepartment() != null) existingStudent.setDepartment(student.getDepartment());
        if (student.getSscMarks() != 0) existingStudent.setSscMarks(student.getSscMarks());
        if (student.getHscMarks() != 0) existingStudent.setHscMarks(student.getHscMarks());
        if (student.getDiplomaMarks() != 0) existingStudent.setDiplomaMarks(student.getDiplomaMarks());
        if (student.getSem1Marks() != 0) existingStudent.setSem1Marks(student.getSem1Marks());
        if (student.getSem2Marks() != 0) existingStudent.setSem2Marks(student.getSem2Marks());
        if (student.getSem3Marks() != 0) existingStudent.setSem3Marks(student.getSem3Marks());
        if (student.getSem4Marks() != 0) existingStudent.setSem4Marks(student.getSem4Marks());
        if (student.getSem5Marks() != 0) existingStudent.setSem5Marks(student.getSem5Marks());
        if (student.getSem6Marks() != 0) existingStudent.setSem6Marks(student.getSem6Marks());
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
                                                   double avgMarks, String gr_No, byte[] profileImage) {
        return new StudentDTO(id, user, firstName, middleName, lastName, dateOfBirth, gender, phoneNumber, address,
                department, academicYear, sscMarks, hscMarks, diplomaMarks, sem1Marks, sem2Marks,
                sem3Marks, sem4Marks, sem5Marks, sem6Marks, noOfBacklogs, avgMarks, gr_No, profileImage);
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
                    "First Name", "Middle Name", "Last Name", "Gender", "Date of Birth",
                    "GR No", "SSC Marks", "HSC Marks", "Diploma Marks", "Average Marks",
                    "No. of Backlogs", "Email", "Phone No", "Year of Passing", "Academic Year"
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
                row.createCell(4).setCellValue(basicDTO.getDateOfBirth());
                row.createCell(5).setCellValue(basicDTO.getGrNo());
                row.createCell(6).setCellValue(basicDTO.getSscMarks());
                row.createCell(7).setCellValue(basicDTO.getHscMarks());
                row.createCell(8).setCellValue(basicDTO.getDiplomaMarks());
                row.createCell(9).setCellValue(basicDTO.getAvgMarks());
                row.createCell(10).setCellValue(basicDTO.getNoOfBacklogs());
                row.createCell(11).setCellValue(basicDTO.getEmail());
                row.createCell(12).setCellValue(basicDTO.getPhoneNo());
                row.createCell(13).setCellValue(basicDTO.getYearOfpassing());
                row.createCell(14).setCellValue(basicDTO.getAcademicYear());
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

        // Prepare response with Excel file



}



