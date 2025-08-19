package dev.nottekk.notvolt.services;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CreditsService {
	private final FeatureGateService featureGateService;
	private final Map<String, Integer> fallback = new ConcurrentHashMap<>();

	public CreditsService(FeatureGateService featureGateService) {
		this.featureGateService = featureGateService;
	}

	public int dailyAllowance(String guildId) {
		return switch (featureGateService.getTier(guildId)) {
			case FREE -> 20;
			case PREMIUM -> 200;
			case PREMIUM_PLUS -> 1000;
		};
	}

	private String key(String guildId) { return "credits:" + guildId + ":" + LocalDate.now(); }

	public synchronized int getRemaining(String guildId) {
		return fallback.computeIfAbsent(key(guildId), k -> dailyAllowance(guildId));
	}

	public synchronized boolean tryDeduct(String guildId, int cost) {
		int remain = getRemaining(guildId);
		if (remain < cost) return false;
		fallback.put(key(guildId), remain - cost);
		return true;
	}
}
