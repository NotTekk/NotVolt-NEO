package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.PollEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<PollEntity, Long> {
}
