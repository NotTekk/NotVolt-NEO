package dev.nottekk.notvolt.web.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RbacService {
	private final Set<String> adminIds;
	private final DiscordApiClient discordApiClient;
	private final ObjectMapper mapper = new ObjectMapper();

	public RbacService(DiscordApiClient discordApiClient) {
		String env = System.getenv("NOTVOLT_ADMIN_IDS");
		this.adminIds = env == null || env.isBlank() ? Set.of() : Arrays.stream(env.split(",")).map(String::trim).collect(Collectors.toSet());
		this.discordApiClient = discordApiClient;
	}

	public boolean isAdmin(String discordId) { return adminIds.contains(discordId); }

	public boolean canManageGuild(String discordId, String guildId, String userAccessToken) {
		if (isAdmin(discordId)) return true;
		String json = discordApiClient.getUserGuilds(userAccessToken);
		if (json == null) return false;
		try {
			JsonNode arr = mapper.readTree(json);
			for (JsonNode g : arr) {
				if (guildId.equals(g.get("id").asText())) {
					boolean owner = g.get("owner").asBoolean(false);
					long perms = g.get("permissions").asLong(0L);
					boolean manageGuild = (perms & 0x20) != 0; // MANAGE_GUILD
					return owner || manageGuild;
				}
			}
		} catch (Exception ignored) {}
		return false;
	}
}
