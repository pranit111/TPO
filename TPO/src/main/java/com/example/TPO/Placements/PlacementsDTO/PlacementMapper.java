package com.example.TPO.Placements.PlacementsDTO;

import com.example.TPO.DBMS.Placements.Placement;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationMapper;
import com.example.TPO.JobApplication.JobApplicationDTO.JobApplicationDTO;
 // assuming you have a mapper for this
import com.example.TPO.DBMS.Applications.JobApplication;
import org.springframework.stereotype.Component;

@Component
public class  PlacementMapper {

    // Convert Placement entity to DTO
    public PlacementDTO toDTO(Placement placement) {
        if (placement == null) return null;

        JobApplicationDTO applicationDTO = JobApplicationMapper.toJobApplicationDTO(placement.getApplication());

        return new PlacementDTO(
                placement.getId(),
                applicationDTO,
                placement.getPlacementDate(),
                placement.getOfferLetterUrl(),
                placement.getPlaced_package(),
                placement.getRemarks(),
                placement.getCreatedAt(),
                placement.getUpdatedAt()
        );
    }

    // Convert PlacementDTO to entity (requires JobApplication entity as input)
    public static Placement toEntity(PlacementDTO dto, JobApplication application) {
        if (dto == null) return null;

        Placement placement = new Placement();
        placement.setId(dto.getId());
        placement.setApplication(application); // must be fetched beforehand
        placement.setPlacementDate(dto.getPlacementDate());
        placement.setOfferLetterUrl(dto.getOfferLetterUrl());
        placement.setPlaced_package(dto.getPlacedPackage());
        placement.setRemarks(dto.getRemarks());
        placement.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : placement.getCreatedAt());
        placement.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : placement.getUpdatedAt());

        return placement;
    }
}
