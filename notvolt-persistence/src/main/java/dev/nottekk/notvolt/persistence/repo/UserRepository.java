package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByDiscordId(String discordId);
}
