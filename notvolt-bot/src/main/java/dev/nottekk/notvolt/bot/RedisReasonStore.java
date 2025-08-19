package dev.nottekk.notvolt.bot;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RedisReasonStore implements AutoCloseable {
	private final RedisClient client;
	private final StatefulRedisConnection<String, String> connection;
	private final ConcurrentHashMap<String, String> fallback = new ConcurrentHashMap<>();

	public RedisReasonStore(String redisUrl) {
		this.client = redisUrl != null && !redisUrl.isBlank() ? RedisClient.create(redisUrl) : null;
		this.connection = this.client != null ? this.client.connect() : null;
	}

	public void put(String userId, String messageId, String reason, Duration ttl) {
		String key = key(userId, messageId);
		if (connection != null) {
			RedisCommands<String, String> cmd = connection.sync();
			cmd.setex(key, ttl.toSeconds(), reason);
			cmd.setex(latestKey(userId), ttl.toSeconds(), userId + ":" + messageId);
		} else {
			fallback.put(latestKey(userId), key);
			fallback.put(key, reason);
		}
	}

	public Optional<String> getLatestReasonForUser(String userId) {
		if (connection != null) {
			RedisCommands<String, String> cmd = connection.sync();
			String latest = cmd.get(latestKey(userId));
			if (latest == null) return Optional.empty();
			String reason = cmd.get(latest);
			return Optional.ofNullable(reason);
		}
		String latest = fallback.get(latestKey(userId));
		return Optional.ofNullable(fallback.get(latest));
	}

	private static String key(String userId, String messageId) { return "why:" + userId + ":" + messageId; }
	private static String latestKey(String userId) { return "why:" + userId + ":latest"; }

	@Override
	public void close() {
		if (connection != null) connection.close();
		if (client != null) client.shutdown();
	}
}
