package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.CaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseRepository extends JpaRepository<CaseEntity, Long> {
	List<CaseEntity> findByGuildIdOrderByIdDesc(String guildId);
}
