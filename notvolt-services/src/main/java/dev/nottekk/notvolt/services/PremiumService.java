package dev.nottekk.notvolt.services;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.time.Duration;
import java.util.Optional;

public class PremiumService implements AutoCloseable {
	private final RedisClient client;
	private final StatefulRedisConnection<String, String> connection;
	private final FeatureGateService featureGateService;

	public PremiumService(FeatureGateService featureGateService, String redisUrl) {
		this.featureGateService = featureGateService;
		this.client = redisUrl != null && !redisUrl.isBlank() ? RedisClient.create(redisUrl) : null;
		this.connection = this.client != null ? this.client.connect() : null;
	}

	public FeatureGateService.Tier getTierForGuild(String guildId) {
		// First check cache
		FeatureGateService.Tier t = readTier("guild:"+guildId).orElse(null);
		if (t != null) return t;
		// Fallback to configured tier
		t = featureGateService.getTier(guildId);
		cacheTier("guild:"+guildId, t, Duration.ofMinutes(2));
		return t;
	}

	public FeatureGateService.Tier getTierForUser(String userId) {
		FeatureGateService.Tier t = readTier("user:"+userId).orElse(FeatureGateService.Tier.FREE);
		cacheTier("user:"+userId, t, Duration.ofMinutes(2));
		return t;
	}

	public void setTierForGuild(String guildId, FeatureGateService.Tier tier, Duration ttl) {
		cacheTier("guild:"+guildId, tier, ttl);
	}

	private Optional<FeatureGateService.Tier> readTier(String key) {
		if (connection == null) return Optional.empty();
		String v = connection.sync().get("premium:"+key);
		if (v == null) return Optional.empty();
		try { return Optional.of(FeatureGateService.Tier.valueOf(v)); } catch (Exception e) { return Optional.empty(); }
	}

	private void cacheTier(String key, FeatureGateService.Tier tier, Duration ttl) {
		if (connection == null) return;
		RedisCommands<String, String> cmd = connection.sync();
		cmd.setex("premium:"+key, ttl.toSeconds(), tier.name());
	}

	@Override
	public void close() {
		if (connection != null) connection.close();
		if (client != null) client.shutdown();
	}
}
