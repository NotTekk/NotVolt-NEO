package dev.nottekk.notvolt.services;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class GuildConfigService {
	public static final class GuildConfig {
		private final String guildId;
		private final Map<String, String> values;

		public GuildConfig(String guildId) {
			this.guildId = Objects.requireNonNull(guildId);
			this.values = new ConcurrentHashMap<>();
		}

		public String getGuildId() { return guildId; }
		public Map<String, String> getValues() { return values; }
	}

	private final Map<String, GuildConfig> storage = new ConcurrentHashMap<>();

	public GuildConfig getOrCreate(String guildId) {
		return storage.computeIfAbsent(guildId, GuildConfig::new);
	}

	public Optional<GuildConfig> find(String guildId) {
		return Optional.ofNullable(storage.get(guildId));
	}
}
