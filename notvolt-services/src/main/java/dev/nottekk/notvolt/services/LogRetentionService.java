package dev.nottekk.notvolt.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.Instant;

public class LogRetentionService {
	private static final Logger log = LoggerFactory.getLogger(LogRetentionService.class);
	private final FeatureGateService featureGateService;

	public LogRetentionService(FeatureGateService featureGateService) {
		this.featureGateService = featureGateService;
	}

	@Scheduled(fixedDelay = 86_400_000L)
	public void cleanup() {
		// In MVP, we log intent; real impl would delete old log files by tier from disk or logging backend
		log.info("retention.cleanup.start");
		// TODO: delete logs older than tier policy
	}
}
