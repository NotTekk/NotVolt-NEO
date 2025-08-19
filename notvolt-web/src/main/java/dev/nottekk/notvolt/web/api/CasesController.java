package dev.nottekk.notvolt.web.api;

import dev.nottekk.notvolt.persistence.repo.CaseRepository;
import dev.nottekk.notvolt.web.security.RbacService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guilds/{guildId}")
public class CasesController {
	private final CaseRepository caseRepository;
	private final RbacService rbacService;

	public CasesController(CaseRepository caseRepository, RbacService rbacService) {
		this.caseRepository = caseRepository;
		this.rbacService = rbacService;
	}

	@GetMapping("/cases")
	public ResponseEntity<?> cases(@AuthenticationPrincipal OAuth2User user, @PathVariable String guildId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
		if (user == null) return ResponseEntity.status(401).build();
		String uid = user.getAttribute("id");
		if (!rbacService.isAdmin(uid)) return ResponseEntity.status(403).build();
		return ResponseEntity.ok(caseRepository.findByGuildIdOrderByIdDesc(guildId));
	}
}
