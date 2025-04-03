package com.example.TPO.Logs.LogsController;

import com.example.TPO.DBMS.Logs.Logs;

import com.example.TPO.Logs.LogsService.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LogsController {

@Autowired
    LogsService logsService;
    @GetMapping
    public ResponseEntity<List<Logs>> getAllLogs() {
        return ResponseEntity.ok( logsService.getAllLogs());
    }

    // Save a log manually (For testing)
    @PostMapping("/save")
    public String saveLog(@RequestBody Logs log) {

        return "Log saved successfully!";
    }
    @GetMapping("/search")
    public ResponseEntity<List<Logs>> searchLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String performedBy,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String details
    ) {
        return ResponseEntity.ok(logsService.searchLogs(action, performedBy, entityName, entityId, details));
    }
}
