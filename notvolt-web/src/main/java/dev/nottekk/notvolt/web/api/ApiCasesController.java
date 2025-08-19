package dev.nottekk.notvolt.web.api;

import dev.nottekk.notvolt.persistence.repo.CaseRepository;
import dev.nottekk.notvolt.services.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiCasesController {
	private final CaseRepository caseRepository;
	private final RateLimiter rateLimiter = new RateLimiter(System.getenv("REDIS_URL"));

	public ApiCasesController(CaseRepository caseRepository) {
		this.caseRepository = caseRepository;
	}

	@GetMapping("/cases")
	public ResponseEntity<?> list(HttpServletRequest request, @RequestHeader(value = "X-Api-Key", required = false) String key) {
		Object scopes = request.getAttribute("apiScopes");
		Object guildId = request.getAttribute("apiGuildId");
		if (scopes == null || guildId == null) return ResponseEntity.status(401).build();
		if (!scopes.toString().contains("read")) return ResponseEntity.status(403).build();
		long retryAfter = rateLimiter.allow("api:" + guildId, 30, java.time.Duration.ofSeconds(10));
		if (retryAfter > 0) return ResponseEntity.status(429).header("Retry-After", Long.toString(retryAfter)).build();
		return ResponseEntity.ok(caseRepository.findByGuildIdOrderByIdDesc(guildId.toString()));
	}
}
