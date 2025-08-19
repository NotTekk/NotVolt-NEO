package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "feeds")
public class FeedEntity {
	public enum Type { RSS, YT, TWITCH, REDDIT }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "guild_id", nullable = false, length = 32)
	private String guildId;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 16)
	private Type type;

	@Column(name = "url", nullable = false)
	private String url;

	@Column(name = "rules_json", columnDefinition = "TEXT")
	private String rulesJson;

	@Column(name = "post_channel_id", length = 32)
	private String postChannelId;

	@Column(name = "last_item_id")
	private String lastItemId;

	@Column(name = "status")
	private String status;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	public Long getId() { return id; }
	public String getGuildId() { return guildId; }
	public void setGuildId(String guildId) { this.guildId = guildId; }
	public Type getType() { return type; }
	public void setType(Type type) { this.type = type; }
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
	public String getRulesJson() { return rulesJson; }
	public void setRulesJson(String rulesJson) { this.rulesJson = rulesJson; }
	public String getPostChannelId() { return postChannelId; }
	public void setPostChannelId(String postChannelId) { this.postChannelId = postChannelId; }
	public String getLastItemId() { return lastItemId; }
	public void setLastItemId(String lastItemId) { this.lastItemId = lastItemId; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public OffsetDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
