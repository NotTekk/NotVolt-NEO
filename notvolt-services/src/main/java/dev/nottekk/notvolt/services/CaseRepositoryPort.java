package dev.nottekk.notvolt.services;

import dev.nottekk.notvolt.persistence.entity.CaseEntity;

import java.util.List;
import java.util.Optional;

public interface CaseRepositoryPort {
	CaseEntity save(CaseEntity entity);
	Optional<CaseEntity> findById(long id);
	List<CaseEntity> findByGuildIdOrderByIdDesc(String guildId);
}
