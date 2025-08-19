package dev.nottekk.notvolt.services;

import org.springframework.scheduling.annotation.Scheduled;

public class PatreonSyncService {
	private final PremiumService premiumService;

	public PatreonSyncService(PremiumService premiumService) {
		this.premiumService = premiumService;
	}

	public void applyPledge(String userId, String tierName) {
		FeatureGateService.Tier tier = mapTier(tierName);
		premiumService.setTierForGuild(userId, tier, java.time.Duration.ofMinutes(10));
	}

	@Scheduled(fixedDelay = 120_000L)
	public void scheduledSync() {
		// TODO: call Patreon API using client credentials if present and refresh cache
	}

	private FeatureGateService.Tier mapTier(String tierName) {
		String t = tierName == null ? "FREE" : tierName.toUpperCase();
		return switch (t) {
			case "PREMIUM" -> FeatureGateService.Tier.PREMIUM;
			case "PRO", "PREMIUM_PLUS" -> FeatureGateService.Tier.PREMIUM_PLUS;
			default -> FeatureGateService.Tier.FREE;
		};
	}
}
