package com.example.TPO.Tpo.DashBoard;


import com.example.TPO.Companies.CompaniesRepository.CompaniesRepository;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    CompaniesRepository companiesRepository;
    public DashboardData getDashboardData() {
        DashboardData dashboardData = new DashboardData();

        // Mocked Student Data
        DashboardData.StudentData studentData = new DashboardData.StudentData();
        int totalStudents = (int) studentRepository.count();
        studentData.setTotal(totalStudents);

// 2. Calculate placed students (35% of total)
        int placedStudents = (int) Math.round(totalStudents * 0.35);
        studentData.setPlaced(placedStudents);

// 3. Calculate remaining students
        int remainingStudents = totalStudents - placedStudents;
        studentData.setRemaining(remainingStudents);

// 4. Calculate placement rate (in percentage)
        double placementRate = totalStudents > 0 ?
                ((double) placedStudents / totalStudents) * 100 : 0;
        studentData.setPlacementRate(Math.round(placementRate * 10) / 10.0); // Round to 1 decimal
        dashboardData.setStudentData(studentData); // ✅ Ensure it's set here

// 5. Calculate average package (from placement records)
        // Fetch all companies and map them with dummy openings
        List<DashboardData.Company> companyList = companiesRepository.findAll()
                .stream()
                .map(company -> new DashboardData.Company(company.getName(), generateDummyOpenings()))
                .collect(Collectors.toList());

// Create CompaniesData object
        DashboardData.CompaniesData companiesData = new DashboardData.CompaniesData();
        companiesData.setTotal((int) companiesRepository.count());
        companiesData.setTotalOpenings(companyList.stream().mapToInt(DashboardData.Company::getOpenings).sum()); // Corrected
        companiesData.setRemaining(100);

// Use dynamically fetched company data instead of static list
        companiesData.setCompanies(companyList); // ✅ Corrected

// Set in DashboardData
        dashboardData.setCompaniesData(companiesData);

// Dummy Openings Generator


        // Mocked Insights Data
        DashboardData.InsightsData insightsData = new DashboardData.InsightsData();
        insightsData.setHighestPackage("24 LPA");
        insightsData.setAveragePackage("8.5 LPA");
        insightsData.setTopHiringCompany("FinTech Pro");
        insightsData.setTopDepartment("Computer Science");
        dashboardData.setInsightsData(insightsData);

        // Mocked Department Data
        DashboardData.DepartmentsData departmentsData = new DashboardData.DepartmentsData();
        departmentsData.setTotalDepartments(5);
        departmentsData.setEligibleStudents(400);
        departmentsData.setRemaining(100);
        dashboardData.setDepartmentsData(departmentsData);

        // Mocked Top Students Data
        List<DashboardData.TopStudent> topStudents = studentRepository.findRandomStudents(PageRequest.of(0, 3)) // ✅ Fetch any 3 students
                .stream()
                .map(student -> new DashboardData.TopStudent(
                        student.getId().intValue(),
                        student.getFirstName(),
                        student.getDepartment(),
                        String.valueOf(student.getAvgMarks()),
                        student.getAcademicyear()
                ))
                .collect(Collectors.toList());

// Set top students in DashboardData
        dashboardData.setTopStudents(topStudents);

        dashboardData.setTopStudents(topStudents);

        return dashboardData;
    }

    private int generateDummyOpenings() {            return new Random().nextInt(91) + 10;

    }

    private DashboardData.Company createCompany(String name, int openings) {
        DashboardData.Company company = new DashboardData.Company();
        company.setName(name);
        company.setOpenings(openings);
        return company;
    }

    private DashboardData.TopStudent createTopStudent(int rank, String name, String department, String packageAmount, String company) {
        DashboardData.TopStudent student = new DashboardData.TopStudent();
        student.setRank(rank);
        student.setName(name);
        student.setDepartment(department);
        student.setPackageAmount(packageAmount);
        student.setCompany(company);
        return student;
    }
}
