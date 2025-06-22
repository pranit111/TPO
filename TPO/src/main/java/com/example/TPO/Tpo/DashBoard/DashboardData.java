package com.example.TPO.Tpo.DashBoard;

import java.util.List;
import java.time.LocalDate;

public class DashboardData {
    private StudentData studentData;
    private CompaniesData companiesData;
    private InsightsData insightsData;
    private DepartmentsData departmentsData;
    private List<TopStudent> topStudents;
    
    // New enhanced data structures
    private PlacementTrends placementTrends;
    private List<DepartmentStats> departmentStats;
    private List<MonthlyPlacement> monthlyPlacements;
    private ProcessMetrics processMetrics;
    private List<PackageDistribution> packageDistribution;
    private RecentActivities recentActivities;
    private CompanyAnalytics companyAnalytics;

    // Getters and Setters
    public StudentData getStudentData() {
        return studentData;
    }

    public void setStudentData(StudentData studentData) {
        this.studentData = studentData;
    }

    public CompaniesData getCompaniesData() {
        return companiesData;
    }

    public void setCompaniesData(CompaniesData companiesData) {
        this.companiesData = companiesData;
    }

    public InsightsData getInsightsData() {
        return insightsData;
    }

    public void setInsightsData(InsightsData insightsData) {
        this.insightsData = insightsData;
    }

    public DepartmentsData getDepartmentsData() {
        return departmentsData;
    }

    public void setDepartmentsData(DepartmentsData departmentsData) {
        this.departmentsData = departmentsData;
    }

    public List<TopStudent> getTopStudents() {
        return topStudents;
    }

    public void setTopStudents(List<TopStudent> topStudents) {
        this.topStudents = topStudents;
    }

    public PlacementTrends getPlacementTrends() {
        return placementTrends;
    }

    public void setPlacementTrends(PlacementTrends placementTrends) {
        this.placementTrends = placementTrends;
    }

    public List<DepartmentStats> getDepartmentStats() {
        return departmentStats;
    }

    public void setDepartmentStats(List<DepartmentStats> departmentStats) {
        this.departmentStats = departmentStats;
    }

    public List<MonthlyPlacement> getMonthlyPlacements() {
        return monthlyPlacements;
    }

    public void setMonthlyPlacements(List<MonthlyPlacement> monthlyPlacements) {
        this.monthlyPlacements = monthlyPlacements;
    }

    public ProcessMetrics getProcessMetrics() {
        return processMetrics;
    }

    public void setProcessMetrics(ProcessMetrics processMetrics) {
        this.processMetrics = processMetrics;
    }

    public List<PackageDistribution> getPackageDistribution() {
        return packageDistribution;
    }

    public void setPackageDistribution(List<PackageDistribution> packageDistribution) {
        this.packageDistribution = packageDistribution;
    }

    public RecentActivities getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(RecentActivities recentActivities) {
        this.recentActivities = recentActivities;
    }

    public CompanyAnalytics getCompanyAnalytics() {
        return companyAnalytics;
    }

    public void setCompanyAnalytics(CompanyAnalytics companyAnalytics) {
        this.companyAnalytics = companyAnalytics;
    }

    public static class StudentData {
        private int total;
        private int placed;
        private int remaining;
        private double placementRate;
        private double averagePackage;

        // Getters and Setters
        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPlaced() {
            return placed;
        }

        public void setPlaced(int placed) {
            this.placed = placed;
        }

        public int getRemaining() {
            return remaining;
        }

        public void setRemaining(int remaining) {
            this.remaining = remaining;
        }

        public double getPlacementRate() {
            return placementRate;
        }

        public void setPlacementRate(double placementRate) {
            this.placementRate = placementRate;
        }

        public double getAveragePackage() {
            return averagePackage;
        }

        public void setAveragePackage(double averagePackage) {
            this.averagePackage = averagePackage;
        }
    }

    public static class CompaniesData {
        private int total;
        private int totalOpenings;
        private int remaining;
        private List<Company> companies;

        // Getters and Setters
        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalOpenings() {
            return totalOpenings;
        }

        public void setTotalOpenings(int totalOpenings) {
            this.totalOpenings = totalOpenings;
        }

        public int getRemaining() {
            return remaining;
        }

        public void setRemaining(int remaining) {
            this.remaining = remaining;
        }

        public List<Company> getCompanies() {
            return companies;
        }

        public void setCompanies(List<Company> companies) {
            this.companies = companies;
        }
    }

