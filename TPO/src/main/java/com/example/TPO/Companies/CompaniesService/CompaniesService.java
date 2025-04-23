package com.example.TPO.Companies.CompaniesService;

import com.example.TPO.Companies.CompaniesDTO.CompanyDTO;
import com.example.TPO.Companies.CompaniesRepository.CompaniesRepository;
import com.example.TPO.DBMS.Company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompaniesService {
@Autowired
CompaniesRepository companiesRepository;

    public CompaniesService(CompaniesRepository companiesRepository) {
        this.companiesRepository = companiesRepository;
    }

    public List<CompanyDTO> getAllCompanies() {
        return companiesRepository.findAll()
                .stream()
                .map(CompanyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CompanyDTO getCompanyById(Long id) {
        return companiesRepository.findById(id)
                .map(CompanyDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        return CompanyDTO.fromEntity(companiesRepository.save(companyDTO.toEntity()));
    }

    public CompanyDTO updateCompany(Long id, CompanyDTO companyDTO) {
        return companiesRepository.findById(id)
                .map(existing -> {
                    if (companyDTO.getName() != null) existing.setName(companyDTO.getName());
                    if (companyDTO.getIndustryType() != null) existing.setIndustryType(companyDTO.getIndustryType());
                    if (companyDTO.getEmail() != null) existing.setEmail(companyDTO.getEmail());
                    if (companyDTO.getContactNumber() != null) existing.setContactNumber(companyDTO.getContactNumber());
                    if (companyDTO.getLocation() != null) existing.setLocation(companyDTO.getLocation());
                    if (companyDTO.getWebsite() != null) existing.setWebsite(companyDTO.getWebsite());
                    if (companyDTO.getAssociatedSince() != null) existing.setAssociatedSince(companyDTO.getAssociatedSince());
                    if (companyDTO.getActive() != null) existing.setActive(companyDTO.getActive());
                    if (companyDTO.getMnc() != null) existing.setMnc(companyDTO.getMnc());
                    if (companyDTO.getHr_Name() != null) existing.setHr_Name(companyDTO.getHr_Name());

                    return CompanyDTO.fromEntity(companiesRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }


    public void deleteCompany(Long id) {
        companiesRepository.deleteById(id);
    }
}
