package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "api_tokens")
public class ApiTokenEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "guild_id", length = 32)
	private String guildId;

	@Column(name = "name")
	private String name;

	@Column(name = "scopes")
	private String scopes; // comma-separated: read,write,manage

	@Column(name = "hash", nullable = false)
	private String hash;

	@Column(name = "allowed_ips")
	private String allowedIps; // comma or CIDR list

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	public Long getId() { return id; }
	public String getGuildId() { return guildId; }
	public void setGuildId(String guildId) { this.guildId = guildId; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getScopes() { return scopes; }
	public void setScopes(String scopes) { this.scopes = scopes; }
	public String getHash() { return hash; }
	public void setHash(String hash) { this.hash = hash; }
	public String getAllowedIps() { return allowedIps; }
	public void setAllowedIps(String allowedIps) { this.allowedIps = allowedIps; }
	public OffsetDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
