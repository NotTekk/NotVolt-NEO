package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.GuildEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuildRepository extends JpaRepository<GuildEntity, Long> {
	Optional<GuildEntity> findByGuildId(String guildId);
}
