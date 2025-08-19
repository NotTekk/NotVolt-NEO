package dev.nottekk.notvolt.web.api;

import dev.nottekk.notvolt.services.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
	private final AnalyticsService analyticsService;

	public AnalyticsController(AnalyticsService analyticsService) {
		this.analyticsService = analyticsService;
	}

	@GetMapping("/summary")
	public ResponseEntity<?> summary(@AuthenticationPrincipal OAuth2User user) {
		if (user == null) return ResponseEntity.status(401).build();
		var s = analyticsService.summary();
		return ResponseEntity.ok(Map.of(
			"commandsUsed", s.commandsUsed(),
			"errors", s.errors(),
			"cases", s.cases()
		));
	}
}
