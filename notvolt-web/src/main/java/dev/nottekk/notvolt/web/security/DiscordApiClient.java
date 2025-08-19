package dev.nottekk.notvolt.web.security;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
public class DiscordApiClient {
	private final OkHttpClient http = new OkHttpClient();

	public String getUserGuilds(String accessToken) {
		try {
			Request req = new Request.Builder()
					.url("https://discord.com/api/users/@me/guilds")
					.header("Authorization", "Bearer " + accessToken)
					.build();
			try (Response resp = http.newCall(req).execute()) {
				if (resp.isSuccessful() && resp.body() != null) return resp.body().string();
			}
		} catch (Exception ignored) {}
		return null;
	}
}
