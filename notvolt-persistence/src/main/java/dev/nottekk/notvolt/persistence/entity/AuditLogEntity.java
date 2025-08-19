package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "guild_id", nullable = false, length = 32)
	private String guildId;

	@Column(name = "actor_discord_id", nullable = false, length = 32)
	private String actorDiscordId;

	@Column(name = "action", nullable = false)
	private String action;

	@Column(name = "before_json", columnDefinition = "TEXT")
	private String beforeJson;

	@Column(name = "after_json", columnDefinition = "TEXT")
	private String afterJson;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	public Long getId() { return id; }
	public String getGuildId() { return guildId; }
	public void setGuildId(String guildId) { this.guildId = guildId; }
	public String getActorDiscordId() { return actorDiscordId; }
	public void setActorDiscordId(String actorDiscordId) { this.actorDiscordId = actorDiscordId; }
	public String getAction() { return action; }
	public void setAction(String action) { this.action = action; }
	public String getBeforeJson() { return beforeJson; }
	public void setBeforeJson(String beforeJson) { this.beforeJson = beforeJson; }
	public String getAfterJson() { return afterJson; }
	public void setAfterJson(String afterJson) { this.afterJson = afterJson; }
	public OffsetDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
