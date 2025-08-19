package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "discord_id", nullable = false, unique = true, length = 32)
	private String discordId;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	public Long getId() { return id; }
	public String getDiscordId() { return discordId; }
	public void setDiscordId(String discordId) { this.discordId = discordId; }
	public String getDisplayName() { return displayName; }
	public void setDisplayName(String displayName) { this.displayName = displayName; }
	public OffsetDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
