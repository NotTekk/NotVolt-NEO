package dev.nottekk.notvolt.services.playbook;

import java.util.Map;

public class Playbook {
	public enum Type { RAID_SHIELD, SCAM_SWEEP, DRAMA_COOLDOWN }
	private final Type type;
	private final Map<String, String> channelSettings; // channelId -> setting (e.g., slowmode=10, lock=true)

	public Playbook(Type type, Map<String, String> channelSettings) {
		this.type = type;
		this.channelSettings = channelSettings;
	}
	public Type getType() { return type; }
	public Map<String, String> getChannelSettings() { return channelSettings; }
}
