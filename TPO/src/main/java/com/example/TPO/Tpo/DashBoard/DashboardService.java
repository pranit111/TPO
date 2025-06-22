package com.example.TPO.Tpo.DashBoard;

import com.example.TPO.Companies.CompaniesRepository.CompaniesRepository;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.Placements.PlacementRepository.PlacementRepository;
import com.example.TPO.JobApplication.JobApplicationRepository.JobApplicationRepository;
import com.example.TPO.JobPost.JobPostRepository.JobPostRepository;
import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Placements.Placement;
import com.example.TPO.DBMS.stud.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    CompaniesRepository companiesRepository;
    @Autowired
    PlacementRepository placementRepository;
    @Autowired
    JobApplicationRepository jobApplicationRepository;
    @Autowired
    JobPostRepository jobPostRepository;    public DashboardData getDashboardData() {
        DashboardData dashboardData = new DashboardData();

        // Real Student Data
        DashboardData.StudentData studentData = getStudentAnalytics();
        dashboardData.setStudentData(studentData);        // Real Company Data  
        DashboardData.CompaniesData companiesData = getCompaniesData();
        dashboardData.setCompaniesData(companiesData);

        // Real Insights Data
        DashboardData.InsightsData insightsData = getPlacementInsights();
        dashboardData.setInsightsData(insightsData);

        // Real Department Data
        DashboardData.DepartmentsData departmentsData = getDepartmentAnalytics();
        dashboardData.setDepartmentsData(departmentsData);

        // Real Top Students Data
        List<DashboardData.TopStudent> topStudents = getTopPlacedStudents();
        dashboardData.setTopStudents(topStudents);

        // Set enhanced analytics data
        dashboardData.setPlacementTrends(getPlacementTrends());
        dashboardData.setDepartmentStats(getDepartmentStats());
        dashboardData.setMonthlyPlacements(getMonthlyPlacements());
        dashboardData.setProcessMetrics(getProcessMetrics());
        dashboardData.setPackageDistribution(getPackageDistribution());        dashboardData.setRecentActivities(getRecentActivities());
        dashboardData.setCompanyAnalytics(getCompanyAnalyticsData());

        return dashboardData;
    }

    private DashboardData.StudentData getStudentAnalytics() {
        DashboardData.StudentData studentData = new DashboardData.StudentData();
        
        long totalStudents = studentRepository.count();
        long placedStudents = placementRepository.count(); // Count from placement table
        long remainingStudents = totalStudents - placedStudents;
        
        studentData.setTotal((int) totalStudents);
        studentData.setPlaced((int) placedStudents);
        studentData.setRemaining((int) remainingStudents);
        
        double placementRate = totalStudents > 0 ? 
            ((double) placedStudents / totalStudents) * 100 : 0;
        studentData.setPlacementRate(Math.round(placementRate * 10) / 10.0);
        
        // Calculate real average package from placements
        double averagePackage = getAveragePackageFromPlacements();
        studentData.setAveragePackage(averagePackage);
        
        return studentData;
    }    private DashboardData.CompaniesData getCompaniesData() {
        DashboardData.CompaniesData companiesData = new DashboardData.CompaniesData();
        
        long totalCompanies = companiesRepository.count();
        long totalJobPosts = jobPostRepository.count();
        long activeJobPosts = jobApplicationRepository.findAll().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.APPLIED || 
                          app.getStatus() == ApplicationStatus.UNDER_REVIEW)
            .count();
        
        companiesData.setTotal((int) totalCompanies);
        companiesData.setTotalOpenings((int) totalJobPosts);
        companiesData.setRemaining((int) activeJobPosts);
        
        // Get companies with real job openings
        List<DashboardData.Company> companyList = getCompaniesWithJobPostCounts();
        companiesData.setCompanies(companyList);
        
        return companiesData;
    }

    private DashboardData.InsightsData getPlacementInsights() {
        DashboardData.InsightsData insightsData = new DashboardData.InsightsData();
        
        // Get highest package from placements
        Double highestPackage = getHighestPackage();
        insightsData.setHighestPackage(highestPackage != null ? 
            String.format("%.1f LPA", highestPackage) : "N/A");
        
        // Get average package
        Double avgPackage = getAveragePackageFromPlacements();
        insightsData.setAveragePackage(avgPackage > 0 ? 
            String.format("%.1f LPA", avgPackage) : "N/A");
        
        // Get top hiring company
        String topCompany = getTopHiringCompany();
        insightsData.setTopHiringCompany(topCompany != null ? topCompany : "N/A");
        
        // Get top performing department
        String topDepartment = getTopPerformingDepartment();
        insightsData.setTopDepartment(topDepartment != null ? topDepartment : "N/A");
        
        return insightsData;
    }

    private DashboardData.DepartmentsData getDepartmentAnalytics() {
        DashboardData.DepartmentsData departmentsData = new DashboardData.DepartmentsData();
        
        // Count unique departments
        List<String> departments = studentRepository.findAll().stream()
            .map(Student::getDepartment)
            .distinct()
            .collect(Collectors.toList());
        
        departmentsData.setTotalDepartments(departments.size());
        
        // Count eligible students (those with complete profiles)
        long eligibleStudents = studentRepository.findAll().stream()
            .filter(s -> s.isResults_verified() && s.getAvgMarks() > 0)
            .count();
        departmentsData.setEligibleStudents((int) eligibleStudents);
        
        long remainingEligible = eligibleStudents - placementRepository.count();
        departmentsData.setRemaining((int) remainingEligible);
        
        return departmentsData;
    }

    private List<DashboardData.TopStudent> getTopPlacedStudents() {
        // Get top 5 students by package from placements
        List<Placement> topPlacements = placementRepository.findAll().stream()
            .sorted((p1, p2) -> Double.compare(p2.getPlaced_package(), p1.getPlaced_package()))
            .limit(5)
            .collect(Collectors.toList());

        return topPlacements.stream()
            .map(placement -> {
                Student student = placement.getApplication().getStudent();
                String companyName = placement.getApplication().getJobPost().getCompany().getName();
                
                return new DashboardData.TopStudent(
                    topPlacements.indexOf(placement) + 1,
                    student.getFirstName() + " " + student.getLastName(),
                    student.getDepartment(),
                    String.format("%.1f LPA", placement.getPlaced_package()),
                    companyName
                );
            })            .collect(Collectors.toList());
    }

    // Helper methods for real data fetching

    private double getAveragePackageFromPlacements() {
        List<Placement> placements = placementRepository.findAll();
        if (placements.isEmpty()) return 0.0;
        
        return placements.stream()
            .mapToDouble(Placement::getPlaced_package)
            .average()
            .orElse(0.0);
    }

    private List<DashboardData.Company> getCompaniesWithJobPostCounts() {
        return companiesRepository.findAll().stream()
            .map(company -> {
                long jobPostCount = jobPostRepository.findAll().stream()
                    .filter(jp -> jp.getCompany().getId().equals(company.getId()))
                    .count();
                return new DashboardData.Company(company.getName(), (int) jobPostCount);
            })
            .collect(Collectors.toList());
    }

    private Double getHighestPackage() {
        return placementRepository.findAll().stream()
            .mapToDouble(Placement::getPlaced_package)
            .max()
            .orElse(0.0);
    }

    private String getTopHiringCompany() {
        return placementRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getJobPost().getCompany().getName(),
                Collectors.counting()
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    private String getTopPerformingDepartment() {
        return placementRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getStudent().getDepartment(),
                Collectors.counting()
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    // Enhanced analytics methods

    private DashboardData.PlacementTrends getPlacementTrends() {
        DashboardData.PlacementTrends trends = new DashboardData.PlacementTrends();
        
        // Get placements from last 6 months
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        List<Placement> recentPlacements = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && 
                        p.getPlacementDate().isAfter(sixMonthsAgo))
            .collect(Collectors.toList());

        // Group by month
        Map<String, List<Placement>> placementsByMonth = recentPlacements.stream()
            .collect(Collectors.groupingBy(p -> 
                p.getPlacementDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
            ));

        List<String> dates = new ArrayList<>();
        List<Integer> placedCounts = new ArrayList<>();
        List<Double> averagePackages = new ArrayList<>();

        placementsByMonth.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                dates.add(entry.getKey());
                placedCounts.add(entry.getValue().size());
                double avgPackage = entry.getValue().stream()
                    .mapToDouble(Placement::getPlaced_package)
                    .average().orElse(0.0);
                averagePackages.add(avgPackage);
            });

        trends.setDates(dates);
        trends.setPlacedCounts(placedCounts);
        trends.setAveragePackages(averagePackages);
        trends.setTrend(placedCounts.size() > 1 && 
            placedCounts.get(placedCounts.size()-1) > placedCounts.get(0) ? 
            "INCREASING" : "STABLE");

        return trends;
    }

    private List<DashboardData.DepartmentStats> getDepartmentStats() {
        Map<String, List<Student>> studentsByDept = studentRepository.findAll().stream()
            .collect(Collectors.groupingBy(Student::getDepartment));

        Map<String, List<Placement>> placementsByDept = placementRepository.findAll().stream()
            .collect(Collectors.groupingBy(p -> 
                p.getApplication().getStudent().getDepartment()));

        return studentsByDept.entrySet().stream()
            .map(entry -> {
                String dept = entry.getKey();
                List<Student> students = entry.getValue();
                List<Placement> placements = placementsByDept.getOrDefault(dept, new ArrayList<>());

                DashboardData.DepartmentStats stats = new DashboardData.DepartmentStats();
                stats.setDepartmentName(dept);
                stats.setTotalStudents(students.size());
                stats.setPlacedStudents(placements.size());
                stats.setUnplacedStudents(students.size() - placements.size());
                
                double avgPackage = placements.stream()
                    .mapToDouble(Placement::getPlaced_package)
                    .average().orElse(0.0);
                stats.setAveragePackage(avgPackage);
                
                double placementPercentage = students.size() > 0 ? 
                    ((double) placements.size() / students.size()) * 100 : 0;
                stats.setPlacementPercentage(placementPercentage);

                return stats;
            })
            .collect(Collectors.toList());
    }

    private List<DashboardData.MonthlyPlacement> getMonthlyPlacements() {
        Map<String, List<Placement>> placementsByMonth = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null)
            .collect(Collectors.groupingBy(p -> 
                p.getPlacementDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
            ));

        return placementsByMonth.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                DashboardData.MonthlyPlacement monthly = new DashboardData.MonthlyPlacement();
                monthly.setMonth(entry.getKey());
                monthly.setTotalPlaced(entry.getValue().size());
                
                double avgPackage = entry.getValue().stream()
                    .mapToDouble(Placement::getPlaced_package)
                    .average().orElse(0.0);
                monthly.setAveragePackage(avgPackage);
                
                return monthly;
            })
            .collect(Collectors.toList());
    }

    private DashboardData.ProcessMetrics getProcessMetrics() {
        DashboardData.ProcessMetrics metrics = new DashboardData.ProcessMetrics();
        
        long totalApplications = jobApplicationRepository.count();
        long pendingApplications = jobApplicationRepository.findAll().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.APPLIED || 
                          app.getStatus() == ApplicationStatus.UNDER_REVIEW)
            .count();
        
        long totalPlacements = placementRepository.count();
        
        metrics.setTotalInterviews((int) totalApplications);
        metrics.setPendingApplications((int) pendingApplications);
        metrics.setTotalOffers((int) totalPlacements);
        metrics.setActiveProcesses((int) pendingApplications);
        
        return metrics;
    }

    private List<DashboardData.PackageDistribution> getPackageDistribution() {
        List<Placement> placements = placementRepository.findAll();
        if (placements.isEmpty()) return new ArrayList<>();

        Map<String, Long> packageRanges = placements.stream()
            .collect(Collectors.groupingBy(p -> {
                double pkg = p.getPlaced_package();
                if (pkg < 3) return "0-3 LPA";
                else if (pkg < 5) return "3-5 LPA";
                else if (pkg < 10) return "5-10 LPA";
                else if (pkg < 15) return "10-15 LPA";
                else return "15+ LPA";
            }, Collectors.counting()));

        return packageRanges.entrySet().stream()
            .map(entry -> {
                double percentage = (double) entry.getValue() / placements.size() * 100;
                return new DashboardData.PackageDistribution(
                    entry.getKey(), 
                    entry.getValue().intValue(), 
                    Math.round(percentage * 10) / 10.0
                );
            })
            .collect(Collectors.toList());
    }

    private DashboardData.RecentActivities getRecentActivities() {
        DashboardData.RecentActivities activities = new DashboardData.RecentActivities();
        
        List<DashboardData.RecentActivities.Activity> activityList = placementRepository.findAll().stream()
            .filter(p -> p.getCreatedAt() != null)
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .limit(10)
            .map(placement -> {
                DashboardData.RecentActivities.Activity activity = 
                    new DashboardData.RecentActivities.Activity();
                activity.setType("PLACEMENT");
                activity.setDescription("Student placed at " + 
                    placement.getApplication().getJobPost().getCompany().getName());
                activity.setDate(placement.getPlacementDate());
                activity.setStatus("COMPLETED");
                activity.setStudentName(placement.getApplication().getStudent().getFirstName());
                activity.setCompanyName(placement.getApplication().getJobPost().getCompany().getName());
                return activity;
            })
            .collect(Collectors.toList());
        
        activities.setActivities(activityList);
        activities.setTotalCount(activityList.size());
        
        return activities;
    }

    private DashboardData.CompanyAnalytics getCompanyAnalyticsData() {
        DashboardData.CompanyAnalytics analytics = new DashboardData.CompanyAnalytics();
        
        long totalCompanies = companiesRepository.count();
        long activeCompanies = placementRepository.findAll().stream()
            .map(p -> p.getApplication().getJobPost().getCompany().getId())
            .distinct()
            .count();
        
        analytics.setTotalCompanies((int) totalCompanies);
        analytics.setActiveCompanies((int) activeCompanies);
        
        double avgPackageOffered = getAveragePackageFromPlacements();
        analytics.setAveragePackageOffered(avgPackageOffered);
        
        return analytics;
    }
}
