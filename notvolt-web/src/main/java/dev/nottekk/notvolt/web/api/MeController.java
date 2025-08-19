package dev.nottekk.notvolt.web.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nottekk.notvolt.persistence.entity.UserTokenEntity;
import dev.nottekk.notvolt.persistence.repo.UserTokenRepository;
import dev.nottekk.notvolt.web.security.CryptoService;
import dev.nottekk.notvolt.web.security.DiscordApiClient;
import dev.nottekk.notvolt.web.security.RbacService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MeController {
	private final UserTokenRepository tokenRepo;
	private final CryptoService crypto;
	private final DiscordApiClient discordApiClient;
	private final RbacService rbacService;
	private final ObjectMapper mapper = new ObjectMapper();

	public MeController(UserTokenRepository tokenRepo, CryptoService crypto, DiscordApiClient discordApiClient, RbacService rbacService) {
		this.tokenRepo = tokenRepo;
		this.crypto = crypto;
		this.discordApiClient = discordApiClient;
		this.rbacService = rbacService;
	}

	@GetMapping("/me")
	public ResponseEntity<?> me(@AuthenticationPrincipal OAuth2User user) {
		if (user == null) return ResponseEntity.status(401).build();
		String id = user.getAttribute("id");
		String username = user.getAttribute("username");

		List<Map<String, Object>> manageable = new ArrayList<>();
		UserTokenEntity token = tokenRepo.findById(id).orElse(null);
		if (token != null) {
			String accessToken = crypto.decrypt(token.getEncAccessToken());
			String json = discordApiClient.getUserGuilds(accessToken);
			if (json != null) {
				try {
					JsonNode arr = mapper.readTree(json);
					for (JsonNode g : arr) {
						String gid = g.get("id").asText();
						boolean owner = g.get("owner").asBoolean(false);
						long perms = g.get("permissions").asLong(0L);
						boolean manageGuild = (perms & 0x20) != 0; // MANAGE_GUILD
						if (rbacService.isAdmin(id) || owner || manageGuild) {
							manageable.add(Map.of(
									"id", gid,
									"name", g.has("name") ? g.get("name").asText() : ""
							));
						}
					}
				} catch (Exception ignored) {}
			}
		}

		return ResponseEntity.ok(Map.of(
			"id", id,
			"username", username,
			"guilds", manageable
		));
	}
}