    public static class Company {


        private String name;
        private int openings;

        public Company(String name, int openings) {
            this.name = name;
            this.openings = openings;
        }

        public Company() {

        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getOpenings() {
            return openings;
        }

        public void setOpenings(int openings) {
            this.openings = openings;
        }
    }

    public static class InsightsData {
        private String highestPackage;
        private String averagePackage;
        private String topHiringCompany;
        private String topDepartment;

        // Getters and Setters
        public String getHighestPackage() {
            return highestPackage;
        }

        public void setHighestPackage(String highestPackage) {
            this.highestPackage = highestPackage;
        }

        public String getAveragePackage() {
            return averagePackage;
        }

        public void setAveragePackage(String averagePackage) {
            this.averagePackage = averagePackage;
        }

        public String getTopHiringCompany() {
            return topHiringCompany;
        }

        public void setTopHiringCompany(String topHiringCompany) {
            this.topHiringCompany = topHiringCompany;
        }

        public String getTopDepartment() {
            return topDepartment;
        }

        public void setTopDepartment(String topDepartment) {
            this.topDepartment = topDepartment;
        }
    }

    public static class DepartmentsData {
        private int totalDepartments;
        private int eligibleStudents;
        private int remaining;

        // Getters and Setters
        public int getTotalDepartments() {
            return totalDepartments;
        }

        public void setTotalDepartments(int totalDepartments) {
            this.totalDepartments = totalDepartments;
        }

        public int getEligibleStudents() {
            return eligibleStudents;
        }

        public void setEligibleStudents(int eligibleStudents) {
            this.eligibleStudents = eligibleStudents;
        }

        public int getRemaining() {
            return remaining;
        }

        public void setRemaining(int remaining) {
            this.remaining = remaining;
        }
    }

    public static class TopStudent {
        private int rank;
        private String name;
        private String department;
        private String packageAmount;
        private String company;

        public TopStudent() {
        }

        public TopStudent(int rank, String name, String department, String packageAmount, String company) {
            this.rank = rank;
            this.name = name;
            this.department = department;
            this.packageAmount = packageAmount;
            this.company = company;
        }

        // Getters and Setters
        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getPackageAmount() {
            return packageAmount;
        }

        public void setPackageAmount(String packageAmount) {
            this.packageAmount = packageAmount;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }
    }

    // New comprehensive data structures for enhanced dashboard analytics
    
    public static class PlacementTrends {
        private List<String> dates;
        private List<Integer> placedCounts;
        private List<Integer> unplacedCounts;
        private List<Double> averagePackages;
        private String trend; // "INCREASING", "DECREASING", "STABLE"

        // Getters and Setters
        public List<String> getDates() {
            return dates;
        }

        public void setDates(List<String> dates) {
            this.dates = dates;
        }

        public List<Integer> getPlacedCounts() {
            return placedCounts;
        }

        public void setPlacedCounts(List<Integer> placedCounts) {
            this.placedCounts = placedCounts;
        }

        public List<Integer> getUnplacedCounts() {
            return unplacedCounts;
        }

        public void setUnplacedCounts(List<Integer> unplacedCounts) {
            this.unplacedCounts = unplacedCounts;
        }

        public List<Double> getAveragePackages() {
            return averagePackages;
        }

        public void setAveragePackages(List<Double> averagePackages) {
            this.averagePackages = averagePackages;
        }

        public String getTrend() {
            return trend;
        }

        public void setTrend(String trend) {
            this.trend = trend;
        }
    }

    public static class DepartmentStats {
        private String departmentName;
        private int totalStudents;
        private int placedStudents;
        private int unplacedStudents;
        private double averagePackage;
        private double placementPercentage;
        private String topCompany;
        private double highestPackage;

        // Getters and Setters
        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public int getTotalStudents() {
            return totalStudents;
        }

        public void setTotalStudents(int totalStudents) {
            this.totalStudents = totalStudents;
        }

        public int getPlacedStudents() {
            return placedStudents;
        }

        public void setPlacedStudents(int placedStudents) {
            this.placedStudents = placedStudents;
        }

        public int getUnplacedStudents() {
            return unplacedStudents;
        }

        public void setUnplacedStudents(int unplacedStudents) {
            this.unplacedStudents = unplacedStudents;
        }

        public double getAveragePackage() {
            return averagePackage;
        }

        public void setAveragePackage(double averagePackage) {
            this.averagePackage = averagePackage;
        }

