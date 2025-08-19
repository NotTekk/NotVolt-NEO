package dev.nottekk.notvolt.persistence.repo;

import dev.nottekk.notvolt.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
}
