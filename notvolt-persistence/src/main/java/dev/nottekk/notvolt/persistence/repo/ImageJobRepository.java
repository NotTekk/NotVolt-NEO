package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.ImageJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageJobRepository extends JpaRepository<ImageJobEntity, Long> {
	List<ImageJobEntity> findByGuildIdOrderByPriorityDescCreatedAtAsc(String guildId);
}
