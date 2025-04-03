package com.example.TPO.Logs.LogsService;

import com.example.TPO.DBMS.Logs.Logs;
import com.example.TPO.Logs.LogsRepository.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogsService {
    @Autowired
    LogsRepository logsRepository;
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
}
