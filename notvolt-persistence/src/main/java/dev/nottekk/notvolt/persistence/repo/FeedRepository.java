package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.FeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<FeedEntity, Long> {
	List<FeedEntity> findByGuildId(String guildId);
}
