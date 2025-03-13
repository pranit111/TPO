package com.example.TPO.Companies.CompaniesRepository;

import com.example.TPO.DBMS.Company.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompaniesRepository extends JpaRepository<Company,Long> {
}
