package com.example.TPO.Placements.PlacementService;

import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.Placements.Placement;
import com.example.TPO.Downloads.Excel.ExcelService.ExcelService;
import com.example.TPO.JobApplication.JobApplicationRepository.JobApplicationRepository;
import com.example.TPO.Placements.PlacementRepository.PlacementRepository;
import com.example.TPO.Placements.PlacementsDTO.PlacementDTO;
import com.example.TPO.Placements.PlacementsDTO.PlacementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Service
public class PlacementService {
    @Autowired
    JobApplicationRepository jobApplicationRepository;
    @Autowired
     PlacementRepository placementRepository;
    @Autowired
    PlacementMapper placementMapper;
    @Autowired
    ExcelService excelService;

    public PlacementDTO savePlacement(Placement placement, long applicationid, MultipartFile offerletter) throws IOException {
        placement.setOfferLetterUrl(offerletter.getBytes());
//        Change the applications status
        JobApplication jobApplication = jobApplicationRepository.findById(applicationid)
                .orElseThrow(() -> new RuntimeException("Job Application not found with id: " + applicationid));
        jobApplication.setStatus(ApplicationStatus.HIRED);
        placement.setApplication(jobApplication);

        return placementMapper.toDTO( placementRepository.save(placement));
    }

    public PlacementDTO getPlacementById(Long id) {
        return  placementMapper.toDTO(placementRepository.findById(id).get());
    }

    public Page<PlacementDTO> getAllPlacements(Pageable pageable) {
        Page<Placement> placementPage = placementRepository.findAll(pageable);
        return placementPage.map(placementMapper::toDTO);
    }

    public Page<PlacementDTO> searchAll(String keyword, Pageable pageable) {
        Page<Placement> result = placementRepository.searchAll(keyword, pageable);
        return result.map(placementMapper::toDTO);
    }

    // Filter methods
    public Page<PlacementDTO> filterByCompany(String companyName, Pageable pageable) {
        Page<Placement> result = placementRepository.filterByCompany(companyName, pageable);
        return result.map(placementMapper::toDTO);
    }

    public Page<PlacementDTO> filterByJobDesignation(String jobDesignation, Pageable pageable) {
        Page<Placement> result = placementRepository.filterByJobDesignation(jobDesignation, pageable);
        return result.map(placementMapper::toDTO);
    }

    public Page<PlacementDTO> filterByPackageRange(double minPackage, double maxPackage, Pageable pageable) {
        Page<Placement> result = placementRepository.filterByPackageRange(minPackage, maxPackage, pageable);
        return result.map(placementMapper::toDTO);
    }

    public Page<PlacementDTO> filterByDateRange(LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Page<Placement> result = placementRepository.filterByDateRange(fromDate, toDate, pageable);
        return result.map(placementMapper::toDTO);
    }

    public Page<PlacementDTO> filterByStudentName(String studentName, Pageable pageable) {
        Page<Placement> result = placementRepository.filterByStudentName(studentName, pageable);
        return result.map(placementMapper::toDTO);
    }

    public Page<PlacementDTO> filterByDepartment(String department, Pageable pageable) {
        Page<Placement> result = placementRepository.filterByDepartment(department, pageable);
        return result.map(placementMapper::toDTO);
    }

    public Page<PlacementDTO> filterByStudentYear(String studentYear, Pageable pageable) {
        Page<Placement> result = placementRepository.filterByStudentYear(studentYear, pageable);
        return result.map(placementMapper::toDTO);
    }

    public void deletePlacement(Long id) {
        placementRepository.deleteById(id);
    }

    public ResponseEntity<byte[]> downloadofferletter(Long id) {
        Optional<Placement> placement=placementRepository.findById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("document.pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(placement.get().getOfferLetterUrl());
    }

    // Download placements data as Excel
    public ResponseEntity<byte[]> downloadPlacementsExcel() throws IOException {
        List<Placement> placements = placementRepository.findAll();
        
        // Define Excel headers
        String[] headers = {
            "Placement ID", "Student Name", "Company", "Job Designation", 
            "Package (LPA)", "Placement Date", "Department", "Student Year", "Remarks"
        };
        
        // Convert placement data to String arrays
        List<String[]> data = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        for (Placement placement : placements) {
            String[] row = new String[9];
            row[0] = placement.getId().toString();
            row[1] = placement.getApplication().getStudent().getFirstName() + " " + 
                     placement.getApplication().getStudent().getLastName();
            row[2] = placement.getApplication().getJobPost().getCompany().getName();
            row[3] = placement.getApplication().getJobPost().getJobDesignation();
            row[4] = String.valueOf(placement.getPlaced_package());
            row[5] = placement.getPlacementDate() != null ? placement.getPlacementDate().format(dateFormatter) : "";
            row[6] = placement.getApplication().getStudent().getDepartment();
            row[7] = placement.getApplication().getJobPost().getStudentYear().toString();
            row[8] = placement.getRemarks() != null ? placement.getRemarks() : "";
            
            data.add(row);
        }
        
        // Generate Excel file
        byte[] excelData = excelService.generateExcel(data, headers);
        
        // Set response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        responseHeaders.setContentDisposition(
            ContentDisposition.attachment().filename("placements_" + LocalDate.now() + ".xlsx").build()
        );
        
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(excelData);
    }

    // Download filtered placements data as Excel
    public ResponseEntity<byte[]> downloadFilteredPlacementsExcel(String filterType, String filterValue) throws IOException {
        List<Placement> placements;
        
        // Apply filter based on type
        switch (filterType.toLowerCase()) {
            case "company":
                placements = placementRepository.filterByCompany(filterValue, Pageable.unpaged()).getContent();
                break;
            case "department":
                placements = placementRepository.filterByDepartment(filterValue, Pageable.unpaged()).getContent();
                break;
            case "studentyear":
                placements = placementRepository.filterByStudentYear(filterValue, Pageable.unpaged()).getContent();
                break;
            case "jobdesignation":
                placements = placementRepository.filterByJobDesignation(filterValue, Pageable.unpaged()).getContent();
                break;
            default:
                placements = placementRepository.findAll();
        }
        
        // Define Excel headers
        String[] headers = {
            "Placement ID", "Student Name", "Company", "Job Designation", 
            "Package (LPA)", "Placement Date", "Department", "Student Year", "Remarks"
        };
        
        // Convert placement data to String arrays
        List<String[]> data = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        for (Placement placement : placements) {
            String[] row = new String[9];
            row[0] = placement.getId().toString();
            row[1] = placement.getApplication().getStudent().getFirstName() + " " + 
                     placement.getApplication().getStudent().getLastName();
            row[2] = placement.getApplication().getJobPost().getCompany().getName();
            row[3] = placement.getApplication().getJobPost().getJobDesignation();
            row[4] = String.valueOf(placement.getPlaced_package());
            row[5] = placement.getPlacementDate() != null ? placement.getPlacementDate().format(dateFormatter) : "";
            row[6] = placement.getApplication().getStudent().getDepartment();
            row[7] = placement.getApplication().getJobPost().getStudentYear().toString();
            row[8] = placement.getRemarks() != null ? placement.getRemarks() : "";
            
            data.add(row);
        }
        
        // Generate Excel file
        byte[] excelData = excelService.generateExcel(data, headers);
        
        // Set response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        responseHeaders.setContentDisposition(
            ContentDisposition.attachment().filename("placements_" + filterType + "_" + filterValue + "_" + LocalDate.now() + ".xlsx").build()
        );
        
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(excelData);
    }

}
