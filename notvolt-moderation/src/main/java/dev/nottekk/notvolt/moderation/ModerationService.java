package dev.nottekk.notvolt.moderation;

import dev.nottekk.notvolt.persistence.entity.CaseEntity;
import dev.nottekk.notvolt.services.CaseService;
import dev.nottekk.notvolt.services.ModLogService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;

public class ModerationService {
	private final CaseService caseService;
	private final ModLogService modLogService;
	private final JDA jda;

	public ModerationService(CaseService caseService, ModLogService modLogService, JDA jda) {
		this.caseService = caseService;
		this.modLogService = modLogService;
		this.jda = jda;
	}

	public CaseEntity ban(String guildId, String actorId, String targetId, String reason) {
		Guild guild = jda.getGuildById(guildId);
		if (guild != null) guild.ban(targetId, 0, reason).reason(reason).queue();
		CaseEntity created = caseService.create(guildId, actorId, targetId, "ban", reason);
		modLogService.log(guildId, created.getId(), "ban", actorId, targetId, reason);
		return created;
	}

	public CaseEntity kick(String guildId, String actorId, String targetId, String reason) {
		Guild guild = jda.getGuildById(guildId);
		Member m = guild != null ? guild.getMemberById(targetId) : null;
		if (m != null) guild.kick(m).reason(reason).queue();
		CaseEntity created = caseService.create(guildId, actorId, targetId, "kick", reason);
		modLogService.log(guildId, created.getId(), "kick", actorId, targetId, reason);
		return created;
	}

	public CaseEntity warn(String guildId, String actorId, String targetId, String reason) {
		CaseEntity created = caseService.create(guildId, actorId, targetId, "warn", reason);
		modLogService.log(guildId, created.getId(), "warn", actorId, targetId, reason);
		return created;
	}

	public CaseEntity timeout(String guildId, String actorId, String targetId, String reason, long seconds) {
		Guild guild = jda.getGuildById(guildId);
		Member m = guild != null ? guild.getMemberById(targetId) : null;
		if (m != null) guild.timeoutFor(m, java.time.Duration.ofSeconds(seconds)).reason(reason).queue();
		CaseEntity created = caseService.create(guildId, actorId, targetId, "timeout", reason);
		modLogService.log(guildId, created.getId(), "timeout", actorId, targetId, reason);
		return created;
	}

	public CaseEntity unban(String guildId, String actorId, String targetId, String reason) {
		Guild guild = jda.getGuildById(guildId);
		if (guild != null) guild.unban(targetId).reason(reason).queue();
		CaseEntity created = caseService.create(guildId, actorId, targetId, "unban", reason);
		modLogService.log(guildId, created.getId(), "unban", actorId, targetId, reason);
		return created;
	}

	public CaseEntity purge(String guildId, String actorId, String channelId, int count, String reason) {
		// For MVP: record only. Bot command will handle deletes.
		CaseEntity created = caseService.create(guildId, actorId, channelId, "purge", reason);
		modLogService.log(guildId, created.getId(), "purge", actorId, channelId, reason);
		return created;
	}

	public CaseEntity slowmode(String guildId, String actorId, String channelId, int seconds, String reason) {
		Guild guild = jda.getGuildById(guildId);
		if (guild != null && channelId != null) {
			var c = guild.getTextChannelById(channelId);
			if (c != null) c.getManager().setSlowmode(seconds).reason(reason).queue();
		}
		CaseEntity created = caseService.create(guildId, actorId, channelId, "slowmode", reason);
		modLogService.log(guildId, created.getId(), "slowmode", actorId, channelId, reason);
		return created;
	}

	public CaseEntity lock(String guildId, String actorId, String channelId, String reason) {
		Guild guild = jda.getGuildById(guildId);
		if (guild != null && channelId != null) {
			var c = guild.getTextChannelById(channelId);
			if (c != null) c.getManager().setLocked(true).reason(reason).queue();
		}
		CaseEntity created = caseService.create(guildId, actorId, channelId, "lock", reason);
		modLogService.log(guildId, created.getId(), "lock", actorId, channelId, reason);
		return created;
	}
}
