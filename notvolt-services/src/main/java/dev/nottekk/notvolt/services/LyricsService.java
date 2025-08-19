package dev.nottekk.notvolt.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LyricsService {
	private final OkHttpClient http = new OkHttpClient();
	private final String apiKey;

	public LyricsService(String apiKey) {
		this.apiKey = apiKey;
	}

	public String findLyrics(String query) {
		try {
			// TODO: replace with a reliable public lyrics API; placeholder endpoint
			String url = "https://api.lyrics.ovh/v1/" + java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
			Request req = new Request.Builder().url(url).header("X-API-Key", apiKey == null ? "" : apiKey).build();
			try (Response resp = http.newCall(req).execute()) {
				if (resp.isSuccessful() && resp.body() != null) {
					String body = resp.body().string();
					return body.length() > 1800 ? body.substring(0, 1800) + "..." : body;
				}
			}
		} catch (Exception ignored) {}
		return "not found";
	}
}
