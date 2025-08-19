package dev.nottekk.notvolt.services;

import dev.nottekk.notvolt.persistence.entity.CaseEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class CaseService {
	private final CaseRepositoryPort caseRepository;

	public CaseService(CaseRepositoryPort caseRepository) {
		this.caseRepository = caseRepository;
	}

	public CaseEntity create(String guildId, String actorDiscordId, String targetDiscordId, String action, String reason) {
		CaseEntity e = new CaseEntity();
		e.setGuildId(guildId);
		e.setActorDiscordId(actorDiscordId);
		e.setTargetDiscordId(targetDiscordId);
		e.setAction(action);
		e.setReason(reason);
		e.setCreatedAt(OffsetDateTime.now());
		return caseRepository.save(e);
	}

	public Optional<CaseEntity> get(long id) {
		return caseRepository.findById(id);
	}

	public List<CaseEntity> list(String guildId) {
		return caseRepository.findByGuildIdOrderByIdDesc(guildId);
	}
}
