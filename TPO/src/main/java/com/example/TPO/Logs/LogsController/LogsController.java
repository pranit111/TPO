package com.example.TPO.Logs.LogsController;

import com.example.TPO.DBMS.Logs.Logs;
import com.example.TPO.Logs.LogsService.LogsService;
import com.example.TPO.UserManagement.Service.JWTService;
import com.example.TPO.UserManagement.UserRepo.UserRepo;
import com.example.TPO.UserManagement.entity.User;
import com.example.TPO.DBMS.Tpo.TPOUser;
import com.example.TPO.DBMS.Tpo.TPO_Role;
import com.example.TPO.Tpo.TpoRepository.TpoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("api8/")
@RestController
public class LogsController {

    @Autowired
    LogsService logsService;
    
    @Autowired
    JWTService jwtService;
    
    @Autowired
    UserRepo userRepo;
    
    @Autowired
    TpoRepository tpoRepository;

    // Helper method to check if user is authorized (TPO Admin only)
    private boolean isAuthorizedAdmin(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return false;
            }
            String token = authHeader.substring(7);
            User dbUser = userRepo.findById(jwtService.extractUserId(token)).orElse(null);
            if (dbUser == null) return false;
            
            Optional<TPOUser> tpoUser = tpoRepository.findByUser(dbUser);
            return tpoUser.isPresent() && tpoUser.get().getRole() == TPO_Role.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }

    // Get all logs (with pagination and authorization)
    @GetMapping
    public ResponseEntity<?> getAllLogs(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access - TPO Admin required"));
        }
        
        try {
            Page<Logs> logs = logsService.getAllLogsPaginated(page, size, sortBy, sortDirection);
            
            Map<String, Object> response = new HashMap<>();
            response.put("logs", logs.getContent());
            response.put("totalElements", logs.getTotalElements());
            response.put("totalPages", logs.getTotalPages());
            response.put("currentPage", logs.getNumber());
            response.put("pageSize", logs.getSize());
            response.put("hasNext", logs.hasNext());
            response.put("hasPrevious", logs.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch logs: " + e.getMessage()));
        }
    }

    // Advanced search with multiple filters and pagination
    @GetMapping("/search")
    public ResponseEntity<?> searchLogs(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String performedBy,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String details,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access - TPO Admin required"));
        }
        
        try {
            LocalDateTime fromDateTime = null;
            LocalDateTime toDateTime = null;
            
            if (dateFrom != null && !dateFrom.isEmpty()) {
                fromDateTime = LocalDate.parse(dateFrom, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            }
            if (dateTo != null && !dateTo.isEmpty()) {
                toDateTime = LocalDate.parse(dateTo, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);
            }
            
            Page<Logs> logs = logsService.searchLogsAdvanced(
                action, performedBy, entityName, entityId, details,
                fromDateTime, toDateTime, page, size, sortBy, sortDirection
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("logs", logs.getContent());
            response.put("totalElements", logs.getTotalElements());
            response.put("totalPages", logs.getTotalPages());
            response.put("currentPage", logs.getNumber());
            response.put("pageSize", logs.getSize());
            response.put("hasNext", logs.hasNext());
            response.put("hasPrevious", logs.hasPrevious());
            
            // Add search summary
            Map<String, Object> searchSummary = new HashMap<>();
            searchSummary.put("action", action);
            searchSummary.put("performedBy", performedBy);
            searchSummary.put("entityName", entityName);
            searchSummary.put("entityId", entityId);
            searchSummary.put("details", details);
            searchSummary.put("dateFrom", dateFrom);
            searchSummary.put("dateTo", dateTo);
            response.put("searchCriteria", searchSummary);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }

    // Filter logs by action type
    @GetMapping("/filter/action")
    public ResponseEntity<?> filterByAction(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            Page<Logs> logs = logsService.filterByAction(action, page, size);
            return ResponseEntity.ok(createPaginatedResponse(logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Filter failed: " + e.getMessage()));
        }
    }

    // Filter logs by entity type
    @GetMapping("/filter/entity")
    public ResponseEntity<?> filterByEntity(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String entityName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            Page<Logs> logs = logsService.filterByEntity(entityName, page, size);
            return ResponseEntity.ok(createPaginatedResponse(logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Filter failed: " + e.getMessage()));
        }
    }

    // Filter logs by user
    @GetMapping("/filter/user")
    public ResponseEntity<?> filterByUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            Page<Logs> logs = logsService.filterByUser(performedBy, page, size);
            return ResponseEntity.ok(createPaginatedResponse(logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Filter failed: " + e.getMessage()));
        }
    }

    // Filter logs by date range
    @GetMapping("/filter/daterange")
    public ResponseEntity<?> filterByDateRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            LocalDateTime fromDateTime = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            LocalDateTime toDateTime = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);
            
            Page<Logs> logs = logsService.filterByDateRange(fromDateTime, toDateTime, page, size);
            return ResponseEntity.ok(createPaginatedResponse(logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Date filter failed: " + e.getMessage()));
        }
    }

    // Get recent logs (last 24 hours, 7 days, etc.)
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentLogs(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "24") int hours,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            LocalDateTime since = LocalDateTime.now().minusHours(hours);
            Page<Logs> logs = logsService.getRecentLogs(since, page, size);
            
            Map<String, Object> response = createPaginatedResponse(logs);
            response.put("timeframe", hours + " hours");
            response.put("since", since.toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch recent logs: " + e.getMessage()));
        }
    }

    // Get logs statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getLogsStatistics(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "30") int days) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            Map<String, Object> stats = logsService.getLogsStatistics(days);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch statistics: " + e.getMessage()));
        }
    }

    // Export logs to Excel
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportLogsToExcel(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String performedBy,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            LocalDateTime fromDateTime = null;
            LocalDateTime toDateTime = null;
            
            if (dateFrom != null && !dateFrom.isEmpty()) {
                fromDateTime = LocalDate.parse(dateFrom, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            }
            if (dateTo != null && !dateTo.isEmpty()) {
                toDateTime = LocalDate.parse(dateTo, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);
            }
            
            byte[] excelData = logsService.exportLogsToExcel(action, performedBy, entityName, fromDateTime, toDateTime);
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=logs_report.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Save a log manually (For testing) - Enhanced with validation
    @PostMapping("/save")
    public ResponseEntity<?> saveLog(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Logs log) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            // Validate required fields
            if (log.getAction() == null || log.getAction().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Action is required"));
            }
            
            if (log.getPerformedBy() == null || log.getPerformedBy().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "PerformedBy is required"));
            }
            
            log.setTimestamp(LocalDateTime.now());
            logsService.saveLog(log.getAction(), log.getPerformedBy(), 
                log.getEntityName(), log.getEntityId(), log.getDetails());
            
            return ResponseEntity.ok(Map.of("message", "Log saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to save log: " + e.getMessage()));
        }
    }

    // Get log by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getLogById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            Optional<Logs> log = logsService.getLogById(id);
            if (log.isPresent()) {
                return ResponseEntity.ok(log.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch log: " + e.getMessage()));
        }
    }

    // Get all unique actions for filtering dropdown
    @GetMapping("/actions/unique")
    public ResponseEntity<?> getUniqueActions(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            List<String> uniqueActions = logsService.getUniqueActions();
            return ResponseEntity.ok(Map.of("actions", uniqueActions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch unique actions: " + e.getMessage()));
        }
    }

    // Get all unique entity names for filtering dropdown
    @GetMapping("/entities/unique")
    public ResponseEntity<?> getUniqueEntities(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            List<String> uniqueEntities = logsService.getUniqueEntities();
            return ResponseEntity.ok(Map.of("entities", uniqueEntities));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch unique entities: " + e.getMessage()));
        }
    }

    // Get all unique users for filtering dropdown
    @GetMapping("/users/unique")
    public ResponseEntity<?> getUniqueUsers(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorizedAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized access"));
        }
        
        try {
            List<String> uniqueUsers = logsService.getUniqueUsers();
            return ResponseEntity.ok(Map.of("users", uniqueUsers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch unique users: " + e.getMessage()));
        }
    }

    // Helper method to create standardized paginated response
    private Map<String, Object> createPaginatedResponse(Page<Logs> logs) {
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logs.getContent());
        response.put("totalElements", logs.getTotalElements());
        response.put("totalPages", logs.getTotalPages());
        response.put("currentPage", logs.getNumber());
        response.put("pageSize", logs.getSize());
        response.put("hasNext", logs.hasNext());
        response.put("hasPrevious", logs.hasPrevious());
        return response;
    }
}
