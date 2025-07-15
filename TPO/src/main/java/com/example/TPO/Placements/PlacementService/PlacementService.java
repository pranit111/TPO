package com.example.TPO.Placements.PlacementService;

import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Applications.JobApplication;
import com.example.TPO.DBMS.Placements.Placement;
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
}
