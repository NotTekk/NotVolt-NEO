package dev.nottekk.notvolt.music;

import dev.nottekk.notvolt.services.FeatureGateService;
import net.dv8tion.jda.api.JDA;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MusicGateway {
	private final JDA jda;
	private final FeatureGateService featureGateService;
	private final Map<String, Deque<Track>> queues = new ConcurrentHashMap<>();
	private final Map<String, Boolean> paused = new ConcurrentHashMap<>();
	private final Map<String, Boolean> loopTrack = new ConcurrentHashMap<>();
	private final Map<String, Boolean> loopQueue = new ConcurrentHashMap<>();

	// TODO: integrate a Lavalink Java client; placeholders below
	public MusicGateway(JDA jda, FeatureGateService featureGateService) {
		this.jda = jda;
		this.featureGateService = featureGateService;
	}

	public void connect() {
		String host = System.getenv("LAVALINK_HOST");
		String port = System.getenv("LAVALINK_PORT");
		String password = System.getenv("LAVALINK_PASSWORD");
		// TODO: use lavalink client to connect using above credentials
	}

	public Deque<Track> getQueue(String guildId) {
		return queues.computeIfAbsent(guildId, g -> new ArrayDeque<>());
	}

	public void enqueue(String guildId, Track track) {
		getQueue(guildId).addLast(track);
		// TODO: if not playing, start playback via lavalink
	}

	public Optional<Track> skip(String guildId) {
		Deque<Track> q = getQueue(guildId);
		if (q.isEmpty()) return Optional.empty();
		Track skipped = q.pollFirst();
		// TODO: instruct lavalink to play next
		return Optional.ofNullable(skipped);
	}

	public void pause(String guildId) { paused.put(guildId, true); /* TODO: lavalink pause */ }
	public void resume(String guildId) { paused.put(guildId, false); /* TODO: lavalink resume */ }

	public boolean remove(String guildId, int index) {
		Deque<Track> q = getQueue(guildId);
		if (index < 0 || index >= q.size()) return false;
		List<Track> list = new ArrayList<>(q);
		Track rem = list.remove(index);
		q.clear(); q.addAll(list);
		return rem != null;
	}

	public boolean move(String guildId, int from, int to) {
		Deque<Track> q = getQueue(guildId);
		List<Track> list = new ArrayList<>(q);
		if (from < 0 || from >= list.size() || to < 0 || to >= list.size()) return false;
		Track t = list.remove(from);
		list.add(to, t);
		q.clear(); q.addAll(list);
		return true;
	}

	public void shuffle(String guildId) {
		Deque<Track> q = getQueue(guildId);
		List<Track> list = new ArrayList<>(q);
		Collections.shuffle(list);
		q.clear(); q.addAll(list);
	}

	public void setLoopTrack(String guildId, boolean enabled) { loopTrack.put(guildId, enabled); }
	public void setLoopQueue(String guildId, boolean enabled) { loopQueue.put(guildId, enabled); }
	public boolean isLoopTrack(String guildId) { return loopTrack.getOrDefault(guildId, false); }
	public boolean isLoopQueue(String guildId) { return loopQueue.getOrDefault(guildId, false); }

	// Premium-gated filters
	public boolean applyBassBoost(String guildId) { if (!featureGateService.isPremium(guildId)) return false; /* TODO lavalink filters */ return true; }
	public boolean applyNightcore(String guildId) { if (!featureGateService.isPremiumPlus(guildId)) return false; /* TODO lavalink filters */ return true; }
	public boolean applyEq(String guildId) { if (!featureGateService.isPremium(guildId)) return false; /* TODO lavalink filters */ return true; }
}
