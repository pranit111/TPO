package com.example.TPO.Logs.LogsService;

import com.example.TPO.DBMS.Logs.Logs;
import com.example.TPO.Logs.LogsRepository.LogsRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogsService {
    @Autowired
    LogsRepository logsRepository;
    
    // Original methods
    public List<Logs> getAllLogs() {
        return logsRepository.findAll();
    }

    public void saveLog(String action, String performedBy, String entityName, String entityId, String details) {
        Logs log = new Logs();
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());

        logsRepository.save(log);
    }

    public List<Logs> searchLogs(String action, String performedBy, String entityName, String entityId, String details) {
        return logsRepository.searchLogs(action, performedBy, entityName, entityId, details);
    }

    // New enhanced methods
    public Page<Logs> getAllLogsPaginated(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return logsRepository.findAll(pageable);
    }

    public Page<Logs> searchLogsAdvanced(String action, String performedBy, String entityName, 
                                       String entityId, String details, LocalDateTime fromDateTime, 
                                       LocalDateTime toDateTime, int page, int size, 
                                       String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return logsRepository.searchLogsAdvanced(action, performedBy, entityName, entityId, 
                                               details, fromDateTime, toDateTime, pageable);
    }

    public Page<Logs> filterByAction(String action, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return logsRepository.findByActionContainingIgnoreCase(action, pageable);
    }

    public Page<Logs> filterByEntity(String entityName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return logsRepository.findByEntityNameContainingIgnoreCase(entityName, pageable);
    }

    public Page<Logs> filterByUser(String performedBy, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return logsRepository.findByPerformedByContainingIgnoreCase(performedBy, pageable);
    }

    public Page<Logs> filterByDateRange(LocalDateTime fromDateTime, LocalDateTime toDateTime, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return logsRepository.findByTimestampBetween(fromDateTime, toDateTime, pageable);
    }

    public Page<Logs> getRecentLogs(LocalDateTime since, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return logsRepository.findByTimestampAfter(since, pageable);
    }

    public Optional<Logs> getLogById(Long id) {
        return logsRepository.findById(id);
    }

    public List<String> getUniqueActions() {
        return logsRepository.findDistinctActions();
    }

    public List<String> getUniqueEntities() {
        return logsRepository.findDistinctEntityNames();
    }

    public List<String> getUniqueUsers() {
        return logsRepository.findDistinctPerformedBy();
    }

    public Map<String, Object> getLogsStatistics(int days) {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        
        List<Logs> recentLogs = logsRepository.findByTimestampAfter(since);
        
        // Total logs in period
        stats.put("totalLogs", recentLogs.size());
        
        // Action statistics
        Map<String, Long> actionStats = recentLogs.stream()
            .collect(Collectors.groupingBy(Logs::getAction, Collectors.counting()));
        stats.put("actionBreakdown", actionStats);
        
        // Entity statistics
        Map<String, Long> entityStats = recentLogs.stream()
            .filter(log -> log.getEntityName() != null)
            .collect(Collectors.groupingBy(Logs::getEntityName, Collectors.counting()));
        stats.put("entityBreakdown", entityStats);
        
        // User activity statistics
        Map<String, Long> userStats = recentLogs.stream()
            .collect(Collectors.groupingBy(Logs::getPerformedBy, Collectors.counting()));
        stats.put("userActivity", userStats);
        
        // Daily activity (last 7 days)
        Map<String, Long> dailyActivity = new HashMap<>();
        for (int i = 0; i < Math.min(days, 7); i++) {
            LocalDateTime dayStart = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = dayStart.plusDays(1);
            
            long dayCount = recentLogs.stream()
                .filter(log -> log.getTimestamp().isAfter(dayStart) && log.getTimestamp().isBefore(dayEnd))
                .count();
            
            dailyActivity.put(dayStart.format(DateTimeFormatter.ISO_LOCAL_DATE), dayCount);
        }
        stats.put("dailyActivity", dailyActivity);
        
        // Most active users (top 10)
        List<Map<String, Object>> topUsers = userStats.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .map(entry -> {
                Map<String, Object> user = new HashMap<>();
                user.put("user", entry.getKey());
                user.put("count", entry.getValue());
                return user;
            })
            .collect(Collectors.toList());
        stats.put("topUsers", topUsers);
        
        // Most common actions (top 10)
        List<Map<String, Object>> topActions = actionStats.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .map(entry -> {
                Map<String, Object> action = new HashMap<>();
                action.put("action", entry.getKey());
                action.put("count", entry.getValue());
                return action;
            })
            .collect(Collectors.toList());
        stats.put("topActions", topActions);
        
        stats.put("period", days + " days");
        stats.put("since", since.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return stats;
    }

    public byte[] exportLogsToExcel(String action, String performedBy, String entityName, 
                                   LocalDateTime fromDateTime, LocalDateTime toDateTime) throws IOException {
        List<Logs> logs;
        
        if (action != null || performedBy != null || entityName != null || fromDateTime != null || toDateTime != null) {
            logs = logsRepository.searchLogsForExport(action, performedBy, entityName, fromDateTime, toDateTime, 
                                                    Sort.by("timestamp").descending());
        } else {
            logs = logsRepository.findAll(Sort.by("timestamp").descending());
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Logs Report");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Action", "Performed By", "Entity Name", "Entity ID", "Details", "Timestamp"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            for (Logs log : logs) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(log.getId() != null ? log.getId() : 0);
                row.createCell(1).setCellValue(log.getAction() != null ? log.getAction() : "");
                row.createCell(2).setCellValue(log.getPerformedBy() != null ? log.getPerformedBy() : "");
                row.createCell(3).setCellValue(log.getEntityName() != null ? log.getEntityName() : "");
                row.createCell(4).setCellValue(log.getEntityId() != null ? log.getEntityId() : "");
                row.createCell(5).setCellValue(log.getDetails() != null ? log.getDetails() : "");
                row.createCell(6).setCellValue(log.getTimestamp() != null ? 
                    log.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
