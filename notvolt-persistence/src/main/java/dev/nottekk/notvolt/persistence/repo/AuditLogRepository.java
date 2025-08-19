package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
}
