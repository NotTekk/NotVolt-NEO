package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.ReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<ReminderEntity, Long> {
	List<ReminderEntity> findByDoneFalseAndDueAtBefore(OffsetDateTime cutoff);
}
