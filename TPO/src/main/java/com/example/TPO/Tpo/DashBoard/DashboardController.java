package com.example.TPO.Tpo.DashBoard;

import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.DBMS.Tpo.TPO_Role;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api7/dashboard")
public class DashboardController {
    @Autowired
    TpoRepository tpoRepository;
    @Autowired
    UserRepo userRepo;
    @Autowired
    JWTService jwtService;
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardData getDashboardData(@RequestHeader("Authorization") String authheader) {
        String authToken="";
        if (authheader  != null && authheader.startsWith("Bearer ")) {
            authToken = authheader.substring(7);}
        User dbUser=userRepo.findById(jwtService.extractUserId(authToken)).get();
        Optional<TPOUser> tpoUser=tpoRepository.findByUser(dbUser);
        TPOUser tpoUser1=tpoUser.get();
        if(tpoUser1.getRole()== TPO_Role.ADMIN){
        return dashboardService.getDashboardData();}
        DashboardData dash= new  DashboardData();
        return dash;
    }

    // Enhanced endpoints for real-time dashboard functionality
    
    @GetMapping("/stats/real-time")
    public ResponseEntity<Map<String, Object>> getRealTimeStats(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> realTimeStats = dashboardService.getRealTimeStatistics();
        return ResponseEntity.ok(realTimeStats);
    }

