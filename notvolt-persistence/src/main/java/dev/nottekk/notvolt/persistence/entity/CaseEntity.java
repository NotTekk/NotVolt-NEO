package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "moderation_cases")
public class CaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "guild_id", nullable = false, length = 32)
	private String guildId;

	@Column(name = "user_discord_id", nullable = false, length = 32)
	private String targetDiscordId;

	@Column(name = "action", nullable = false, length = 32)
	private String action;

	@Column(name = "reason")
	private String reason;

	@Column(name = "actor_discord_id", nullable = false, length = 32)
	private String actorDiscordId;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	public Long getId() { return id; }
	public String getGuildId() { return guildId; }
	public void setGuildId(String guildId) { this.guildId = guildId; }
	public String getTargetDiscordId() { return targetDiscordId; }
	public void setTargetDiscordId(String targetDiscordId) { this.targetDiscordId = targetDiscordId; }
	public String getAction() { return action; }
	public void setAction(String action) { this.action = action; }
	public String getReason() { return reason; }
	public void setReason(String reason) { this.reason = reason; }
	public String getActorDiscordId() { return actorDiscordId; }
	public void setActorDiscordId(String actorDiscordId) { this.actorDiscordId = actorDiscordId; }
	public OffsetDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
