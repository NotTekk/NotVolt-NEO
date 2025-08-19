package dev.nottekk.notvolt.services;

import dev.nottekk.notvolt.persistence.entity.ReminderEntity;
import dev.nottekk.notvolt.persistence.repo.ReminderRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.OffsetDateTime;

public class ReminderService {
	private final ReminderRepository repo;
	private final PostGateway postGateway;

	public ReminderService(ReminderRepository repo, PostGateway postGateway) {
		this.repo = repo;
		this.postGateway = postGateway;
	}

	public ReminderEntity schedule(String guildId, String channelId, String userId, OffsetDateTime when, String message) {
		ReminderEntity r = new ReminderEntity();
		r.setGuildId(guildId);
		r.setChannelId(channelId);
		r.setUserId(userId);
		r.setDueAt(when);
		r.setMessage(message);
		r.setDone(false);
		return repo.save(r);
	}

	@Scheduled(fixedDelay = 60_000L)
	public void tick() {
		for (ReminderEntity r : repo.findByDoneFalseAndDueAtBefore(OffsetDateTime.now())) {
			postGateway.post(r.getGuildId(), r.getChannelId(), "Reminder for <@"+r.getUserId()+">: " + r.getMessage());
			r.setDone(true);
			repo.save(r);
		}
	}
}
