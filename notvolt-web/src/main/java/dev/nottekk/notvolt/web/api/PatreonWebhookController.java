package dev.nottekk.notvolt.web.api;

import dev.nottekk.notvolt.services.PatreonSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/patreon")
public class PatreonWebhookController {
	private final PatreonSyncService patreonSyncService;

	public PatreonWebhookController(PatreonSyncService patreonSyncService) {
		this.patreonSyncService = patreonSyncService;
	}

	@PostMapping("/webhook")
	public ResponseEntity<?> webhook(@RequestHeader(value = "X-Patreon-Signature", required = false) String signature, @RequestBody Map<String, Object> body) {
		// TODO: verify HMAC signature using PATREON_WEBHOOK_SECRET
		String userId = (String) body.getOrDefault("userId", "");
		String tier = (String) body.getOrDefault("tier", "FREE");
		if (userId == null || userId.isBlank()) return ResponseEntity.badRequest().build();
		patreonSyncService.applyPledge(userId, tier);
		return ResponseEntity.ok(Map.of("status", "ok"));
	}
}
