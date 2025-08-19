package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_tokens")
public class UserTokenEntity {
	@Id
	@Column(name = "discord_id", length = 32)
	private String discordId;

	@Column(name = "enc_access_token", columnDefinition = "TEXT")
	private String encAccessToken;

	@Column(name = "enc_refresh_token", columnDefinition = "TEXT")
	private String encRefreshToken;

	@Column(name = "expires_at")
	private OffsetDateTime expiresAt;

	public String getDiscordId() { return discordId; }
	public void setDiscordId(String discordId) { this.discordId = discordId; }
	public String getEncAccessToken() { return encAccessToken; }
	public void setEncAccessToken(String encAccessToken) { this.encAccessToken = encAccessToken; }
	public String getEncRefreshToken() { return encRefreshToken; }
	public void setEncRefreshToken(String encRefreshToken) { this.encRefreshToken = encRefreshToken; }
	public OffsetDateTime getExpiresAt() { return expiresAt; }
	public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
}
