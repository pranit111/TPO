package com.example.TPO.Placements.PlacementContoller;

import com.example.TPO.DBMS.Placements.Placement;
import com.example.TPO.Placements.PlacementService.PlacementService;
import com.example.TPO.Placements.PlacementsDTO.PlacementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api9/placements")
public class PlacementController {

    @Autowired
    private PlacementService placementService;

    @PostMapping
    public PlacementDTO createPlacementStudent(
            @ModelAttribute("placement") Placement placement,
            @RequestParam("applicationid") long applicationId,
            @RequestPart("file") MultipartFile offerLetter) throws IOException {

        return placementService.savePlacement(placement, applicationId, offerLetter);
    }

    @GetMapping("/{id}")
    public PlacementDTO getPlacementById(@PathVariable Long id) {
        return placementService.getPlacementById(id);
    }

    @GetMapping
    public Page<PlacementDTO> getAllPlacements(Pageable pageable) {
        return placementService.getAllPlacements(pageable);
    }
    
    @GetMapping("/offerletter/{id}")
    public ResponseEntity<byte[]> getOfferLetter(@PathVariable Long id) {
      return placementService.downloadofferletter(id);
    }

    @GetMapping("/search")
    public Page<PlacementDTO> searchPlacements(@RequestParam("q") String keyword, Pageable pageable) {
        return placementService.searchAll(keyword, pageable);
    }

    // Filter endpoints
    @GetMapping("/filter/company")
    public Page<PlacementDTO> filterByCompany(
            @RequestParam("companyName") String companyName,
            Pageable pageable) {
        return placementService.filterByCompany(companyName, pageable);
    }

    @GetMapping("/filter/job-designation")
    public Page<PlacementDTO> filterByJobDesignation(
            @RequestParam("jobDesignation") String jobDesignation,
            Pageable pageable) {
        return placementService.filterByJobDesignation(jobDesignation, pageable);
    }

    @GetMapping("/filter/package-range")
    public Page<PlacementDTO> filterByPackageRange(
            @RequestParam("minPackage") double minPackage,
            @RequestParam("maxPackage") double maxPackage,
            Pageable pageable) {
        return placementService.filterByPackageRange(minPackage, maxPackage, pageable);
    }

    @GetMapping("/filter/date-range")
    public Page<PlacementDTO> filterByDateRange(
            @RequestParam("fromDate") LocalDate fromDate,
            @RequestParam("toDate") LocalDate toDate,
            Pageable pageable) {
        return placementService.filterByDateRange(fromDate, toDate, pageable);
    }

    @GetMapping("/filter/student")
    public Page<PlacementDTO> filterByStudentName(
            @RequestParam("studentName") String studentName,
            Pageable pageable) {
        return placementService.filterByStudentName(studentName, pageable);
    }

    @GetMapping("/filter/department")
    public Page<PlacementDTO> filterByDepartment(
            @RequestParam("department") String department,
            Pageable pageable) {
        return placementService.filterByDepartment(department, pageable);
    }

    @GetMapping("/filter/student-year")
    public Page<PlacementDTO> filterByStudentYear(
            @RequestParam("studentYear") String studentYear,
            Pageable pageable) {
        return placementService.filterByStudentYear(studentYear, pageable);
    }

    @DeleteMapping("/{id}")
    public void deletePlacement(@PathVariable Long id) {
        placementService.deletePlacement(id);
    }
}