        public double getPlacementPercentage() {
            return placementPercentage;
        }

        public void setPlacementPercentage(double placementPercentage) {
            this.placementPercentage = placementPercentage;
        }

        public String getTopCompany() {
            return topCompany;
        }

        public void setTopCompany(String topCompany) {
            this.topCompany = topCompany;
        }

        public double getHighestPackage() {
            return highestPackage;
        }

        public void setHighestPackage(double highestPackage) {
            this.highestPackage = highestPackage;
        }
    }

    public static class MonthlyPlacement {
        private String month;
        private int totalPlaced;
        private int totalUnplaced;
        private double averagePackage;
        private int newCompanies;
        private int totalApplications;

        // Getters and Setters
        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public int getTotalPlaced() {
            return totalPlaced;
        }

        public void setTotalPlaced(int totalPlaced) {
            this.totalPlaced = totalPlaced;
        }

        public int getTotalUnplaced() {
            return totalUnplaced;
        }

        public void setTotalUnplaced(int totalUnplaced) {
            this.totalUnplaced = totalUnplaced;
        }

        public double getAveragePackage() {
            return averagePackage;
        }

        public void setAveragePackage(double averagePackage) {
            this.averagePackage = averagePackage;
        }

        public int getNewCompanies() {
            return newCompanies;
        }

        public void setNewCompanies(int newCompanies) {
            this.newCompanies = newCompanies;
        }

        public int getTotalApplications() {
            return totalApplications;
        }

        public void setTotalApplications(int totalApplications) {
            this.totalApplications = totalApplications;
        }
    }

    public static class ProcessMetrics {
        private int totalInterviews;
        private int totalOffers;
        private int totalAcceptances;
        private int pendingApplications;
        private int upcomingInterviews;
        private int documentsVerified;
        private int documentsPending;
        private double averageInterviewScore;
        private double offerAcceptanceRate;
        private int activeProcesses;

        // Getters and Setters
        public int getTotalInterviews() {
            return totalInterviews;
        }

        public void setTotalInterviews(int totalInterviews) {
            this.totalInterviews = totalInterviews;
        }

        public int getTotalOffers() {
            return totalOffers;
        }

        public void setTotalOffers(int totalOffers) {
            this.totalOffers = totalOffers;
        }

        public int getTotalAcceptances() {
            return totalAcceptances;
        }

        public void setTotalAcceptances(int totalAcceptances) {
            this.totalAcceptances = totalAcceptances;
        }

        public int getPendingApplications() {
            return pendingApplications;
        }

        public void setPendingApplications(int pendingApplications) {
            this.pendingApplications = pendingApplications;
        }

        public int getUpcomingInterviews() {
            return upcomingInterviews;
        }

        public void setUpcomingInterviews(int upcomingInterviews) {
            this.upcomingInterviews = upcomingInterviews;
        }

        public int getDocumentsVerified() {
            return documentsVerified;
        }

        public void setDocumentsVerified(int documentsVerified) {
            this.documentsVerified = documentsVerified;
        }

        public int getDocumentsPending() {
            return documentsPending;
        }

        public void setDocumentsPending(int documentsPending) {
            this.documentsPending = documentsPending;
        }

        public double getAverageInterviewScore() {
            return averageInterviewScore;
        }

        public void setAverageInterviewScore(double averageInterviewScore) {
            this.averageInterviewScore = averageInterviewScore;
        }

        public double getOfferAcceptanceRate() {
            return offerAcceptanceRate;
        }

        public void setOfferAcceptanceRate(double offerAcceptanceRate) {
            this.offerAcceptanceRate = offerAcceptanceRate;
        }

        public int getActiveProcesses() {
            return activeProcesses;
        }

        public void setActiveProcesses(int activeProcesses) {
            this.activeProcesses = activeProcesses;
        }
    }

    public static class PackageDistribution {
        private String packageRange; // e.g., "3-5 LPA", "5-10 LPA"
        private int studentCount;
        private double percentage;

        public PackageDistribution() {}

        public PackageDistribution(String packageRange, int studentCount, double percentage) {
            this.packageRange = packageRange;
            this.studentCount = studentCount;
            this.percentage = percentage;
        }

        // Getters and Setters
        public String getPackageRange() {
            return packageRange;
        }

        public void setPackageRange(String packageRange) {
            this.packageRange = packageRange;
        }

        public int getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(int studentCount) {
            this.studentCount = studentCount;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }
    }

