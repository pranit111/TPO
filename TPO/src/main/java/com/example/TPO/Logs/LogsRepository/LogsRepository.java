package com.example.TPO.Logs.LogsRepository;

import com.example.TPO.DBMS.Logs.Logs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LogsRepository extends JpaRepository<Logs,Long> {
    
    // Original search method
    @Query("SELECT l FROM Logs l WHERE " +
            "(:action IS NULL OR l.action LIKE %:action%) AND " +
            "(:performedBy IS NULL OR l.performedBy LIKE %:performedBy%) AND " +
            "(:entityName IS NULL OR l.entityName LIKE %:entityName%) AND " +
            "(:entityId IS NULL OR l.entityId LIKE %:entityId%) AND " +
            "(:details IS NULL OR l.details LIKE %:details%)")
    List<Logs> searchLogs(
            @Param("action") String action,
            @Param("performedBy") String performedBy,
            @Param("entityName") String entityName,
            @Param("entityId") String entityId,
            @Param("details") String details
    );

    // Advanced search with pagination and date filtering
    @Query("SELECT l FROM Logs l WHERE " +
            "(:action IS NULL OR l.action LIKE %:action%) AND " +
            "(:performedBy IS NULL OR l.performedBy LIKE %:performedBy%) AND " +
            "(:entityName IS NULL OR l.entityName LIKE %:entityName%) AND " +
            "(:entityId IS NULL OR l.entityId LIKE %:entityId%) AND " +
            "(:details IS NULL OR l.details LIKE %:details%) AND " +
            "(:fromDateTime IS NULL OR l.timestamp >= :fromDateTime) AND " +
            "(:toDateTime IS NULL OR l.timestamp <= :toDateTime)")
    Page<Logs> searchLogsAdvanced(
            @Param("action") String action,
            @Param("performedBy") String performedBy,
            @Param("entityName") String entityName,
            @Param("entityId") String entityId,
            @Param("details") String details,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            Pageable pageable
    );

    // Search logs for export (no pagination)
    @Query("SELECT l FROM Logs l WHERE " +
            "(:action IS NULL OR l.action LIKE %:action%) AND " +
            "(:performedBy IS NULL OR l.performedBy LIKE %:performedBy%) AND " +
            "(:entityName IS NULL OR l.entityName LIKE %:entityName%) AND " +
            "(:fromDateTime IS NULL OR l.timestamp >= :fromDateTime) AND " +
            "(:toDateTime IS NULL OR l.timestamp <= :toDateTime)")
    List<Logs> searchLogsForExport(
            @Param("action") String action,
            @Param("performedBy") String performedBy,
            @Param("entityName") String entityName,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            Sort sort
    );

    // Filter by action (case-insensitive)
    Page<Logs> findByActionContainingIgnoreCase(String action, Pageable pageable);

    // Filter by entity name (case-insensitive)
    Page<Logs> findByEntityNameContainingIgnoreCase(String entityName, Pageable pageable);

    // Filter by performed by (case-insensitive)
    Page<Logs> findByPerformedByContainingIgnoreCase(String performedBy, Pageable pageable);

    // Filter by timestamp range
    Page<Logs> findByTimestampBetween(LocalDateTime fromDateTime, LocalDateTime toDateTime, Pageable pageable);

    // Filter by timestamp after a certain date
    Page<Logs> findByTimestampAfter(LocalDateTime since, Pageable pageable);

    // Filter by timestamp after a certain date (without pagination)
    List<Logs> findByTimestampAfter(LocalDateTime since);

    // Get distinct actions
    @Query("SELECT DISTINCT l.action FROM Logs l WHERE l.action IS NOT NULL ORDER BY l.action")
    List<String> findDistinctActions();

    // Get distinct entity names
    @Query("SELECT DISTINCT l.entityName FROM Logs l WHERE l.entityName IS NOT NULL ORDER BY l.entityName")
    List<String> findDistinctEntityNames();

    // Get distinct users (performed by)
    @Query("SELECT DISTINCT l.performedBy FROM Logs l WHERE l.performedBy IS NOT NULL ORDER BY l.performedBy")
    List<String> findDistinctPerformedBy();

    // Get logs count by action
    @Query("SELECT l.action, COUNT(l) FROM Logs l WHERE l.timestamp >= :since GROUP BY l.action")
    List<Object[]> getActionCountsSince(@Param("since") LocalDateTime since);

    // Get logs count by entity
    @Query("SELECT l.entityName, COUNT(l) FROM Logs l WHERE l.entityName IS NOT NULL AND l.timestamp >= :since GROUP BY l.entityName")
    List<Object[]> getEntityCountsSince(@Param("since") LocalDateTime since);

    // Get logs count by user
    @Query("SELECT l.performedBy, COUNT(l) FROM Logs l WHERE l.timestamp >= :since GROUP BY l.performedBy")
    List<Object[]> getUserCountsSince(@Param("since") LocalDateTime since);

    // Get daily log counts
    @Query("SELECT DATE(l.timestamp), COUNT(l) FROM Logs l WHERE l.timestamp >= :since GROUP BY DATE(l.timestamp) ORDER BY DATE(l.timestamp)")
    List<Object[]> getDailyCountsSince(@Param("since") LocalDateTime since);
}
