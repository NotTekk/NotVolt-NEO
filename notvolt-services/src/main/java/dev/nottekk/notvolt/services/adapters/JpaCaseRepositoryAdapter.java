package dev.nottekk.notvolt.services.adapters;

import dev.nottekk.notvolt.persistence.entity.CaseEntity;
import dev.nottekk.notvolt.persistence.repo.CaseRepository;
import dev.nottekk.notvolt.services.CaseRepositoryPort;

import java.util.List;
import java.util.Optional;

public class JpaCaseRepositoryAdapter implements CaseRepositoryPort {
	private final CaseRepository repo;
	public JpaCaseRepositoryAdapter(CaseRepository repo) { this.repo = repo; }
	@Override public CaseEntity save(CaseEntity entity) { return repo.save(entity); }
	@Override public Optional<CaseEntity> findById(long id) { return repo.findById(id); }
	@Override public List<CaseEntity> findByGuildIdOrderByIdDesc(String guildId) { return repo.findByGuildIdOrderByIdDesc(guildId); }
}