    @GetMapping("/analytics/growth")
    public ResponseEntity<Map<String, Object>> getGrowthAnalytics(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "6") int months) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> growthData = dashboardService.getGrowthAnalytics(months);
        return ResponseEntity.ok(growthData);
    }

    @GetMapping("/activities/recent")
    public ResponseEntity<DashboardData.RecentActivities> getRecentActivities(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "10") int limit) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        DashboardData.RecentActivities activities = dashboardService.getRecentActivitiesWithLimit(limit);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/metrics/process")
    public ResponseEntity<DashboardData.ProcessMetrics> getProcessMetrics(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        DashboardData.ProcessMetrics metrics = dashboardService.getDetailedProcessMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/analytics/departments")
    public ResponseEntity<Map<String, Object>> getDepartmentAnalytics(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> departmentData = dashboardService.getComprehensiveDepartmentAnalytics();
        return ResponseEntity.ok(departmentData);
    }

    @GetMapping("/analytics/companies")
    public ResponseEntity<DashboardData.CompanyAnalytics> getCompanyAnalytics(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        DashboardData.CompanyAnalytics analytics = dashboardService.getDetailedCompanyAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/trends/placement")
    public ResponseEntity<DashboardData.PlacementTrends> getPlacementTrends(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "12") int months) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        DashboardData.PlacementTrends trends = dashboardService.getPlacementTrendsForPeriod(months);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/export/dashboard-data")
    public ResponseEntity<byte[]> exportDashboardData(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "excel") String format) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            byte[] exportData = dashboardService.exportDashboardData(format);
            String filename = "dashboard_report." + (format.equals("pdf") ? "pdf" : "xlsx");
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", format.equals("pdf") ? 
                    "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(exportData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getDashboardNotifications(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> notifications = dashboardService.getDashboardNotifications();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshDashboardData(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        dashboardService.refreshCachedData();
        return ResponseEntity.ok(Map.of("message", "Dashboard data refreshed successfully"));
    }

    // Student Management Endpoints
    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> getAllStudents(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String department,
            @RequestParam(defaultValue = "") String status) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> studentsData = dashboardService.getStudentsWithFilters(page, size, search, department, status);
        return ResponseEntity.ok(studentsData);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Map<String, Object>> getStudentById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> student = dashboardService.getStudentDetails(id);
        return ResponseEntity.ok(student);
    }

    // Company Management Endpoints
    @GetMapping("/companies")
    public ResponseEntity<Map<String, Object>> getAllCompanies(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String industry) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> companiesData = dashboardService.getCompaniesWithFilters(page, size, search, industry);
        return ResponseEntity.ok(companiesData);
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Map<String, Object>> getCompanyById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> company = dashboardService.getCompanyDetails(id);
        return ResponseEntity.ok(company);
    }

    // Activity Logs Management
    @GetMapping("/logs")
    public ResponseEntity<Map<String, Object>> getActivityLogs(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String action,
            @RequestParam(defaultValue = "") String entity,
            @RequestParam(defaultValue = "") String dateFrom,
            @RequestParam(defaultValue = "") String dateTo) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> logsData = dashboardService.getActivityLogsWithFilters(page, size, action, entity, dateFrom, dateTo);
        return ResponseEntity.ok(logsData);
    }

    // Advanced Analytics Endpoints
    @GetMapping("/analytics/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceAnalytics(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "12") int months) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> performanceData = dashboardService.getPerformanceAnalytics(months);
        return ResponseEntity.ok(performanceData);
    }

    @GetMapping("/analytics/package-distribution")
    public ResponseEntity<Map<String, Object>> getPackageDistribution(
            @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> packageData = dashboardService.getDetailedPackageDistribution();
        return ResponseEntity.ok(packageData);
    }

    @GetMapping("/analytics/hiring-trends")
    public ResponseEntity<Map<String, Object>> getHiringTrends(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "12") int months) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> hiringTrends = dashboardService.getHiringTrends(months);
        return ResponseEntity.ok(hiringTrends);
    }

    // Data Export Endpoints
    @GetMapping("/export/students")
    public ResponseEntity<byte[]> exportStudentData(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "excel") String format,
            @RequestParam(defaultValue = "") String department,
            @RequestParam(defaultValue = "") String status) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            byte[] exportData = dashboardService.exportStudentData(format, department, status);
            String filename = "students_report." + (format.equals("pdf") ? "pdf" : "xlsx");
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", format.equals("pdf") ? 
                    "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(exportData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/companies")
    public ResponseEntity<byte[]> exportCompanyData(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "excel") String format) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            byte[] exportData = dashboardService.exportCompanyData(format);
            String filename = "companies_report." + (format.equals("pdf") ? "pdf" : "xlsx");
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", format.equals("pdf") ? 
                    "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(exportData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Real-time Data Endpoints
    @GetMapping("/realtime/dashboard-summary")
    public ResponseEntity<Map<String, Object>> getRealTimeDashboardSummary(
            @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> summary = dashboardService.getRealTimeDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/realtime/alerts")
    public ResponseEntity<Map<String, Object>> getRealTimeAlerts(
            @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> alerts = dashboardService.getRealTimeAlerts();
        return ResponseEntity.ok(alerts);
    }

    // System Status and Health
    @GetMapping("/system/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth(
            @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> healthData = dashboardService.getSystemHealthStatus();
        return ResponseEntity.ok(healthData);
    }

    // Data Validation and Integrity
    @GetMapping("/validation/data-integrity")
    public ResponseEntity<Map<String, Object>> checkDataIntegrity(
            @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> integrityReport = dashboardService.checkDataIntegrity();
        return ResponseEntity.ok(integrityReport);
    }

    // Custom Reports
    @PostMapping("/reports/custom")
    public ResponseEntity<Map<String, Object>> generateCustomReport(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> reportParams) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        Map<String, Object> reportData = dashboardService.generateCustomReport(reportParams);
        return ResponseEntity.ok(reportData);
    }

    // Helper method for authorization check
    private boolean isAuthorizedAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        
        try {
            String authToken = authHeader.substring(7);
            User dbUser = userRepo.findById(jwtService.extractUserId(authToken)).orElse(null);
            if (dbUser == null) return false;
            
            Optional<TPOUser> tpoUser = tpoRepository.findByUser(dbUser);
            return tpoUser.isPresent() && tpoUser.get().getRole() == TPO_Role.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }
}
