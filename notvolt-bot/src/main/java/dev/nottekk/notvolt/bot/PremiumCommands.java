package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.FeatureGateService;
import dev.nottekk.notvolt.services.PremiumService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PremiumCommands extends ListenerAdapter {
	private final PremiumService premiumService;
	private final FeatureGateService featureGateService;

	public PremiumCommands(PremiumService premiumService, FeatureGateService featureGateService) {
		this.premiumService = premiumService;
		this.featureGateService = featureGateService;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		if (!"premium".equals(event.getName())) return;
		String guildId = event.getGuild().getId();
		var tier = premiumService.getTierForGuild(guildId);
		String perks = switch (tier) {
			case FREE -> "Free: 2 feeds, basic features";
			case PREMIUM -> "Premium: 10 feeds, music filters, higher limits";
			case PREMIUM_PLUS -> "Pro: Unlimited feeds (fair use), advanced filters, extended analytics";
		};
		event.reply("Tier: " + tier + "\n" + perks + "\nUpgrade: https://patreon.com/").setEphemeral(true).queue();
	}
}
