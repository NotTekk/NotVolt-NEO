package dev.nottekk.notvolt.services;

import java.util.Map;

public class FeatureGateService {
	private final GuildConfigService guildConfigService;

	public FeatureGateService(GuildConfigService guildConfigService) {
		this.guildConfigService = guildConfigService;
	}

	public enum Tier { FREE, PREMIUM, PREMIUM_PLUS }

	public Tier getTier(String guildId) {
		Map<String, String> cfg = guildConfigService.getOrCreate(guildId).getValues();
		String tier = cfg.getOrDefault("billing.tier", "FREE");
		try { return Tier.valueOf(tier); } catch (Exception e) { return Tier.FREE; }
	}

	public boolean isPremium(String guildId) {
		Tier t = getTier(guildId); return t == Tier.PREMIUM || t == Tier.PREMIUM_PLUS;
	}

	public boolean isPremiumPlus(String guildId) {
		return getTier(guildId) == Tier.PREMIUM_PLUS;
	}
}
