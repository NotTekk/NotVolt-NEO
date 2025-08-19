package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "image_jobs")
public class ImageJobEntity {
	public enum Type { TEXT2IMG, UPSCALE, MEME }
	public enum Status { QUEUED, RUNNING, DONE, ERROR }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "guild_id", nullable = false, length = 32)
	private String guildId;

	@Column(name = "user_id", nullable = false, length = 32)
	private String userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 16)
	private Type type;

	@Column(name = "params_json", columnDefinition = "TEXT")
	private String paramsJson;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 16)
	private Status status;

	@Column(name = "cost", nullable = false)
	private int cost;

	@Column(name = "priority", nullable = false)
	private int priority;

	@Column(name = "output_url")
	private String outputUrl;

	@Column(name = "created_at")
	private OffsetDateTime createdAt;

	@Column(name = "completed_at")
	private OffsetDateTime completedAt;

	public Long getId() { return id; }
	public String getGuildId() { return guildId; }
	public void setGuildId(String guildId) { this.guildId = guildId; }
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	public Type getType() { return type; }
	public void setType(Type type) { this.type = type; }
	public String getParamsJson() { return paramsJson; }
	public void setParamsJson(String paramsJson) { this.paramsJson = paramsJson; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
	public int getCost() { return cost; }
	public void setCost(int cost) { this.cost = cost; }
	public int getPriority() { return priority; }
	public void setPriority(int priority) { this.priority = priority; }
	public String getOutputUrl() { return outputUrl; }
	public void setOutputUrl(String outputUrl) { this.outputUrl = outputUrl; }
	public OffsetDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
	public OffsetDateTime getCompletedAt() { return completedAt; }
	public void setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; }
}
