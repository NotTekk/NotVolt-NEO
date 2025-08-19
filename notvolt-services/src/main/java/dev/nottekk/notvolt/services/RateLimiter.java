package dev.nottekk.notvolt.services;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.time.Duration;

public class RateLimiter implements AutoCloseable {
	private final RedisClient client;
	private final StatefulRedisConnection<String, String> connection;

	public RateLimiter(String redisUrl) {
		this.client = redisUrl != null && !redisUrl.isBlank() ? RedisClient.create(redisUrl) : null;
		this.connection = this.client != null ? this.client.connect() : null;
	}

	public long allow(String bucketKey, int max, Duration window) {
		if (connection == null) return 0; // no limit if no redis
		RedisCommands<String, String> cmd = connection.sync();
		String key = "rl:" + bucketKey;
		Long count = cmd.incr(key);
		if (count != null && count == 1L) {
			cmd.expire(key, window.toSeconds());
		}
		long ttl = cmd.ttl(key);
		if (count != null && count > max) {
			return ttl > 0 ? ttl : window.toSeconds();
		}
		return 0;
	}

	public long allowUser(String userId) { return allow("user:"+userId, 5, Duration.ofSeconds(10)); }
	public long allowGuild(String guildId) { return allow("guild:"+guildId, 60, Duration.ofSeconds(10)); }
	public long allowFeature(String key, int max, Duration window) { return allow("feature:"+key, max, window); }

	@Override
	public void close() {
		if (connection != null) connection.close();
		if (client != null) client.shutdown();
	}
}
