package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "polls")
public class PollEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "guild_id", nullable = false, length = 32)
	private String guildId;

	@Column(name = "question", nullable = false)
	private String question;

	@Column(name = "options_json", columnDefinition = "TEXT")
	private String optionsJson;

	@Column(name = "closes_at")
	private OffsetDateTime closesAt;

	@Column(name = "created_by", length = 32)
	private String createdBy;

	public Long getId() { return id; }
	public String getGuildId() { return guildId; }
	public void setGuildId(String guildId) { this.guildId = guildId; }
	public String getQuestion() { return question; }
	public void setQuestion(String question) { this.question = question; }
	public String getOptionsJson() { return optionsJson; }
	public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }
	public OffsetDateTime getClosesAt() { return closesAt; }
	public void setClosesAt(OffsetDateTime closesAt) { this.closesAt = closesAt; }
	public String getCreatedBy() { return createdBy; }
	public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
