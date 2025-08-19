package dev.nottekk.notvolt.persistence.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reminders")
public class ReminderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "guild_id", length = 32)
	private String guildId;

	@Column(name = "user_id", length = 32)
	private String userId;

	@Column(name = "channel_id", length = 32)
	private String channelId;

	@Column(name = "message", columnDefinition = "TEXT")
	private String message;

	@Column(name = "due_at")
	private OffsetDateTime dueAt;

	@Column(name = "done")
	private boolean done;

	public Long getId() { return id; }
	public String getGuildId() { return guildId; }
	public void setGuildId(String guildId) { this.guildId = guildId; }
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	public String getChannelId() { return channelId; }
	public void setChannelId(String channelId) { this.channelId = channelId; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public OffsetDateTime getDueAt() { return dueAt; }
	public void setDueAt(OffsetDateTime dueAt) { this.dueAt = dueAt; }
	public boolean isDone() { return done; }
	public void setDone(boolean done) { this.done = done; }
}
