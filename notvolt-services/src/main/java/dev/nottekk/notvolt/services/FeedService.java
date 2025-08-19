package dev.nottekk.notvolt.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nottekk.notvolt.persistence.entity.FeedEntity;
import dev.nottekk.notvolt.persistence.repo.FeedRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.OffsetDateTime;
import java.util.List;

public class FeedService {
	private final FeedRepository repo;
	private final PostGateway postGateway;
	private final OkHttpClient http = new OkHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();

	public FeedService(FeedRepository repo, PostGateway postGateway) {
		this.repo = repo;
		this.postGateway = postGateway;
	}

	public FeedEntity add(String guildId, FeedEntity.Type type, String url, String postChannelId, String rulesJson) {
		FeedEntity e = new FeedEntity();
		e.setGuildId(guildId);
		e.setType(type);
		e.setUrl(url);
		e.setPostChannelId(postChannelId);
		e.setRulesJson(rulesJson);
		e.setStatus("active");
		e.setCreatedAt(OffsetDateTime.now());
		return repo.save(e);
	}

	public void remove(long id) { repo.deleteById(id); }
	public List<FeedEntity> list(String guildId) { return repo.findByGuildId(guildId); }

	@Scheduled(fixedDelay = 120_000L, initialDelay = 10_000L)
	public void scanAll() {
		for (FeedEntity f : repo.findAll()) {
			try { scan(f); } catch (Exception ignored) {}
		}
	}

	void scan(FeedEntity f) throws Exception {
		String latestId = switch (f.getType()) {
			case RSS -> fetchRssLatestId(f.getUrl());
			case REDDIT -> fetchRedditLatestId(f.getUrl());
			case YT -> fetchYoutubeLatestId(f.getUrl());
			case TWITCH -> fetchTwitchLatestId(f.getUrl());
		};
		if (latestId == null || latestId.equals(f.getLastItemId())) return;
		f.setLastItemId(latestId);
		repo.save(f);
		postGateway.post(f.getGuildId(), f.getPostChannelId(), "New item: " + latestId + "\n" + f.getUrl());
	}

	private String fetchRssLatestId(String url) throws Exception {
		Request req = new Request.Builder().url(url).build();
		try (Response resp = http.newCall(req).execute()) {
			if (!resp.isSuccessful() || resp.body() == null) return null;
			String xml = resp.body().string();
			int i = xml.indexOf("<item>");
			if (i < 0) return null;
			int linkStart = xml.indexOf("<link>", i);
			int linkEnd = xml.indexOf("</link>", linkStart);
			if (linkStart < 0 || linkEnd < 0) return null;
			return xml.substring(linkStart + 6, linkEnd).trim();
		}
	}

	private String fetchRedditLatestId(String url) throws Exception {
		Request req = new Request.Builder().url(url).build();
		try (Response resp = http.newCall(req).execute()) {
			if (!resp.isSuccessful() || resp.body() == null) return null;
			JsonNode n = mapper.readTree(resp.body().string());
			if (n.has("data") && n.get("data").has("children") && n.get("data").get("children").isArray() && n.get("data").get("children").size() > 0) {
				return n.get("data").get("children").get(0).get("data").get("id").asText();
			}
			return null;
		}
	}

	private String fetchYoutubeLatestId(String url) {
		String key = System.getenv("GOOGLE_CSE_KEY");
		if (key == null || key.isBlank()) return null; // skip gracefully
		// TODO: implement YouTube Data API poll (channels or playlist)
		return null;
	}

	private String fetchTwitchLatestId(String url) {
		// TODO: implement Twitch API poll or webhook; skip if creds missing
		return null;
	}
}
