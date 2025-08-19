package dev.nottekk.notvolt.services;

import dev.nottekk.notvolt.persistence.entity.TicketEntity;
import dev.nottekk.notvolt.persistence.repo.TicketRepository;

import java.time.OffsetDateTime;

public class TicketService {
	private final TicketRepository repo;
	private final PostGateway postGateway;

	public TicketService(TicketRepository repo, PostGateway postGateway) {
		this.repo = repo;
		this.postGateway = postGateway;
	}

	public TicketEntity open(String guildId, String openerId, String category, String description, String notifyChannelId) {
		TicketEntity t = new TicketEntity();
		t.setGuildId(guildId);
		t.setOpenerId(openerId);
		t.setCategory(category);
		t.setStatus("OPEN");
		t.setCreatedAt(OffsetDateTime.now());
		TicketEntity saved = repo.save(t);
		if (notifyChannelId != null) {
			postGateway.post(guildId, notifyChannelId, "New report from <@"+openerId+"> (#"+saved.getId()+")\n"+description);
		}
		return saved;
	}

	public TicketEntity close(long id) {
		TicketEntity t = repo.findById(id).orElseThrow();
		t.setStatus("CLOSED");
		t.setClosedAt(OffsetDateTime.now());
		return repo.save(t);
	}

	public TicketEntity generateTranscript(long id) {
		TicketEntity t = repo.findById(id).orElseThrow();
		String url = "https://example.com/transcripts/" + id + ".html"; // TODO: real transcript
		t.setTranscriptUrl(url);
		return repo.save(t);
	}
}
