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
                    existing.setName(companyDTO.getName());
                    existing.setIndustryType(companyDTO.getIndustryType());
                    existing.setEmail(companyDTO.getEmail());
                    existing.setContactNumber(companyDTO.getContactNumber());
                    existing.setLocation(companyDTO.getLocation());
                    existing.setWebsite(companyDTO.getWebsite());
                    existing.setAssociatedSince(companyDTO.getAssociatedSince());
                    existing.setActive(companyDTO.getActive());
                    return CompanyDTO.fromEntity(companiesRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public void deleteCompany(Long id) {
        companiesRepository.deleteById(id);
    }
}
