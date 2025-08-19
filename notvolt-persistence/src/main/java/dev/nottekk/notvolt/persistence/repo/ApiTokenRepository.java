package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.ApiTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiTokenRepository extends JpaRepository<ApiTokenEntity, Long> {
	Optional<ApiTokenEntity> findByHash(String hash);
}
