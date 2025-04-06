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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        JobApplication jobApplication= jobApplicationRepository.getById(applicationid);
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

    public void deletePlacement(Long id) {
        placementRepository.deleteById(id);
    }
}
