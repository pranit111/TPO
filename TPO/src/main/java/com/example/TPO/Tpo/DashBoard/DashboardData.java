package com.example.TPO.Tpo.DashBoard;

import java.util.List;

public class DashboardData {
    private StudentData studentData;
    private CompaniesData companiesData;
    private InsightsData insightsData;
    private DepartmentsData departmentsData;
    private List<TopStudent> topStudents;

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
}
