package com.example.library.service;

import com.example.library.mongo.AuditLog;
import com.example.library.mongo.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog log(String type, String user, String entityType, Long entityId, Map<String, Object> payload) {
        AuditLog log = new AuditLog();
        log.setTimestamp(Instant.now());
        log.setType(type);
        log.setUser(user);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPayload(payload);
        return auditLogRepository.save(log);
    }
}
