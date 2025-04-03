package com.example.TPO.Logs.LogsRepository;

import com.example.TPO.DBMS.Logs.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogsRepository extends JpaRepository<Logs,Long> {
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
}
