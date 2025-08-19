package dev.nottekk.notvolt.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nottekk.notvolt.persistence.entity.CaseEntity;
import dev.nottekk.notvolt.persistence.entity.UserTokenEntity;
import dev.nottekk.notvolt.persistence.repo.CaseRepository;
import dev.nottekk.notvolt.persistence.repo.UserTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/privacy")
public class PrivacyController {
	private final UserTokenRepository tokenRepo;
	private final CaseRepository caseRepo;
	private final ObjectMapper mapper = new ObjectMapper();

	public PrivacyController(UserTokenRepository tokenRepo, CaseRepository caseRepo) {
		this.tokenRepo = tokenRepo;
		this.caseRepo = caseRepo;
	}

	@GetMapping("/export")
	public ResponseEntity<?> export(@AuthenticationPrincipal OAuth2User user) {
		if (user == null) return ResponseEntity.status(401).build();
		String id = user.getAttribute("id");
		Map<String, Object> data = new HashMap<>();
		UserTokenEntity token = tokenRepo.findById(id).orElse(null);
		data.put("userId", id);
		data.put("tokensPresent", token != null);
		// Cases involving this user as target or actor
		List<CaseEntity> cases = caseRepo.findAll().stream().filter(c -> id.equals(c.getActorDiscordId()) || id.equals(c.getTargetDiscordId())).toList();
		data.put("cases", cases);
		return ResponseEntity.ok(data);
	}

	@PostMapping("/delete")
	public ResponseEntity<?> delete(@AuthenticationPrincipal OAuth2User user) {
		if (user == null) return ResponseEntity.status(401).build();
		String id = user.getAttribute("id");
		// MVP: delete tokens and redact cases actor/target with a placeholder while keeping integrity
		tokenRepo.deleteById(id);
		List<CaseEntity> cases = caseRepo.findAll().stream().filter(c -> id.equals(c.getActorDiscordId()) || id.equals(c.getTargetDiscordId())).toList();
		for (CaseEntity c : cases) {
			if (id.equals(c.getActorDiscordId())) c.setActorDiscordId("redacted");
			if (id.equals(c.getTargetDiscordId())) c.setTargetDiscordId("redacted");
		}
		caseRepo.saveAll(cases);
		return ResponseEntity.accepted().body(Map.of("status", "queued"));
	}
}