    public static class RecentActivities {
        private List<Activity> activities;
        private int totalCount;

        // Getters and Setters
        public List<Activity> getActivities() {
            return activities;
        }

        public void setActivities(List<Activity> activities) {
            this.activities = activities;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public static class Activity {
            private String type; // "PLACEMENT", "INTERVIEW", "APPLICATION", "COMPANY_VISIT"
            private String description;
            private LocalDate date;
            private String status; // "COMPLETED", "PENDING", "CANCELLED"
            private String studentName;
            private String companyName;

            public Activity() {}

            public Activity(String type, String description, LocalDate date, String status) {
                this.type = type;
                this.description = description;
                this.date = date;
                this.status = status;
            }

            // Getters and Setters
            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public LocalDate getDate() {
                return date;
            }

            public void setDate(LocalDate date) {
                this.date = date;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStudentName() {
                return studentName;
            }

            public void setStudentName(String studentName) {
                this.studentName = studentName;
            }

            public String getCompanyName() {
                return companyName;
            }

            public void setCompanyName(String companyName) {
                this.companyName = companyName;
            }
        }
    }

    public static class CompanyAnalytics {
        private int totalCompanies;
        private int activeCompanies;
        private int newCompaniesThisYear;
        private List<CompanyPerformance> topPerformingCompanies;
        private List<CompanyCategory> companiesByCategory;
        private double averagePackageOffered;

        // Getters and Setters
        public int getTotalCompanies() {
            return totalCompanies;
        }

        public void setTotalCompanies(int totalCompanies) {
            this.totalCompanies = totalCompanies;
        }

        public int getActiveCompanies() {
            return activeCompanies;
        }

        public void setActiveCompanies(int activeCompanies) {
            this.activeCompanies = activeCompanies;
        }

        public int getNewCompaniesThisYear() {
            return newCompaniesThisYear;
        }

        public void setNewCompaniesThisYear(int newCompaniesThisYear) {
            this.newCompaniesThisYear = newCompaniesThisYear;
        }

        public List<CompanyPerformance> getTopPerformingCompanies() {
            return topPerformingCompanies;
        }

        public void setTopPerformingCompanies(List<CompanyPerformance> topPerformingCompanies) {
            this.topPerformingCompanies = topPerformingCompanies;
        }

        public List<CompanyCategory> getCompaniesByCategory() {
            return companiesByCategory;
        }

        public void setCompaniesByCategory(List<CompanyCategory> companiesByCategory) {
            this.companiesByCategory = companiesByCategory;
        }

        public double getAveragePackageOffered() {
            return averagePackageOffered;
        }

        public void setAveragePackageOffered(double averagePackageOffered) {
            this.averagePackageOffered = averagePackageOffered;
        }

        public static class CompanyPerformance {
            private String companyName;
            private int totalHires;
            private double averagePackage;
            private double hiringRate;
            private int visitFrequency;

            // Getters and Setters
            public String getCompanyName() {
                return companyName;
            }

            public void setCompanyName(String companyName) {
                this.companyName = companyName;
            }

            public int getTotalHires() {
                return totalHires;
            }

            public void setTotalHires(int totalHires) {
                this.totalHires = totalHires;
            }

            public double getAveragePackage() {
                return averagePackage;
            }

            public void setAveragePackage(double averagePackage) {
                this.averagePackage = averagePackage;
            }

            public double getHiringRate() {
                return hiringRate;
            }

            public void setHiringRate(double hiringRate) {
                this.hiringRate = hiringRate;
            }

            public int getVisitFrequency() {
                return visitFrequency;
            }

            public void setVisitFrequency(int visitFrequency) {
                this.visitFrequency = visitFrequency;
            }
        }

        public static class CompanyCategory {
            private String category; // "IT", "CONSULTING", "FINANCE", "MANUFACTURING", etc.
            private int companyCount;
            private int totalOpenings;
            private double averagePackage;

            // Getters and Setters
            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public int getCompanyCount() {
                return companyCount;
            }

            public void setCompanyCount(int companyCount) {
                this.companyCount = companyCount;
            }

            public int getTotalOpenings() {
                return totalOpenings;
            }

            public void setTotalOpenings(int totalOpenings) {
                this.totalOpenings = totalOpenings;
            }

            public double getAveragePackage() {
                return averagePackage;
            }

            public void setAveragePackage(double averagePackage) {
                this.averagePackage = averagePackage;
            }
        }
    }
}
