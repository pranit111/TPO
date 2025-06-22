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
import java.util.Optional;

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


    @DeleteMapping("/{id}")
    public void deletePlacement(@PathVariable Long id) {
        placementService.deletePlacement(id);
    }
}
