package dev.nottekk.notvolt.integrations.nsfw;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Rule34Client {
	private final OkHttpClient http = new OkHttpClient();
	private final String userAgent;

	public Rule34Client(String userAgent) {
		this.userAgent = userAgent == null || userAgent.isBlank() ? "NotVolt/1.0 (+https://nottekk.dev)" : userAgent;
	}

	public String search(String tags) {
		try {
			String url = "https://api.rule34.xxx/index.php?page=dapi&s=post&q=index&json=1&limit=1&tags=" + java.net.URLEncoder.encode(tags, java.nio.charset.StandardCharsets.UTF_8);
			Request req = new Request.Builder().url(url).header("User-Agent", userAgent).build();
			try (Response resp = http.newCall(req).execute()) {
				if (resp.isSuccessful() && resp.body() != null) return resp.body().string();
			}
		} catch (Exception ignored) {}
		return null;
	}
}
