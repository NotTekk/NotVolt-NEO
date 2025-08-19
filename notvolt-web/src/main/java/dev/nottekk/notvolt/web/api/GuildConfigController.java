package dev.nottekk.notvolt.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nottekk.notvolt.persistence.entity.AuditLogEntity;
import dev.nottekk.notvolt.persistence.repo.AuditLogRepository;
import dev.nottekk.notvolt.services.GuildConfigService;
import dev.nottekk.notvolt.web.security.RbacService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/guilds/{guildId}")
public class GuildConfigController {
	private final GuildConfigService configService;
	private final RbacService rbacService;
	private final AuditLogRepository auditRepo;
	private final ObjectMapper mapper = new ObjectMapper();

	public GuildConfigController(GuildConfigService configService, RbacService rbacService, AuditLogRepository auditRepo) {
		this.configService = configService;
		this.rbacService = rbacService;
		this.auditRepo = auditRepo;
	}

	@GetMapping("/config")
	public ResponseEntity<?> get(@AuthenticationPrincipal OAuth2User user, @PathVariable String guildId) {
		if (user == null) return ResponseEntity.status(401).build();
		String uid = user.getAttribute("id");
		// TODO: load token and call Discord; for now, admin-only else forbidden
		if (!rbacService.isAdmin(uid)) return ResponseEntity.status(403).build();
		return ResponseEntity.ok(configService.getOrCreate(guildId).getValues());
	}

	@PutMapping("/config")
	public ResponseEntity<?> put(@AuthenticationPrincipal OAuth2User user, @PathVariable String guildId, @RequestBody Map<String, String> updates) {
		if (user == null) return ResponseEntity.status(401).build();
		String uid = user.getAttribute("id");
		if (!rbacService.isAdmin(uid)) return ResponseEntity.status(403).build();
		var cfg = configService.getOrCreate(guildId);
		Map<String, String> before = new java.util.HashMap<>(cfg.getValues());
		cfg.getValues().putAll(updates);
		// audit
		AuditLogEntity a = new AuditLogEntity();
		a.setGuildId(guildId);
		a.setActorDiscordId(uid);
		a.setAction("config.update");
		a.setBeforeJson(write(before));
		a.setAfterJson(write(cfg.getValues()));
		a.setCreatedAt(OffsetDateTime.now());
		auditRepo.save(a);
		return ResponseEntity.ok(cfg.getValues());
	}

	private String write(Object o) {
		try { return mapper.writeValueAsString(o); } catch (Exception e) { return "{}"; }
	}
}
