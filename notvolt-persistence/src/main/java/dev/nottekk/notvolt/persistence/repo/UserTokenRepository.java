package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.UserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenRepository extends JpaRepository<UserTokenEntity, String> {
}
