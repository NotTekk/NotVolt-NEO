package dev.nottekk.notvolt.services.automod;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutomodRuleRepository {
	private final ObjectMapper mapper = new ObjectMapper();
	private final Path storageDir;
	private final Map<String, List<AutomodRule>> cache = new ConcurrentHashMap<>();

	public AutomodRuleRepository(Path storageDir) {
		this.storageDir = storageDir;
	}

	public synchronized List<AutomodRule> findAll(String guildId) {
		return new ArrayList<>(load(guildId));
	}

	public synchronized void saveAll(String guildId, List<AutomodRule> rules) {
		cache.put(guildId, new ArrayList<>(rules));
		persist(guildId);
	}

	public synchronized void add(String guildId, AutomodRule rule) {
		List<AutomodRule> rules = load(guildId);
		rules.add(rule);
		cache.put(guildId, rules);
		persist(guildId);
	}

	public synchronized void remove(String guildId, int index) {
		List<AutomodRule> rules = load(guildId);
		if (index >= 0 && index < rules.size()) {
			rules.remove(index);
			persist(guildId);
		}
	}

	private List<AutomodRule> load(String guildId) {
		if (cache.containsKey(guildId)) return cache.get(guildId);
		try {
			Files.createDirectories(storageDir);
			Path file = storageDir.resolve(guildId + "-automod.json");
			if (Files.exists(file)) {
				List<AutomodRule> rules = mapper.readValue(Files.readString(file), new TypeReference<List<AutomodRule>>() {});
				cache.put(guildId, new ArrayList<>(rules));
				return rules;
			}
		} catch (IOException ignored) {}
		cache.put(guildId, new ArrayList<>());
		return cache.get(guildId);
	}

	private void persist(String guildId) {
		try {
			Files.createDirectories(storageDir);
			Path file = storageDir.resolve(guildId + "-automod.json");
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cache.get(guildId));
			Files.writeString(file, json);
		} catch (IOException ignored) {}
	}
}
