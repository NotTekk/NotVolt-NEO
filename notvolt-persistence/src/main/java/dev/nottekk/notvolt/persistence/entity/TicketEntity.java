package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tickets")
public class TicketEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "guild_id", nullable = false, length = 32)
	private String guildId;

	@Column(name = "opener_id", nullable = false, length = 32)
	private String openerId;

	@Column(name = "status", nullable = false)
	private String status; // OPEN, CLOSED

	@Column(name = "category")
	private String category;

	@Column(name = "transcript_url")
	private String transcriptUrl;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	@Column(name = "closed_at")
	private OffsetDateTime closedAt;

	public Long getId() { return id; }
	public String getGuildId() { return guildId; }
	public void setGuildId(String guildId) { this.guildId = guildId; }
	public String getOpenerId() { return openerId; }
	public void setOpenerId(String openerId) { this.openerId = openerId; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public String getTranscriptUrl() { return transcriptUrl; }
	public void setTranscriptUrl(String transcriptUrl) { this.transcriptUrl = transcriptUrl; }
	public OffsetDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
	public OffsetDateTime getClosedAt() { return closedAt; }
	public void setClosedAt(OffsetDateTime closedAt) { this.closedAt = closedAt; }
}
