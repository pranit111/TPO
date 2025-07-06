package com.example.TPO.Tpo.DashBoard;

import com.example.TPO.Companies.CompaniesRepository.CompaniesRepository;
import com.example.TPO.Student.StudentRepository.StudentRepository;
import com.example.TPO.Placements.PlacementRepository.PlacementRepository;
import com.example.TPO.JobApplication.JobApplicationRepository.JobApplicationRepository;
import com.example.TPO.JobPost.JobPostRepository.JobPostRepository;
import com.example.TPO.DBMS.Applications.ApplicationStatus;
import com.example.TPO.DBMS.Placements.Placement;
import com.example.TPO.DBMS.stud.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    JobPostRepository jobPostRepository;
    
    // Cache for performance optimization
    private final Map<String, Object> dataCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 300000; // 5 minutes
    
    public DashboardData getDashboardData() {
        DashboardData dashboardData = new DashboardData();

        // Real Student Data
        DashboardData.StudentData studentData = getStudentAnalytics();
        dashboardData.setStudentData(studentData);

        // Real Company Data  
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
        dashboardData.setPackageDistribution(getPackageDistribution());
        dashboardData.setRecentActivities(getRecentActivities());
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

    // Enhanced methods for dynamic dashboard functionality
    
    public Map<String, Object> getRealTimeStatistics() {
        String cacheKey = "realTimeStats";
        if (isCacheValid(cacheKey)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cachedData = (Map<String, Object>) dataCache.get(cacheKey);
            return cachedData;
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // Current metrics
        long totalStudents = studentRepository.count();
        long totalPlacements = placementRepository.count();
        long totalCompanies = companiesRepository.count();
        long activeApplications = jobApplicationRepository.findAll().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.APPLIED || 
                          app.getStatus() == ApplicationStatus.UNDER_REVIEW)
            .count();
        
        // Calculate growth from last month
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        long newPlacementsThisMonth = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && p.getPlacementDate().isAfter(lastMonth))
            .count();
        
        stats.put("totalStudents", totalStudents);
        stats.put("totalPlacements", totalPlacements);
        stats.put("totalCompanies", totalCompanies);
        stats.put("activeApplications", activeApplications);
        stats.put("newPlacementsThisMonth", newPlacementsThisMonth);
        stats.put("placementRate", totalStudents > 0 ? (double) totalPlacements / totalStudents * 100 : 0);
        stats.put("lastUpdated", LocalDate.now().toString());
        
        cacheData(cacheKey, stats);
        return stats;
    }

    public Map<String, Object> getGrowthAnalytics(int months) {
        Map<String, Object> growthData = new HashMap<>();
        LocalDate startDate = LocalDate.now().minusMonths(months);
        
        // Get monthly placement data
        List<Placement> placements = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && p.getPlacementDate().isAfter(startDate))
            .collect(Collectors.toList());
        
        Map<String, List<Placement>> monthlyData = placements.stream()
            .collect(Collectors.groupingBy(p -> 
                p.getPlacementDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))));
        
        List<String> labels = new ArrayList<>();
        List<Integer> placementCounts = new ArrayList<>();
        List<Double> averagePackages = new ArrayList<>();
        
        for (int i = months - 1; i >= 0; i--) {
            LocalDate month = LocalDate.now().minusMonths(i);
            String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String monthLabel = month.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            
            labels.add(monthLabel);
            
            List<Placement> monthPlacements = monthlyData.getOrDefault(monthKey, new ArrayList<>());
            placementCounts.add(monthPlacements.size());
            
            double avgPackage = monthPlacements.stream()
                .mapToDouble(Placement::getPlaced_package)
                .average().orElse(0.0);
            averagePackages.add(Math.round(avgPackage * 100.0) / 100.0);
        }
        
        // Calculate growth rate
        double growthRate = 0.0;
        if (placementCounts.size() >= 2) {
            int current = placementCounts.get(placementCounts.size() - 1);
            int previous = placementCounts.get(placementCounts.size() - 2);
            if (previous > 0) {
                growthRate = ((double) (current - previous) / previous) * 100;
            }
        }
        
        growthData.put("labels", labels);
        growthData.put("placementCounts", placementCounts);
        growthData.put("averagePackages", averagePackages);
        growthData.put("growthRate", Math.round(growthRate * 100.0) / 100.0);
        growthData.put("totalPlacements", placementCounts.stream().mapToInt(Integer::intValue).sum());
        
        return growthData;
    }

    public DashboardData.RecentActivities getRecentActivitiesWithLimit(int limit) {
        DashboardData.RecentActivities activities = new DashboardData.RecentActivities();
        
        List<DashboardData.RecentActivities.Activity> activityList = placementRepository.findAll().stream()
            .filter(p -> p.getCreatedAt() != null)
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .limit(limit)
            .map(placement -> {
                DashboardData.RecentActivities.Activity activity = 
                    new DashboardData.RecentActivities.Activity();
                activity.setType("PLACEMENT");
                activity.setDescription("Student placed at " + 
                    placement.getApplication().getJobPost().getCompany().getName());
                activity.setDate(placement.getPlacementDate());
                activity.setStatus("COMPLETED");
                activity.setStudentName(placement.getApplication().getStudent().getFirstName() + " " +
                    placement.getApplication().getStudent().getLastName());
                activity.setCompanyName(placement.getApplication().getJobPost().getCompany().getName());
                return activity;
            })
            .collect(Collectors.toList());
        
        activities.setActivities(activityList);
        activities.setTotalCount(activityList.size());
        
        return activities;
    }

    public DashboardData.ProcessMetrics getDetailedProcessMetrics() {
        DashboardData.ProcessMetrics metrics = new DashboardData.ProcessMetrics();
        
        long totalApplications = jobApplicationRepository.count();
        long pendingApplications = jobApplicationRepository.findAll().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.APPLIED || 
                          app.getStatus() == ApplicationStatus.UNDER_REVIEW)
            .count();
        
        long acceptedApplications = jobApplicationRepository.findAll().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.OFFERED || 
                          app.getStatus() == ApplicationStatus.HIRED)
            .count();
        
        long totalPlacements = placementRepository.count();
        
        metrics.setTotalInterviews((int) totalApplications);
        metrics.setPendingApplications((int) pendingApplications);
        metrics.setTotalOffers((int) acceptedApplications);
        metrics.setTotalAcceptances((int) totalPlacements);
        metrics.setActiveProcesses((int) pendingApplications);
        
        // Calculate acceptance rate
        double acceptanceRate = totalApplications > 0 ? 
            (double) acceptedApplications / totalApplications * 100 : 0;
        metrics.setOfferAcceptanceRate(Math.round(acceptanceRate * 100.0) / 100.0);
        
        return metrics;
    }

    public Map<String, Object> getComprehensiveDepartmentAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Get department-wise statistics
        Map<String, List<Student>> studentsByDept = studentRepository.findAll().stream()
            .collect(Collectors.groupingBy(Student::getDepartment));
        
        Map<String, List<Placement>> placementsByDept = placementRepository.findAll().stream()
            .collect(Collectors.groupingBy(p -> 
                p.getApplication().getStudent().getDepartment()));
        
        List<Map<String, Object>> departmentData = new ArrayList<>();
        
        for (Map.Entry<String, List<Student>> entry : studentsByDept.entrySet()) {
            String dept = entry.getKey();
            List<Student> students = entry.getValue();
            List<Placement> placements = placementsByDept.getOrDefault(dept, new ArrayList<>());

            Map<String, Object> deptStats = new HashMap<>();
            deptStats.put("name", dept);
            deptStats.put("totalStudents", students.size());
            deptStats.put("placedStudents", placements.size());
            deptStats.put("placementRate", students.size() > 0 ? 
                (double) placements.size() / students.size() * 100 : 0);
            
            double avgPackage = placements.stream()
                .mapToDouble(Placement::getPlaced_package)
                .average().orElse(0.0);
            deptStats.put("averagePackage", Math.round(avgPackage * 100.0) / 100.0);
            
            double highestPackage = placements.stream()
                .mapToDouble(Placement::getPlaced_package)
                .max().orElse(0.0);
            deptStats.put("highestPackage", highestPackage);
            
            departmentData.add(deptStats);
        }
        
        // Sort by placement rate
        departmentData.sort((a, b) -> 
            Double.compare((Double) b.get("placementRate"), (Double) a.get("placementRate")));
        
        analytics.put("departments", departmentData);
        analytics.put("totalDepartments", departmentData.size());
        
        return analytics;
    }

    public DashboardData.CompanyAnalytics getDetailedCompanyAnalytics() {
        DashboardData.CompanyAnalytics analytics = new DashboardData.CompanyAnalytics();
        
        long totalCompanies = companiesRepository.count();
        
        // Get companies that have hired students
        Set<Long> activeCompanyIds = placementRepository.findAll().stream()
            .map(p -> p.getApplication().getJobPost().getCompany().getId())
            .collect(Collectors.toSet());
        
        analytics.setTotalCompanies((int) totalCompanies);
        analytics.setActiveCompanies(activeCompanyIds.size());
        
        // Calculate new companies this year
        LocalDate yearStart = LocalDate.now().withDayOfYear(1);
        long newCompaniesThisYear = companiesRepository.findAll().stream()
            .filter(c -> c.getAssociatedSince() != null && c.getAssociatedSince().isAfter(yearStart))
            .count();
        analytics.setNewCompaniesThisYear((int) newCompaniesThisYear);
        
        double avgPackageOffered = getAveragePackageFromPlacements();
        analytics.setAveragePackageOffered(avgPackageOffered);
        
        // Get top performing companies
        Map<String, Long> companyHires = placementRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getJobPost().getCompany().getName(),
                Collectors.counting()
            ));
        
        List<DashboardData.CompanyAnalytics.CompanyPerformance> topCompanies = 
            companyHires.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    DashboardData.CompanyAnalytics.CompanyPerformance performance = 
                        new DashboardData.CompanyAnalytics.CompanyPerformance();
                    performance.setCompanyName(entry.getKey());
                    performance.setTotalHires(entry.getValue().intValue());
                    
                    // Calculate average package for this company
                    double avgPkg = placementRepository.findAll().stream()
                        .filter(p -> p.getApplication().getJobPost().getCompany().getName().equals(entry.getKey()))
                        .mapToDouble(Placement::getPlaced_package)
                        .average().orElse(0.0);
                    performance.setAveragePackage(Math.round(avgPkg * 100.0) / 100.0);
                    
                    return performance;
                })
                .collect(Collectors.toList());
        
        analytics.setTopPerformingCompanies(topCompanies);
        
        return analytics;
    }

    public DashboardData.PlacementTrends getPlacementTrendsForPeriod(int months) {
        DashboardData.PlacementTrends trends = new DashboardData.PlacementTrends();
        
        LocalDate startDate = LocalDate.now().minusMonths(months);
        List<Placement> recentPlacements = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && 
                        p.getPlacementDate().isAfter(startDate))
            .collect(Collectors.toList());

        Map<String, List<Placement>> placementsByMonth = recentPlacements.stream()
            .collect(Collectors.groupingBy(p -> 
                p.getPlacementDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
            ));

        List<String> dates = new ArrayList<>();
        List<Integer> placedCounts = new ArrayList<>();
        List<Double> averagePackages = new ArrayList<>();

        for (int i = months - 1; i >= 0; i--) {
            LocalDate month = LocalDate.now().minusMonths(i);
            String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String monthLabel = month.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            
            dates.add(monthLabel);
            
            List<Placement> monthPlacements = placementsByMonth.getOrDefault(monthKey, new ArrayList<>());
            placedCounts.add(monthPlacements.size());
            
            double avgPackage = monthPlacements.stream()
                .mapToDouble(Placement::getPlaced_package)
                .average().orElse(0.0);
            averagePackages.add(Math.round(avgPackage * 100.0) / 100.0);
        }

        trends.setDates(dates);
        trends.setPlacedCounts(placedCounts);
        trends.setAveragePackages(averagePackages);
        
        // Determine trend
        if (placedCounts.size() >= 2) {
            int recent = placedCounts.get(placedCounts.size() - 1);
            int previous = placedCounts.get(placedCounts.size() - 2);
            if (recent > previous) {
                trends.setTrend("INCREASING");
            } else if (recent < previous) {
                trends.setTrend("DECREASING");
            } else {
                trends.setTrend("STABLE");
            }
        } else {
            trends.setTrend("STABLE");
        }

        return trends;
    }

    public byte[] exportDashboardData(String format) throws IOException {
        if ("excel".equalsIgnoreCase(format)) {
            return exportToExcel();
        } else if ("pdf".equalsIgnoreCase(format)) {
            return exportToPDF();
        } else {
            throw new UnsupportedOperationException("Format not supported: " + format);
        }
    }

    private byte[] exportToExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Overview Sheet
            Sheet overviewSheet = workbook.createSheet("Dashboard Overview");
            createOverviewSheet(overviewSheet);
            
            // Department Analytics Sheet
            Sheet deptSheet = workbook.createSheet("Department Analytics");
            createDepartmentSheet(deptSheet);
            
            // Company Analytics Sheet
            Sheet companySheet = workbook.createSheet("Company Analytics");
            createCompanySheet(companySheet);
            
            // Recent Activities Sheet
            Sheet activitiesSheet = workbook.createSheet("Recent Activities");
            createActivitiesSheet(activitiesSheet);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createOverviewSheet(Sheet sheet) {
        // Create header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Metric");
        headerRow.createCell(1).setCellValue("Value");
        
        // Add data
        DashboardData data = getDashboardData();
        int rowNum = 1;
        
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Total Students");
        row.createCell(1).setCellValue(data.getStudentData().getTotal());
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Placed Students");
        row.createCell(1).setCellValue(data.getStudentData().getPlaced());
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Placement Rate (%)");
        row.createCell(1).setCellValue(data.getStudentData().getPlacementRate());
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Average Package (LPA)");
        row.createCell(1).setCellValue(data.getStudentData().getAveragePackage());
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Total Companies");
        row.createCell(1).setCellValue(data.getCompaniesData().getTotal());
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createDepartmentSheet(Sheet sheet) {
        // Create header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Department");
        headerRow.createCell(1).setCellValue("Total Students");
        headerRow.createCell(2).setCellValue("Placed Students");
        headerRow.createCell(3).setCellValue("Placement Rate (%)");
        headerRow.createCell(4).setCellValue("Average Package (LPA)");
        
        // Add data
        List<DashboardData.DepartmentStats> deptStats = getDepartmentStats();
        int rowNum = 1;
        
        for (DashboardData.DepartmentStats stats : deptStats) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(stats.getDepartmentName());
            row.createCell(1).setCellValue(stats.getTotalStudents());
            row.createCell(2).setCellValue(stats.getPlacedStudents());
            row.createCell(3).setCellValue(stats.getPlacementPercentage());
            row.createCell(4).setCellValue(stats.getAveragePackage());
        }
        
        // Auto-size columns
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createCompanySheet(Sheet sheet) {
        // Implementation for company analytics sheet
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Company Name");
        headerRow.createCell(1).setCellValue("Total Hires");
        headerRow.createCell(2).setCellValue("Average Package (LPA)");
        
        DashboardData.CompanyAnalytics analytics = getDetailedCompanyAnalytics();
        int rowNum = 1;
        
        for (DashboardData.CompanyAnalytics.CompanyPerformance company : analytics.getTopPerformingCompanies()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(company.getCompanyName());
            row.createCell(1).setCellValue(company.getTotalHires());
            row.createCell(2).setCellValue(company.getAveragePackage());
        }
        
        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createActivitiesSheet(Sheet sheet) {
        // Implementation for recent activities sheet
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Type");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Student");
        headerRow.createCell(3).setCellValue("Company");
        headerRow.createCell(4).setCellValue("Date");
        headerRow.createCell(5).setCellValue("Status");
        
        DashboardData.RecentActivities activities = getRecentActivitiesWithLimit(50);
        int rowNum = 1;
        
        for (DashboardData.RecentActivities.Activity activity : activities.getActivities()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(activity.getType());
            row.createCell(1).setCellValue(activity.getDescription());
            row.createCell(2).setCellValue(activity.getStudentName());
            row.createCell(3).setCellValue(activity.getCompanyName());
            row.createCell(4).setCellValue(activity.getDate() != null ? activity.getDate().toString() : "");
            row.createCell(5).setCellValue(activity.getStatus());
        }
        
        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private byte[] exportToPDF() throws IOException {
        // Basic PDF implementation - in production, you'd use iText or similar
        // For now, return a simple text-based PDF or throw exception
        StringBuilder pdfContent = new StringBuilder();
        pdfContent.append("TPO Dashboard Report\n");
        pdfContent.append("====================\n\n");
        
        DashboardData data = getDashboardData();
        pdfContent.append("Student Statistics:\n");
        pdfContent.append("- Total Students: ").append(data.getStudentData().getTotal()).append("\n");
        pdfContent.append("- Placed Students: ").append(data.getStudentData().getPlaced()).append("\n");
        pdfContent.append("- Placement Rate: ").append(data.getStudentData().getPlacementRate()).append("%\n");
        pdfContent.append("- Average Package: ").append(data.getStudentData().getAveragePackage()).append(" LPA\n\n");
        
        pdfContent.append("Company Statistics:\n");
        pdfContent.append("- Total Companies: ").append(data.getCompaniesData().getTotal()).append("\n");
        pdfContent.append("- Total Openings: ").append(data.getCompaniesData().getTotalOpenings()).append("\n\n");
        
        pdfContent.append("Top Insights:\n");
        pdfContent.append("- Highest Package: ").append(data.getInsightsData().getHighestPackage()).append("\n");
        pdfContent.append("- Average Package: ").append(data.getInsightsData().getAveragePackage()).append("\n");
        pdfContent.append("- Top Hiring Company: ").append(data.getInsightsData().getTopHiringCompany()).append("\n");
        pdfContent.append("- Top Department: ").append(data.getInsightsData().getTopDepartment()).append("\n");
        
        // For now, return as text bytes. In production, use proper PDF library
        return pdfContent.toString().getBytes();
    }

    public Map<String, Object> getDashboardNotifications() {
        Map<String, Object> notifications = new HashMap<>();
        List<Map<String, String>> alerts = new ArrayList<>();
        
        // Check for low placement rates
        double overallPlacementRate = getStudentAnalytics().getPlacementRate();
        if (overallPlacementRate < 50) {
            Map<String, String> alert = new HashMap<>();
            alert.put("type", "warning");
            alert.put("message", "Overall placement rate is below 50%");
            alert.put("action", "Review placement strategies");
            alerts.add(alert);
        }
        
        // Check for departments with low placement
        List<DashboardData.DepartmentStats> deptStats = getDepartmentStats();
        for (DashboardData.DepartmentStats stats : deptStats) {
            if (stats.getPlacementPercentage() < 30) {
                Map<String, String> alert = new HashMap<>();
                alert.put("type", "info");
                alert.put("message", stats.getDepartmentName() + " department has low placement rate");
                alert.put("action", "Focus on " + stats.getDepartmentName() + " placements");
                alerts.add(alert);
            }
        }
        
        // Check for recent high-value placements
        List<Placement> recentHighPlacements = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && 
                        p.getPlacementDate().isAfter(LocalDate.now().minusDays(7)) &&
                        p.getPlaced_package() > 10.0)
            .collect(Collectors.toList());
        
        if (!recentHighPlacements.isEmpty()) {
            Map<String, String> alert = new HashMap<>();
            alert.put("type", "success");
            alert.put("message", recentHighPlacements.size() + " high-value placements this week!");
            alert.put("action", "Celebrate success and share news");
            alerts.add(alert);
        }
        
        notifications.put("alerts", alerts);
        notifications.put("totalAlerts", alerts.size());
        
        return notifications;
    }

    public void refreshCachedData() {
        dataCache.clear();
        cacheTimestamps.clear();
    }

    // Cache management helpers
    private boolean isCacheValid(String key) {
        Long timestamp = cacheTimestamps.get(key);
        return timestamp != null && 
               (System.currentTimeMillis() - timestamp) < CACHE_DURATION &&
               dataCache.containsKey(key);
    }

    private void cacheData(String key, Object data) {
        dataCache.put(key, data);
        cacheTimestamps.put(key, System.currentTimeMillis());
    }

    // Student Management Methods
    public Map<String, Object> getStudentsWithFilters(int page, int size, String search, String department, String status) {
        Map<String, Object> result = new HashMap<>();
        
        List<Student> allStudents = studentRepository.findAll();
        
        // Apply filters
        List<Student> filteredStudents = allStudents.stream()
            .filter(s -> search.isEmpty() || 
                (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(search.toLowerCase()) ||
                s.getUser().getUsername().toLowerCase().contains(search.toLowerCase()))
            .filter(s -> department.isEmpty() || s.getDepartment().equalsIgnoreCase(department))
            .collect(Collectors.toList());
        
        // Apply status filter (placed/unplaced)
        if (!status.isEmpty()) {
            Set<Long> placedStudentIds = placementRepository.findAll().stream()
                .map(p -> p.getApplication().getStudent().getId())
                .collect(Collectors.toSet());
                
            if ("placed".equalsIgnoreCase(status)) {
                filteredStudents = filteredStudents.stream()
                    .filter(s -> placedStudentIds.contains(s.getId()))
                    .collect(Collectors.toList());
            } else if ("unplaced".equalsIgnoreCase(status)) {
                filteredStudents = filteredStudents.stream()
                    .filter(s -> !placedStudentIds.contains(s.getId()))
                    .collect(Collectors.toList());
            }
        }
        
        // Pagination
        int start = page * size;
        int end = Math.min(start + size, filteredStudents.size());
        List<Student> paginatedStudents = filteredStudents.subList(start, end);
        
        // Transform to response format
        List<Map<String, Object>> studentData = paginatedStudents.stream()
            .map(this::studentToMap)
            .collect(Collectors.toList());
        
        result.put("students", studentData);
        result.put("totalElements", filteredStudents.size());
        result.put("totalPages", (int) Math.ceil((double) filteredStudents.size() / size));
        result.put("currentPage", page);
        result.put("pageSize", size);
        
        return result;
    }

    public Map<String, Object> getStudentDetails(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return Map.of("error", "Student not found");
        }
        
        Map<String, Object> details = studentToMap(student);
        
        // Add placement information
        Optional<Placement> placement = placementRepository.findAll().stream()
            .filter(p -> p.getApplication().getStudent().getId().equals(id))
            .findFirst();
        
        if (placement.isPresent()) {
            details.put("placementStatus", "PLACED");
            details.put("placementPackage", placement.get().getPlaced_package());
            details.put("placementCompany", placement.get().getApplication().getJobPost().getCompany().getName());
            details.put("placementDate", placement.get().getPlacementDate());
        } else {
            details.put("placementStatus", "UNPLACED");
        }
        
        return details;
    }

    private Map<String, Object> studentToMap(Student student) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", student.getId());
        map.put("firstName", student.getFirstName());
        map.put("lastName", student.getLastName());
        map.put("email", student.getUser() != null ? student.getUser().getUsername() : "");
        map.put("department", student.getDepartment());
        map.put("phone", student.getPhoneNumber());
        map.put("avgMarks", student.getAvgMarks());
        map.put("verified", student.isResults_verified());
        map.put("batch", student.getAcademicyear());
        return map;
    }

    // Company Management Methods
    public Map<String, Object> getCompaniesWithFilters(int page, int size, String search, String industry) {
        Map<String, Object> result = new HashMap<>();
        
        List<com.example.TPO.DBMS.Company.Company> allCompanies = companiesRepository.findAll();
        
        // Apply filters
        List<com.example.TPO.DBMS.Company.Company> filteredCompanies = allCompanies.stream()
            .filter(c -> search.isEmpty() || 
                c.getName().toLowerCase().contains(search.toLowerCase()) ||
                c.getHr_Name().toLowerCase().contains(search.toLowerCase()))
            .filter(c -> industry.isEmpty() || c.getIndustryType().toLowerCase().contains(industry.toLowerCase()))
            .collect(Collectors.toList());
        
        // Pagination
        int start = page * size;
        int end = Math.min(start + size, filteredCompanies.size());
        List<com.example.TPO.DBMS.Company.Company> paginatedCompanies = filteredCompanies.subList(start, end);
        
        // Transform to response format
        List<Map<String, Object>> companyData = paginatedCompanies.stream()
            .map(this::companyToMap)
            .collect(Collectors.toList());
        
        result.put("companies", companyData);
        result.put("totalElements", filteredCompanies.size());
        result.put("totalPages", (int) Math.ceil((double) filteredCompanies.size() / size));
        result.put("currentPage", page);
        result.put("pageSize", size);
        
        return result;
    }

    public Map<String, Object> getCompanyDetails(Long id) {
        com.example.TPO.DBMS.Company.Company company = companiesRepository.findById(id).orElse(null);
        if (company == null) {
            return Map.of("error", "Company not found");
        }
        
        Map<String, Object> details = companyToMap(company);
        
        // Add hiring statistics
        long totalHires = placementRepository.findAll().stream()
            .filter(p -> p.getApplication().getJobPost().getCompany().getId().equals(id))
            .count();
        
        double avgPackage = placementRepository.findAll().stream()
            .filter(p -> p.getApplication().getJobPost().getCompany().getId().equals(id))
            .mapToDouble(Placement::getPlaced_package)
            .average().orElse(0.0);
        
        details.put("totalHires", totalHires);
        details.put("averagePackage", avgPackage);
        
        return details;
    }

    private Map<String, Object> companyToMap(com.example.TPO.DBMS.Company.Company company) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", company.getId());
        map.put("name", company.getName());
        map.put("industry", company.getIndustryType());
        map.put("hrName", company.getHr_Name());
        map.put("hrContact", company.getContactNumber());
        map.put("location", company.getLocation());
        map.put("website", company.getWebsite());
        map.put("isMNC", company.isMnc());
        map.put("associatedSince", company.getAssociatedSince());
        return map;
    }

    // Activity Logs Management
    public Map<String, Object> getActivityLogsWithFilters(int page, int size, String action, String entity, String dateFrom, String dateTo) {
        Map<String, Object> result = new HashMap<>();
        
        // For now, simulate activity logs from placement data
        List<Map<String, Object>> logs = new ArrayList<>();
        
        List<Placement> placements = placementRepository.findAll();
        for (Placement placement : placements) {
            Map<String, Object> log = new HashMap<>();
            log.put("id", placement.getId());
            log.put("action", "PLACEMENT_CREATED");
            log.put("entity", "PLACEMENT");
            log.put("entityId", placement.getId());
            log.put("performedBy", "SYSTEM");
            log.put("timestamp", placement.getCreatedAt());
            log.put("details", "Student placed at " + placement.getApplication().getJobPost().getCompany().getName());
            logs.add(log);
        }
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, logs.size());
        List<Map<String, Object>> paginatedLogs = logs.subList(start, end);
        
        result.put("logs", paginatedLogs);
        result.put("totalElements", logs.size());
        result.put("totalPages", (int) Math.ceil((double) logs.size() / size));
        result.put("currentPage", page);
        result.put("pageSize", size);
        
        return result;
    }

    // Helper methods for yearly data filtering and calculations
    private List<Student> getYearlyStudents(int year) {
        // Filter students by year of passing
        return studentRepository.findAll().stream()
            .filter(s -> s.getYearOfPassing() == year)
            .collect(Collectors.toList());
    }

    private List<com.example.TPO.DBMS.Company.Company> getYearlyCompanies(int year) {
        return companiesRepository.findAll().stream()
            .filter(c -> c.getAssociatedSince() != null && 
                    c.getAssociatedSince().getYear() <= year )
            .collect(Collectors.toList());
    }

    private List<Placement> getYearlyPlacements(int year) {
        return placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && 
                    p.getPlacementDate().getYear() == year)
            .collect(Collectors.toList());
    }

    private List<?> getYearlyJobPosts(int year) {
        return jobPostRepository.findAll().stream()
            .filter(jp -> jp.getApplicationStartDate() != null && 
                    jp.getApplicationStartDate().getYear() == year )
            .collect(Collectors.toList());
    }

    private List<?> getYearlyJobApplications(int year) {
        // Filter by application date
        return jobApplicationRepository.findAll().stream()
            .filter(ja -> ja.getApplicationDate() != null && 
                    ja.getApplicationDate().getYear() == year )
            .collect(Collectors.toList());
    }

    private List<com.example.TPO.DBMS.JobPost.JobPost> getYearlyJobPostsTyped(int year) {
        return jobPostRepository.findAll().stream()
            .filter(jp -> jp.getApplicationStartDate() != null && 
                    jp.getApplicationStartDate().getYear() == year )
            .collect(Collectors.toList());
    }

    private List<com.example.TPO.DBMS.Applications.JobApplication> getYearlyJobApplicationsTyped(int year) {
        // Filter by application date
        return jobApplicationRepository.findAll().stream()
            .filter(ja -> ja.getApplicationDate() != null && 
                    ja.getApplicationDate().getYear() == year )
            .collect(Collectors.toList());
    }

    // Helper methods for yearly analytics
    private int getYearlyStudentCount(int year) {
        return getYearlyStudents(year).size();
    }

    private int getYearlyPlacementCount(int year) {
        return getYearlyPlacements(year).size();
    }

    private double getYearlyPlacementRate(int year) {
        int totalStudents = getYearlyStudentCount(year);
        int placedStudents = getYearlyPlacementCount(year);
        return totalStudents > 0 ? ((double) placedStudents / totalStudents) * 100 : 0;
    }

    private double getYearlyAveragePackage(int year) {
        List<Placement> yearlyPlacements = getYearlyPlacements(year);
        return yearlyPlacements.stream()
            .mapToDouble(Placement::getPlaced_package)
            .average()
            .orElse(0.0);
    }

    private double getYearlyHighestPackage(int year) {
        List<Placement> yearlyPlacements = getYearlyPlacements(year);
        return yearlyPlacements.stream()
            .mapToDouble(Placement::getPlaced_package)
            .max()
            .orElse(0.0);
    }

    private int getYearlyCompanyCount(int year) {
        return getYearlyCompanies(year).size();
    }

    private int getYearlyJobPostCount(int year) {
        return getYearlyJobPosts(year).size();
    }

    private int getYearlyJobApplicationCount(int year) {
        return getYearlyJobApplications(year).size();
    }

    // Advanced Analytics Methods
    public Map<String, Object> getMultiYearComparisonAnalytics(List<Integer> years) {
        Map<String, Object> comparison = new HashMap<>();
        
        List<Map<String, Object>> yearlyData = new ArrayList<>();
        
        for (int year : years) {
            Map<String, Object> yearData = new HashMap<>();
            yearData.put("year", year);
            yearData.put("totalStudents", getYearlyStudentCount(year));
            yearData.put("totalPlacements", getYearlyPlacementCount(year));
            yearData.put("placementRate", getYearlyPlacementRate(year));
            yearData.put("averagePackage", getYearlyAveragePackage(year));
            yearData.put("highestPackage", getYearlyHighestPackage(year));
            yearData.put("totalCompanies", getYearlyCompanyCount(year));
            
            yearlyData.add(yearData);
        }
        
        comparison.put("yearlyData", yearlyData);
        
        // Calculate trends
        if (years.size() >= 2) {
            Map<String, Object> trends = new HashMap<>();
            
            // Placement rate trend
            List<Double> placementRates = yearlyData.stream()
                .map(data -> (Double) data.get("placementRate"))
                .collect(Collectors.toList());
            trends.put("placementRateTrend", calculateTrend(placementRates));
            
            // Average package trend
            List<Double> avgPackages = yearlyData.stream()
                .map(data -> (Double) data.get("averagePackage"))
                .collect(Collectors.toList());
            trends.put("averagePackageTrend", calculateTrend(avgPackages));
            
            // Company participation trend
            List<Integer> companyCount = yearlyData.stream()
                .map(data -> (Integer) data.get("totalCompanies"))
                .collect(Collectors.toList());
            trends.put("companyParticipationTrend", calculateTrendInteger(companyCount));
            
            comparison.put("trends", trends);
        }
        
        return comparison;
    }

    public Map<String, Object> getSkillBasedAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Get all job posts and their skill requirements
        List<com.example.TPO.DBMS.JobPost.JobPost> jobPosts = jobPostRepository.findAll();
        
        // Extract and analyze skill requirements
        Map<String, Long> skillDemand = jobPosts.stream()
            .filter(jp -> jp.getSkillRequirements() != null && !jp.getSkillRequirements().trim().isEmpty())
            .flatMap(jp -> Arrays.stream(jp.getSkillRequirements().split(",|;|\\|")))
            .map(skill -> skill.trim().toLowerCase())
            .filter(skill -> !skill.isEmpty())
            .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        
        // Get top skills
        List<Map<String, Object>> topSkills = skillDemand.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(20)
            .map(entry -> {
                Map<String, Object> skillMap = new HashMap<>();
                skillMap.put("skill", entry.getKey());
                skillMap.put("demand", entry.getValue());
                return skillMap;
            })
            .collect(Collectors.toList());
        
        analytics.put("topSkills", topSkills);
        analytics.put("totalUniqueSkills", skillDemand.size());
        
        return analytics;
    }

    public Map<String, Object> getLocationBasedAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Job location analysis
        List<com.example.TPO.DBMS.JobPost.JobPost> jobPosts = jobPostRepository.findAll();
        
        Map<String, Long> locationDemand = jobPosts.stream()
            .filter(jp -> jp.getLocation() != null && !jp.getLocation().trim().isEmpty())
            .collect(Collectors.groupingBy(
                jp -> jp.getLocation().trim(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> topLocations = locationDemand.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(15)
            .map(entry -> {
                Map<String, Object> locationMap = new HashMap<>();
                locationMap.put("location", entry.getKey());
                locationMap.put("jobCount", entry.getValue());
                return locationMap;
            })
            .collect(Collectors.toList());
        
        analytics.put("topJobLocations", topLocations);
        
        // Placement location analysis
        List<Placement> placements = placementRepository.findAll();
        
        Map<String, Long> placementLocations = placements.stream()
            .filter(p -> p.getApplication() != null && 
                    p.getApplication().getJobPost() != null &&
                    p.getApplication().getJobPost().getLocation() != null)
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getJobPost().getLocation().trim(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> topPlacementLocations = placementLocations.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .map(entry -> {
                Map<String, Object> locationMap = new HashMap<>();
                locationMap.put("location", entry.getKey());
                locationMap.put("placementCount", entry.getValue());
                return locationMap;
            })
            .collect(Collectors.toList());
        
        analytics.put("topPlacementLocations", topPlacementLocations);
        
        return analytics;
    }

    public Map<String, Object> getSalaryTrendAnalytics(int months) {
        Map<String, Object> analytics = new HashMap<>();
        
        LocalDate startDate = LocalDate.now().minusMonths(months);
        
        List<Placement> recentPlacements = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && p.getPlacementDate().isAfter(startDate))
            .sorted(Comparator.comparing(Placement::getPlacementDate))
            .collect(Collectors.toList());
        
        // Monthly salary trends
        Map<String, List<Placement>> monthlyPlacements = recentPlacements.stream()
            .collect(Collectors.groupingBy(p -> 
                p.getPlacementDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))));
        
        List<Map<String, Object>> monthlyTrends = monthlyPlacements.entrySet().stream()
            .map(entry -> {
                String month = entry.getKey();
                List<Placement> placements = entry.getValue();
                
                double avgSalary = placements.stream()
                    .mapToDouble(Placement::getPlaced_package)
                    .average().orElse(0.0);
                
                double maxSalary = placements.stream()
                    .mapToDouble(Placement::getPlaced_package)
                    .max().orElse(0.0);
                
                double minSalary = placements.stream()
                    .mapToDouble(Placement::getPlaced_package)
                    .min().orElse(0.0);
                
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", month);
                monthData.put("placementCount", placements.size());
                monthData.put("averageSalary", Math.round(avgSalary * 100.0) / 100.0);
                monthData.put("maxSalary", maxSalary);
                monthData.put("minSalary", minSalary);
                
                return monthData;
            })
            .sorted(Comparator.comparing(data -> (String) data.get("month")))
            .collect(Collectors.toList());
        
        analytics.put("monthlyTrends", monthlyTrends);
        
        // Department-wise salary trends
        Map<String, Double> departmentAvgSalary = recentPlacements.stream()
            .filter(p -> p.getApplication() != null && p.getApplication().getStudent() != null)
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getStudent().getDepartment(),
                Collectors.averagingDouble(Placement::getPlaced_package)
            ));
        
        List<Map<String, Object>> departmentSalaryTrends = departmentAvgSalary.entrySet().stream()
            .map(entry -> {
                Map<String, Object> deptData = new HashMap<>();
                deptData.put("department", entry.getKey());
                deptData.put("averageSalary", Math.round(entry.getValue() * 100.0) / 100.0);
                return deptData;
            })
            .sorted((a, b) -> Double.compare((Double) b.get("averageSalary"), (Double) a.get("averageSalary")))
            .collect(Collectors.toList());
        
        analytics.put("departmentSalaryTrends", departmentSalaryTrends);
        
        return analytics;
    }

    public Map<String, Object> getApplicationSuccessRateAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        List<com.example.TPO.DBMS.Applications.JobApplication> applications = jobApplicationRepository.findAll();
        List<Placement> placements = placementRepository.findAll();
        
        // Overall success rate
        long totalApplications = applications.size();
        long successfulApplications = placements.size();
        double overallSuccessRate = totalApplications > 0 ? 
            ((double) successfulApplications / totalApplications) * 100 : 0;
        
        analytics.put("totalApplications", totalApplications);
        analytics.put("successfulApplications", successfulApplications);
        analytics.put("overallSuccessRate", Math.round(overallSuccessRate * 100.0) / 100.0);
        
        // Department-wise success rate
        Map<String, Long> applicationsByDept = applications.stream()
            .filter(app -> app.getStudent() != null)
            .collect(Collectors.groupingBy(
                app -> app.getStudent().getDepartment(),
                Collectors.counting()
            ));
        
        Map<String, Long> successfulApplicationsByDept = placements.stream()
            .filter(p -> p.getApplication() != null && p.getApplication().getStudent() != null)
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getStudent().getDepartment(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> departmentSuccessRates = applicationsByDept.entrySet().stream()
            .map(entry -> {
                String dept = entry.getKey();
                long totalApps = entry.getValue();
                long successfulApps = successfulApplicationsByDept.getOrDefault(dept, 0L);
                double successRate = totalApps > 0 ? ((double) successfulApps / totalApps) * 100 : 0;
                
                Map<String, Object> deptData = new HashMap<>();
                deptData.put("department", dept);
                deptData.put("totalApplications", totalApps);
                deptData.put("successfulApplications", successfulApps);
                deptData.put("successRate", Math.round(successRate * 100.0) / 100.0);
                
                return deptData;
            })
            .sorted((a, b) -> Double.compare((Double) b.get("successRate"), (Double) a.get("successRate")))
            .collect(Collectors.toList());
        
        analytics.put("departmentSuccessRates", departmentSuccessRates);
        
        // Company-wise success rate
        Map<String, Long> applicationsByCompany = applications.stream()
            .filter(app -> app.getJobPost() != null && app.getJobPost().getCompany() != null)
            .collect(Collectors.groupingBy(
                app -> app.getJobPost().getCompany().getName(),
                Collectors.counting()
            ));
        
        Map<String, Long> successfulApplicationsByCompany = placements.stream()
            .filter(p -> p.getApplication() != null && 
                    p.getApplication().getJobPost() != null &&
                    p.getApplication().getJobPost().getCompany() != null)
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getJobPost().getCompany().getName(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> companySuccessRates = applicationsByCompany.entrySet().stream()
            .filter(entry -> entry.getValue() >= 5) // Only companies with 5+ applications
            .map(entry -> {
                String company = entry.getKey();
                long totalApps = entry.getValue();
                long successfulApps = successfulApplicationsByCompany.getOrDefault(company, 0L);
                double successRate = totalApps > 0 ? ((double) successfulApps / totalApps) * 100 : 0;
                
                Map<String, Object> companyData = new HashMap<>();
                companyData.put("company", company);
                companyData.put("totalApplications", totalApps);
                companyData.put("successfulApplications", successfulApps);
                companyData.put("successRate", Math.round(successRate * 100.0) / 100.0);
                
                return companyData;
            })
            .sorted((a, b) -> Double.compare((Double) b.get("successRate"), (Double) a.get("successRate")))
            .collect(Collectors.toList());
        
        analytics.put("companySuccessRates", companySuccessRates);
        
        return analytics;
    }

    public Map<String, Object> getJobTypeAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        List<com.example.TPO.DBMS.JobPost.JobPost> jobPosts = jobPostRepository.findAll();
        
        // Job type distribution
        Map<String, Long> jobTypeDistribution = jobPosts.stream()
            .filter(jp -> jp.getJobType() != null && !jp.getJobType().trim().isEmpty())
            .collect(Collectors.groupingBy(
                jp -> jp.getJobType().trim(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> jobTypeData = jobTypeDistribution.entrySet().stream()
            .map(entry -> {
                Map<String, Object> typeData = new HashMap<>();
                typeData.put("jobType", entry.getKey());
                typeData.put("count", entry.getValue());
                
                // Calculate average package for this job type
                double avgPackage = jobPosts.stream()
                    .filter(jp -> jp.getJobType() != null && jp.getJobType().trim().equals(entry.getKey()))
                    .mapToDouble(jp -> jp.getPackageAmount())
                    .average().orElse(0.0);
                
                typeData.put("averagePackage", Math.round(avgPackage * 100.0) / 100.0);
                
                return typeData;
            })
            .sorted((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")))
            .collect(Collectors.toList());
        
        analytics.put("jobTypeDistribution", jobTypeData);
        
        // Placement success by job type
        List<Placement> placements = placementRepository.findAll();
        
        Map<String, Long> placementsByJobType = placements.stream()
            .filter(p -> p.getApplication() != null && 
                    p.getApplication().getJobPost() != null &&
                    p.getApplication().getJobPost().getJobType() != null)
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getJobPost().getJobType().trim(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> placementJobTypeData = placementsByJobType.entrySet().stream()
            .map(entry -> {
                Map<String, Object> typeData = new HashMap<>();
                typeData.put("jobType", entry.getKey());
                typeData.put("placementCount", entry.getValue());
                
                double avgPlacementPackage = placements.stream()
                    .filter(p -> p.getApplication() != null && 
                            p.getApplication().getJobPost() != null &&
                            p.getApplication().getJobPost().getJobType() != null &&
                            p.getApplication().getJobPost().getJobType().trim().equals(entry.getKey()))
                    .mapToDouble(Placement::getPlaced_package)
                    .average().orElse(0.0);
                
                typeData.put("averagePlacementPackage", Math.round(avgPlacementPackage * 100.0) / 100.0);
                
                return typeData;
            })
            .sorted((a, b) -> Long.compare((Long) b.get("placementCount"), (Long) a.get("placementCount")))
            .collect(Collectors.toList());
        
        analytics.put("placementsByJobType", placementJobTypeData);
        
        return analytics;
    }

    public Map<String, Object> getAcademicPerformanceCorrelation() {
        Map<String, Object> analytics = new HashMap<>();
        
        List<Placement> placements = placementRepository.findAll();
        
        // Correlation between academic performance and placement package
        List<Map<String, Object>> correlationData = placements.stream()
            .filter(p -> p.getApplication() != null && p.getApplication().getStudent() != null)
            .map(p -> {
                Student student = p.getApplication().getStudent();
                Map<String, Object> data = new HashMap<>();
                data.put("cgpa", student.getAvgMarks());
                data.put("placementPackage", p.getPlaced_package());
                data.put("sscMarks", student.getSscMarks());
                data.put("hscMarks", student.getHscMarks());
                data.put("department", student.getDepartment());
                return data;
            })
            .collect(Collectors.toList());
        
        analytics.put("academicPerformanceData", correlationData);
        
        // CGPA ranges and their average packages
        Map<String, List<Placement>> cgpaRanges = placements.stream()
            .filter(p -> p.getApplication() != null && p.getApplication().getStudent() != null)
            .collect(Collectors.groupingBy(p -> {
                double cgpa = p.getApplication().getStudent().getAvgMarks();
                if (cgpa >= 9.0) return "9.0-10.0";
                else if (cgpa >= 8.0) return "8.0-8.9";
                else if (cgpa >= 7.0) return "7.0-7.9";
                else if (cgpa >= 6.0) return "6.0-6.9";
                else return "Below 6.0";
            }));
        
        List<Map<String, Object>> cgpaAnalysis = cgpaRanges.entrySet().stream()
            .map(entry -> {
                String range = entry.getKey();
                List<Placement> rangePlacements = entry.getValue();
                
                double avgPackage = rangePlacements.stream()
                    .mapToDouble(Placement::getPlaced_package)
                    .average().orElse(0.0);
                
                double maxPackage = rangePlacements.stream()
                    .mapToDouble(Placement::getPlaced_package)
                    .max().orElse(0.0);
                
                Map<String, Object> rangeData = new HashMap<>();
                rangeData.put("cgpaRange", range);
                rangeData.put("studentCount", rangePlacements.size());
                rangeData.put("averagePackage", Math.round(avgPackage * 100.0) / 100.0);
                rangeData.put("maxPackage", maxPackage);
                
                return rangeData;
            })
            .sorted((a, b) -> {
                String rangeA = (String) a.get("cgpaRange");
                String rangeB = (String) b.get("cgpaRange");
                return rangeB.compareTo(rangeA); // Sort in descending order
            })
            .collect(Collectors.toList());
        
        analytics.put("cgpaAnalysis", cgpaAnalysis);
        
        return analytics;
    }

    // Performance Analytics - Alias for existing method
    public Map<String, Object> getPerformanceAnalytics(int months) {
        return getSalaryTrendAnalytics(months);
    }

    // Detailed Package Distribution - Alias for existing method
    public Map<String, Object> getDetailedPackageDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        
        List<Placement> placements = placementRepository.findAll();
        
        // Package ranges
        Map<String, Long> packageRanges = placements.stream()
            .collect(Collectors.groupingBy(p -> {
                double pkg = p.getPlaced_package();
                if (pkg < 3) return "0-3 LPA";
                else if (pkg < 5) return "3-5 LPA";
                else if (pkg < 8) return "5-8 LPA";
                else if (pkg < 12) return "8-12 LPA";
                else if (pkg < 20) return "12-20 LPA";
                else return "20+ LPA";
            }, Collectors.counting()));
        
        List<Map<String, Object>> ranges = new ArrayList<>();
        for (Map.Entry<String, Long> entry : packageRanges.entrySet()) {
            Map<String, Object> range = new HashMap<>();
            range.put("range", entry.getKey());
            range.put("count", entry.getValue());
            ranges.add(range);
        }
        
        distribution.put("packageRanges", ranges);
        distribution.put("totalPlacements", placements.size());
        
        // Statistics
        double[] packages = placements.stream().mapToDouble(Placement::getPlaced_package).toArray();
        if (packages.length > 0) {
            distribution.put("averagePackage", Arrays.stream(packages).average().orElse(0.0));
            distribution.put("minPackage", Arrays.stream(packages).min().orElse(0.0));
            distribution.put("maxPackage", Arrays.stream(packages).max().orElse(0.0));
            distribution.put("medianPackage", calculateMedian(packages));
        }
        
        return distribution;
    }

    // Hiring Trends - Enhanced version
    public Map<String, Object> getHiringTrends(int months) {
        Map<String, Object> trends = new HashMap<>();
        
        LocalDate startDate = LocalDate.now().minusMonths(months);
        List<Placement> recentPlacements = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && p.getPlacementDate().isAfter(startDate))
            .collect(Collectors.toList());
        
        // Company hiring trends
        Map<String, Long> companyHires = recentPlacements.stream()
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getJobPost().getCompany().getName(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> topHiringCompanies = companyHires.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .map(entry -> {
                Map<String, Object> company = new HashMap<>();
                company.put("company", entry.getKey());
                company.put("hires", entry.getValue());
                return company;
            })
            .collect(Collectors.toList());
        
        trends.put("topHiringCompanies", topHiringCompanies);
        
        // Department hiring trends
        Map<String, Long> departmentHires = recentPlacements.stream()
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getStudent().getDepartment(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> departmentTrends = departmentHires.entrySet().stream()
            .map(entry -> {
                Map<String, Object> dept = new HashMap<>();
                dept.put("department", entry.getKey());
                dept.put("hires", entry.getValue());
                return dept;
            })
            .collect(Collectors.toList());
        
        trends.put("departmentTrends", departmentTrends);
        
        return trends;
    }

    // Export Methods
    public byte[] exportStudentData(String format, String department, String status) throws IOException {
        List<Student> students = studentRepository.findAll();
        
        // Apply filters
        if (!department.isEmpty()) {
            students = students.stream()
                .filter(s -> s.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
        }
        
        if (!status.isEmpty()) {
            Set<Long> placedStudentIds = placementRepository.findAll().stream()
                .map(p -> p.getApplication().getStudent().getId())
                .collect(Collectors.toSet());
                
            if ("placed".equalsIgnoreCase(status)) {
                students = students.stream()
                    .filter(s -> placedStudentIds.contains(s.getId()))
                    .collect(Collectors.toList());
            } else if ("not_placed".equalsIgnoreCase(status)) {
                students = students.stream()
                    .filter(s -> !placedStudentIds.contains(s.getId()))
                    .collect(Collectors.toList());
            }
        }
        
        if ("excel".equalsIgnoreCase(format)) {
            return exportStudentsToExcel(students);
        } else {
            return exportStudentsToPDF(students);
        }
    }

    public byte[] exportCompanyData(String format) throws IOException {
        List<com.example.TPO.DBMS.Company.Company> companies = companiesRepository.findAll();
        
        if ("excel".equalsIgnoreCase(format)) {
            return exportCompaniesToExcel(companies);
        } else {
            return exportCompaniesToPDF(companies);
        }
    }

    private byte[] exportStudentsToExcel(List<Student> students) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");
            
            // Header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("Department");
            headerRow.createCell(4).setCellValue("Phone");
            headerRow.createCell(5).setCellValue("Avg Marks");
            headerRow.createCell(6).setCellValue("Verified");
            headerRow.createCell(7).setCellValue("Batch");
            
            // Data
            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getId());
                row.createCell(1).setCellValue(student.getFirstName() + " " + student.getLastName());
                row.createCell(2).setCellValue(student.getUser() != null ? student.getUser().getUsername() : "");
                row.createCell(3).setCellValue(student.getDepartment());
                row.createCell(4).setCellValue(student.getPhoneNumber());
                row.createCell(5).setCellValue(student.getAvgMarks());
                row.createCell(6).setCellValue(student.isResults_verified() ? "Yes" : "No");
                row.createCell(7).setCellValue(student.getAcademicyear());
            }
            
            // Auto-size columns
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] exportStudentsToPDF(List<Student> students) {
        StringBuilder content = new StringBuilder();
        content.append("Student Report\n");
        content.append("===============\n\n");
        
        for (Student student : students) {
            content.append("ID: ").append(student.getId()).append("\n");
            content.append("Name: ").append(student.getFirstName()).append(" ").append(student.getLastName()).append("\n");
            content.append("Email: ").append(student.getUser() != null ? student.getUser().getUsername() : "").append("\n");
            content.append("Department: ").append(student.getDepartment()).append("\n");
            content.append("Phone: ").append(student.getPhoneNumber()).append("\n");
            content.append("Avg Marks: ").append(student.getAvgMarks()).append("\n");
            content.append("Verified: ").append(student.isResults_verified() ? "Yes" : "No").append("\n");
            content.append("Batch: ").append(student.getAcademicyear()).append("\n");
            content.append("\n");
        }
        
        return content.toString().getBytes();
    }

    private byte[] exportCompaniesToExcel(List<com.example.TPO.DBMS.Company.Company> companies) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Companies");
            
            // Header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Industry");
            headerRow.createCell(3).setCellValue("HR Name");
            headerRow.createCell(4).setCellValue("HR Contact");
            headerRow.createCell(5).setCellValue("Location");
            headerRow.createCell(6).setCellValue("Website");
            headerRow.createCell(7).setCellValue("Is MNC");
            
            // Data
            int rowNum = 1;
            for (com.example.TPO.DBMS.Company.Company company : companies) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(company.getId());
                row.createCell(1).setCellValue(company.getName());
                row.createCell(2).setCellValue(company.getIndustryType());
                row.createCell(3).setCellValue(company.getHr_Name());
                row.createCell(4).setCellValue(company.getContactNumber());
                row.createCell(5).setCellValue(company.getLocation());
                row.createCell(6).setCellValue(company.getWebsite());
                row.createCell(7).setCellValue(company.isMnc() ? "Yes" : "No");
            }
            
            // Auto-size columns
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] exportCompaniesToPDF(List<com.example.TPO.DBMS.Company.Company> companies) {
        StringBuilder content = new StringBuilder();
        content.append("Company Report\n");
        content.append("===============\n\n");
        
        for (com.example.TPO.DBMS.Company.Company company : companies) {
            content.append("ID: ").append(company.getId()).append("\n");
            content.append("Name: ").append(company.getName()).append("\n");
            content.append("Industry: ").append(company.getIndustryType()).append("\n");
            content.append("HR Name: ").append(company.getHr_Name()).append("\n");
            content.append("HR Contact: ").append(company.getContactNumber()).append("\n");
            content.append("Location: ").append(company.getLocation()).append("\n");
            content.append("Website: ").append(company.getWebsite()).append("\n");
            content.append("Is MNC: ").append(company.isMnc() ? "Yes" : "No").append("\n");
            content.append("\n");
        }
        
        return content.toString().getBytes();
    }

    // Real-time Methods
    public Map<String, Object> getRealTimeDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Current counts
        long totalStudents = studentRepository.count();
        long totalPlacements = placementRepository.count();
        long totalCompanies = companiesRepository.count();
        long totalApplications = jobApplicationRepository.count();
        
        summary.put("totalStudents", totalStudents);
        summary.put("totalPlacements", totalPlacements);
        summary.put("totalCompanies", totalCompanies);
        summary.put("totalApplications", totalApplications);
        summary.put("placementRate", totalStudents > 0 ? (double) totalPlacements / totalStudents * 100 : 0);
        
        // Recent activity (last 24 hours)
        LocalDate yesterday = LocalDate.now().minusDays(1);
        long recentPlacements = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && p.getPlacementDate().isAfter(yesterday))
            .count();
        
        summary.put("recentPlacements", recentPlacements);
        summary.put("lastUpdated", LocalDate.now().toString());
        
        return summary;
    }

    public Map<String, Object> getRealTimeAlerts() {
        Map<String, Object> alerts = new HashMap<>();
        List<Map<String, String>> alertList = new ArrayList<>();
        
        // Low placement rate alert
        double placementRate = (double) placementRepository.count() / studentRepository.count() * 100;
        if (placementRate < 50) {
            alertList.add(Map.of(
                "type", "warning",
                "message", "Placement rate is below 50%",
                "severity", "high"
            ));
        }
        
        // New placements this week
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        long weeklyPlacements = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() != null && p.getPlacementDate().isAfter(weekAgo))
            .count();
        
        if (weeklyPlacements > 5) {
            alertList.add(Map.of(
                "type", "success",
                "message", "Great week! " + weeklyPlacements + " new placements",
                "severity", "low"
            ));
        }
        
        alerts.put("alerts", alertList);
        alerts.put("totalAlerts", alertList.size());
        
        return alerts;
    }

    // System Health
    public Map<String, Object> getSystemHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        
        // Database connectivity (simplified check)
        boolean dbHealth = true;
        try {
            studentRepository.count();
            companiesRepository.count();
            placementRepository.count();
        } catch (Exception e) {
            dbHealth = false;
        }
        
        health.put("database", dbHealth ? "healthy" : "unhealthy");
        health.put("uptime", "Available"); // Simplified
        health.put("cacheSize", dataCache.size());
        health.put("lastCacheRefresh", cacheTimestamps.isEmpty() ? "Never" : "Recently");
        
        return health;
    }

    // Data Integrity
    public Map<String, Object> checkDataIntegrity() {
        Map<String, Object> integrity = new HashMap<>();
        List<String> issues = new ArrayList<>();
        
        // Check for students without departments
        long studentsWithoutDept = studentRepository.findAll().stream()
            .filter(s -> s.getDepartment() == null || s.getDepartment().trim().isEmpty())
            .count();
        
        if (studentsWithoutDept > 0) {
            issues.add(studentsWithoutDept + " students without department information");
        }
        
        // Check for companies without HR contact
        long companiesWithoutHR = companiesRepository.findAll().stream()
            .filter(c -> c.getHr_Name() == null || c.getHr_Name().trim().isEmpty())
            .count();
        
        if (companiesWithoutHR > 0) {
            issues.add(companiesWithoutHR + " companies without HR contact");
        }
        
        // Check for placements without dates
        long placementsWithoutDates = placementRepository.findAll().stream()
            .filter(p -> p.getPlacementDate() == null)
            .count();
        
        if (placementsWithoutDates > 0) {
            issues.add(placementsWithoutDates + " placements without dates");
        }
        
        integrity.put("issues", issues);
        integrity.put("totalIssues", issues.size());
        integrity.put("status", issues.isEmpty() ? "clean" : "issues_found");
        
        return integrity;
    }

    // Custom Reports
    public Map<String, Object> generateCustomReport(Map<String, Object> params) {
        Map<String, Object> report = new HashMap<>();
        
        String reportType = (String) params.getOrDefault("type", "summary");
        
        switch (reportType) {
            case "department":
                report = generateDepartmentReport(params);
                break;
            case "company":
                report = generateCompanyReport(params);
                break;
            case "package":
                report = generatePackageReport(params);
                break;
            default:
                report = generateSummaryReport(params);
        }
        
        report.put("generatedAt", LocalDate.now().toString());
        report.put("parameters", params);
        
        return report;
    }

    private Map<String, Object> generateDepartmentReport(Map<String, Object> params) {
        Map<String, Object> report = new HashMap<>();
        
        String department = (String) params.getOrDefault("department", "");
        
        if (department.isEmpty()) {
            // All departments
            report.put("type", "all_departments");
            report.put("data", getComprehensiveDepartmentAnalytics());
        } else {
            // Specific department
            report.put("type", "specific_department");
            report.put("department", department);
            
            List<Student> deptStudents = studentRepository.findAll().stream()
                .filter(s -> s.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
            
            List<Placement> deptPlacements = placementRepository.findAll().stream()
                .filter(p -> p.getApplication().getStudent().getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
            
            Map<String, Object> deptData = new HashMap<>();
            deptData.put("totalStudents", deptStudents.size());
            deptData.put("totalPlacements", deptPlacements.size());
            deptData.put("placementRate", deptStudents.size() > 0 ? 
                (double) deptPlacements.size() / deptStudents.size() * 100 : 0);
            
            double avgPackage = deptPlacements.stream()
                .mapToDouble(Placement::getPlaced_package)
                .average().orElse(0.0);
            deptData.put("averagePackage", Math.round(avgPackage * 100.0) / 100.0);
            
            double highestPackage = deptPlacements.stream()
                .mapToDouble(Placement::getPlaced_package)
                .max().orElse(0.0);
            deptData.put("highestPackage", highestPackage);
            
            report.put("data", deptData);
        }
        
        return report;
    }

    private Map<String, Object> generateCompanyReport(Map<String, Object> params) {
        Map<String, Object> report = new HashMap<>();
        report.put("type", "company_report");
        
        List<com.example.TPO.DBMS.Company.Company> companies = companiesRepository.findAll();
        
        List<Map<String, Object>> companyStats = companies.stream()
            .map(company -> {
                Map<String, Object> stats = new HashMap<>();
                stats.put("id", company.getId());
                stats.put("name", company.getName());
                stats.put("industry", company.getIndustryType());
                
                long hires = placementRepository.findAll().stream()
                    .filter(p -> p.getApplication().getJobPost().getCompany().getId().equals(company.getId()))
                    .count();
                stats.put("totalHires", hires);
                
                double avgPackage = placementRepository.findAll().stream()
                    .filter(p -> p.getApplication().getJobPost().getCompany().getId().equals(company.getId()))
                    .mapToDouble(Placement::getPlaced_package)
                    .average().orElse(0.0);
                stats.put("averagePackage", avgPackage);
                
                return stats;
            })
            .collect(Collectors.toList());
        
        report.put("data", companyStats);
        
        return report;
    }

    private Map<String, Object> generatePackageReport(Map<String, Object> params) {
        Map<String, Object> report = new HashMap<>();
        report.put("type", "package_report");
        
        List<Placement> placements = placementRepository.findAll();
        
        Map<String, Object> packageStats = new HashMap<>();
        
        double[] packages = placements.stream()
            .mapToDouble(Placement::getPlaced_package)
            .toArray();
        
        if (packages.length > 0) {
            packageStats.put("totalPlacements", packages.length);
            packageStats.put("averagePackage", Arrays.stream(packages).average().orElse(0.0));
            packageStats.put("minPackage", Arrays.stream(packages).min().orElse(0.0));
            packageStats.put("maxPackage", Arrays.stream(packages).max().orElse(0.0));
            packageStats.put("medianPackage", calculateMedian(packages));
            
            // Package distribution
            Map<String, Long> distribution = placements.stream()
                .collect(Collectors.groupingBy(p -> {
                    double pkg = p.getPlaced_package();
                    if (pkg < 5) return "Below 5 LPA";
                    else if (pkg < 10) return "5-10 LPA";
                    else if (pkg < 15) return "10-15 LPA";
                    else return "Above 15 LPA";
                }, Collectors.counting()));
            
            packageStats.put("distribution", distribution);
        }
        
        report.put("data", packageStats);
        
        return report;
    }

    private Map<String, Object> generateSummaryReport(Map<String, Object> params) {
        return Map.of(
            "type", "summary_report",
            "data", getDashboardData()
        );
    }

    // Yearly Backup Feature Implementation
    public byte[] exportYearlyBackup(int year, String format) throws IOException {
        if ("excel".equalsIgnoreCase(format)) {
            return createYearlyBackupExcel(year);
        } else if ("pdf".equalsIgnoreCase(format)) {
            return createYearlyBackupPDF(year);
        } else {
            throw new UnsupportedOperationException("Format not supported: " + format);
        }
    }

    private byte[] createYearlyBackupExcel(int year) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // 1. Summary Sheet
            createYearlyBackupSummarySheet(workbook, year);
            
            // 2. Students Data Sheet
            createYearlyStudentsDataSheet(workbook, year);
            
            // 3. Companies Data Sheet
            createYearlyCompaniesDataSheet(workbook, year);
            
            // 4. Placements Data Sheet
            createYearlyPlacementsDataSheet(workbook, year);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createYearlyBackupPDF(int year) {
        // For now, return a simple text-based PDF content
        StringBuilder content = new StringBuilder();
        content.append("Yearly Backup Report - ").append(year).append("\n");
        content.append("===============================\n\n");
        content.append("Total Students: ").append(getYearlyStudentCount(year)).append("\n");
        content.append("Total Placements: ").append(getYearlyPlacementCount(year)).append("\n");
        content.append("Placement Rate: ").append(getYearlyPlacementRate(year)).append("%\n");
        content.append("Average Package: ").append(getYearlyAveragePackage(year)).append(" LPA\n");
        
        return content.toString().getBytes();
    }

    // Company Analytics Details
    public Map<String, Object> getCompanyAnalyticsDetails() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Get all companies
        List<com.example.TPO.DBMS.Company.Company> companies = companiesRepository.findAll();
        
        // Get all placements
        List<Placement> placements = placementRepository.findAll();
        
        // Company-wise hiring statistics
        Map<String, Long> companyHiringCount = placements.stream()
            .filter(p -> p.getApplication() != null && 
                    p.getApplication().getJobPost() != null &&
                    p.getApplication().getJobPost().getCompany() != null)
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getJobPost().getCompany().getName(), 
                Collectors.counting()));
        
        // Company-wise average package
        Map<String, Double> companyAvgPackage = placements.stream()
            .filter(p -> p.getApplication() != null && 
                    p.getApplication().getJobPost() != null &&
                    p.getApplication().getJobPost().getCompany() != null)
            .collect(Collectors.groupingBy(
                p -> p.getApplication().getJobPost().getCompany().getName(),
                Collectors.averagingDouble(Placement::getPlaced_package)));
        
        analytics.put("totalCompanies", companies.size());
        analytics.put("companyHiringCount", companyHiringCount);
        analytics.put("companyAveragePackage", companyAvgPackage);
        
        return analytics;
    }

    // Comprehensive Yearly Analytics
    public Map<String, Object> getComprehensiveYearlyAnalytics(int year) {
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic statistics
        analytics.put("year", year);
        analytics.put("totalStudents", getYearlyStudentCount(year));
        analytics.put("totalPlacements", getYearlyPlacementCount(year));
        analytics.put("placementRate", getYearlyPlacementRate(year));
        analytics.put("averagePackage", getYearlyAveragePackage(year));
        analytics.put("highestPackage", getYearlyHighestPackage(year));
        analytics.put("totalCompanies", getYearlyCompanyCount(year));
        
        // Additional analytics
        List<Placement> yearlyPlacements = getYearlyPlacements(year);
        
        // Monthly breakdown
        Map<String, Long> monthlyPlacements = yearlyPlacements.stream()
            .filter(p -> p.getPlacementDate() != null)
            .collect(Collectors.groupingBy(
                p -> p.getPlacementDate().format(DateTimeFormatter.ofPattern("MM-yyyy")),
                Collectors.counting()
            ));
        analytics.put("monthlyPlacements", monthlyPlacements);
        
        analytics.put("generatedAt", LocalDate.now().toString());
        
        return analytics;
    }

    // Helper methods for trend calculation
    private String calculateTrend(List<Double> values) {
        if (values.size() < 2) return "insufficient_data";
        
        double first = values.get(0);
        double last = values.get(values.size() - 1);
        
        if (last > first) return "increasing";
        else if (last < first) return "decreasing";
        else return "stable";
    }

    private String calculateTrendInteger(List<Integer> values) {
        if (values.size() < 2) return "insufficient_data";
        
        int first = values.get(0);
        int last = values.get(values.size() - 1);
        
        if (last > first) return "increasing";
        else if (last < first) return "decreasing";
        else return "stable";
    }

    private double calculateMedian(double[] values) {
        Arrays.sort(values);
        int n = values.length;
        if (n % 2 == 0) {
            return (values[n/2 - 1] + values[n/2]) / 2.0;
        } else {
            return values[n/2];
        }
    }

    private void createYearlyBackupSummarySheet(Workbook workbook, int year) {
        Sheet sheet = workbook.createSheet("Yearly Summary " + year);
        
        // Header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Yearly Backup Summary - " + year);
        
        // Key Metrics
        int rowNum = 2;
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Total Students Registered");
        row.createCell(1).setCellValue(getYearlyStudentCount(year));
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Total Students Placed");
        row.createCell(1).setCellValue(getYearlyPlacementCount(year));
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Overall Placement Rate (%)");
        row.createCell(1).setCellValue(getYearlyPlacementRate(year));
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Average Package (LPA)");
        row.createCell(1).setCellValue(getYearlyAveragePackage(year));
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Highest Package (LPA)");
        row.createCell(1).setCellValue(getYearlyHighestPackage(year));
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Total Companies Participated");
        row.createCell(1).setCellValue(getYearlyCompanyCount(year));
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Backup Generated On");
        row.createCell(1).setCellValue(new Date().toString());
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createYearlyStudentsDataSheet(Workbook workbook, int year) {
        Sheet sheet = workbook.createSheet("Students Data " + year);
        
        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Student ID", "First Name", "Last Name", "Email", "Phone", "Department", 
                           "Academic Year", "SSC Marks", "HSC Marks", "CGPA", "Verification Status", 
                           "Placement Status", "Package (LPA)", "Company Placed"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        // Get yearly student data
        List<Student> yearlyStudents = getYearlyStudents(year);
        int rowNum = 1;
        
        for (Student student : yearlyStudents) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getId());
            row.createCell(1).setCellValue(student.getFirstName());
            row.createCell(2).setCellValue(student.getLastName());
            row.createCell(3).setCellValue(student.getUser() != null ? student.getUser().getEmail() : "N/A");
            row.createCell(4).setCellValue(student.getPhoneNumber());
            row.createCell(5).setCellValue(student.getDepartment());
            row.createCell(6).setCellValue(student.getAcademicyear());
            row.createCell(7).setCellValue(student.getSscMarks());
            row.createCell(8).setCellValue(student.getHscMarks());
            row.createCell(9).setCellValue(student.getAvgMarks());
            row.createCell(10).setCellValue(student.isResults_verified() ? "Verified" : "Not Verified");
            
            // Get placement info
            Optional<Placement> placement = getStudentPlacement(student.getId());
            if (placement.isPresent()) {
                row.createCell(11).setCellValue("Placed");
                row.createCell(12).setCellValue(placement.get().getPlaced_package());
                row.createCell(13).setCellValue(getCompanyNameFromPlacement(placement.get()));
            } else {
                row.createCell(11).setCellValue("Not Placed");
                row.createCell(12).setCellValue("N/A");
                row.createCell(13).setCellValue("N/A");
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createYearlyCompaniesDataSheet(Workbook workbook, int year) {
        Sheet sheet = workbook.createSheet("Companies Data " + year);
        
        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Company ID", "Company Name", "Industry Type", "HR Name", "Email", 
                           "Contact Number", "Location", "Website", "MNC Status", "Total Hirings", 
                           "Associated Since"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        // Get yearly company data
        List<com.example.TPO.DBMS.Company.Company> yearlyCompanies = getYearlyCompanies(year);
        int rowNum = 1;
        
        for (com.example.TPO.DBMS.Company.Company company : yearlyCompanies) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(company.getId());
            row.createCell(1).setCellValue(company.getName());
            row.createCell(2).setCellValue(company.getIndustryType());
            row.createCell(3).setCellValue(company.getHr_Name());
            row.createCell(4).setCellValue(company.getEmail());
            row.createCell(5).setCellValue(company.getContactNumber());
            row.createCell(6).setCellValue(company.getLocation());
            row.createCell(7).setCellValue(company.getWebsite());
            row.createCell(8).setCellValue(company.isMnc() ? "MNC" : "Local");
            row.createCell(9).setCellValue(getCompanyHiringCount(company.getId(), year));
            row.createCell(10).setCellValue(company.getAssociatedSince() != null ? 
                company.getAssociatedSince().toString() : "N/A");
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createYearlyPlacementsDataSheet(Workbook workbook, int year) {
        Sheet sheet = workbook.createSheet("Placements Data " + year);
        
        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Placement ID", "Student ID", "Student Name", "Company Name", 
                           "Package (LPA)", "Placement Date", "Department", "Remarks"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        // Get yearly placement data
        List<Placement> yearlyPlacements = getYearlyPlacements(year);
        int rowNum = 1;
        
        for (Placement placement : yearlyPlacements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(placement.getId());
            row.createCell(1).setCellValue(getStudentIdFromPlacement(placement));
            row.createCell(2).setCellValue(getStudentNameFromPlacement(placement));
            row.createCell(3).setCellValue(getCompanyNameFromPlacement(placement));
            row.createCell(4).setCellValue(placement.getPlaced_package());
            row.createCell(5).setCellValue(placement.getPlacementDate() != null ? 
                placement.getPlacementDate().toString() : "N/A");
            row.createCell(6).setCellValue(getDepartmentFromPlacement(placement));
            row.createCell(7).setCellValue(placement.getRemarks());
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private Optional<Placement> getStudentPlacement(Long studentId) {
        return placementRepository.findAll().stream()
            .filter(p -> p.getApplication() != null && 
                    p.getApplication().getStudent() != null && 
                    p.getApplication().getStudent().getId().equals(studentId))
            .findFirst();
    }

    private String getCompanyNameFromPlacement(Placement placement) {
        if (placement.getApplication() != null && 
            placement.getApplication().getJobPost() != null && 
            placement.getApplication().getJobPost().getCompany() != null) {
            return placement.getApplication().getJobPost().getCompany().getName();
        }
        return "N/A";
    }

    private Long getStudentIdFromPlacement(Placement placement) {
        if (placement.getApplication() != null && 
            placement.getApplication().getStudent() != null) {
            return placement.getApplication().getStudent().getId();
        }
        return 0L;
    }

    private String getStudentNameFromPlacement(Placement placement) {
        if (placement.getApplication() != null && 
            placement.getApplication().getStudent() != null) {
            Student student = placement.getApplication().getStudent();
            return student.getFirstName() + " " + student.getLastName();
        }
        return "N/A";
    }

    private String getDepartmentFromPlacement(Placement placement) {
        if (placement.getApplication() != null && 
            placement.getApplication().getStudent() != null) {
            return placement.getApplication().getStudent().getDepartment();
        }
        return "N/A";
    }

    private int getCompanyHiringCount(Long companyId, int year) {
        return (int) placementRepository.findAll().stream()
            .filter(p -> p.getApplication() != null && 
                    p.getApplication().getJobPost() != null &&
                    p.getApplication().getJobPost().getCompany() != null &&
                    p.getApplication().getJobPost().getCompany().getId().equals(companyId) &&
                    p.getPlacementDate() != null && 
                    p.getPlacementDate().getYear() == year )
            .count();
    }
}
